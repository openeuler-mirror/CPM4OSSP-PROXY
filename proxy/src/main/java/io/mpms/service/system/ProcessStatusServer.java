package io.jpom.service.system;


import cn.hutool.core.exceptions.ExceptionUtil;
import io.jpom.model.data.SystemCommanderResult;
import io.jpom.model.system.CpuMessage;
import io.jpom.model.system.CpuTime;
import io.jpom.model.system.ProcessModel;
import io.jpom.model.system.ProgramNet;
import io.jpom.util.CommandUtil;
import io.jpom.util.HostStatusUtil;
import io.jpom.util.UidUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Component
@Slf4j
public class ProcessStatusServer {
 /**
     * 获取proc文件信息
     */
    final static String path = "/proc";

    /**
     * 全部内存
     */
    private static String memoryTotal = "";

    /**
     * CPU核数
     */
    private static Integer cpuCount = 1;

    /**
     * 缓存uid及对应用户名
     */
    Map<String, String> searchUidByName = new HashMap<>();

    /**
     * 缓存ltcs pid
     */
    Map<String, String> ltcsPid = new HashMap<>();

    static Map<String, Integer> network = new HashMap<>();

    public List<ProcessModel> accept(boolean getALL) {

        List<ProcessModel> processList = new ArrayList<>();
        Map<String, ProcessModel> processMap = new HashMap<>();
        Map<String, CpuMessage> allCpu = new HashMap<>();
        Map<String, CpuMessage> allCpu2 = new HashMap<>();
        try {
            List<String> allProcess = new ArrayList<>();
            if (getALL) {
                //获取所有进程
                allProcess = getAllProcess();
            } else {
                //获取可信的进程
                if (ObjectUtils.isEmpty(ltcsPid)) {
                    ltcsPid = getLtcsPid();
                }
                allProcess.add(ltcsPid.get("server"));
                allProcess.add(ltcsPid.get("proxy"));
            }

            //获取总内存大小
            memoryTotal = getAllMemory();
            //获取CPU核心数
            cpuCount = getCpuCount();
            if (network.isEmpty()) {
                network = getNetwork();
            }
            for (String process : allProcess) {
                //打第一次CPU快照
                ProcessModel processObject = new ProcessModel();
                CpuMessage cpuMessage = new CpuMessage();
                allCpu.put(process, getCpuInfo(process, cpuMessage));
                Map<String, String> memoryMap = getProcesMemory(process);
                processObject.setMem(memoryMap.get("MEM"));
                processObject.setCommand(memoryMap.get("NAME"));
                processObject.setRes(memoryMap.get("RES"));
                processObject.setStatus(memoryMap.get("STATE"));
                processObject.setShr(memoryMap.get("VMSWAP"));
                processObject.setVirt(memoryMap.get("VMSIZE"));
                processObject.setUser(getProcesUser(process));
                processObject.setPid(Integer.parseInt(process));
                Integer port = network.get(process);
                if (ObjectUtils.isNotEmpty(port)){
                    processObject.setPort(String.valueOf(port));
                }
                processMap.put(process, processObject);
            }
            //睡眠线程 获取第二次CPU快照
            Thread.sleep(50);
            //获取第二次CPU快照
            for (String process : allProcess) {
                CpuMessage cpuMessage = new CpuMessage();
                allCpu2.put(process, getCpuInfo(process, cpuMessage));
            }
            //计算CPU占比
            Map<String, String> getCpuTime = getCpuTime(allCpu, allCpu2, allProcess);
            for (String process : allProcess) {
                processMap.get(process).setCpu(getCpuTime.get(process));
                processList.add(processMap.get(process));
            }
        } catch (Exception e) {
            log.error("获取进程cpu占用率异常{}", ExceptionUtil.getMessage(e));
        }
        //以cpu占用排序
        return processList.stream()
                .filter(s -> ObjectUtils.isNotEmpty(s.getCpu())).sorted((o1, o2) -> o2.getCpu().compareTo(o1.getCpu())).collect(Collectors.toList());
    }
  /**
     * 获取可信服务的pid
     */
    public static Map<String, String> getLtcsPid() {
        Map<String, String> ltcsPid = new HashMap<>();
        SystemCommanderResult serverPid = CommandUtil.execAgentSystemCommand("/usr/share/ltcs_server/jdk1.8.0/bin/jps");
        String[] split = serverPid.getCommanderresult().split("\n");
        for (String item : split) {
            if (item.contains("server")) {
                ltcsPid.put("server", item.split(" ")[0]);
            }
            if (item.contains("agent")) {
                ltcsPid.put("proxy", item.split(" ")[0]);
            }
        }
        return ltcsPid;
    }

  /**
     * CPU快照
     */
    public Map<String, String> getCpuTime(Map<String, CpuMessage> listold, Map<String, CpuMessage> listnew, List<String> process) {
        Map<String, String> cpuList = new HashMap<>();
        for (String pid : process) {
            if (listold.get(pid) != null && listnew.get(pid) != null) {
                double processCpuPret = (listnew.get(pid).getProcessTime() - listold.get(pid).getProcessTime()) /
                        (listnew.get(pid).getTotalTime() - listold.get(pid).getTotalTime());
                //保留小数点后四位
                double processCpuPercentage = Double.parseDouble(String.format("%.4f", processCpuPret)) * 100;
                //计算结果为多核，需乘CPU核数
                cpuList.put(pid, String.format("%.2f", processCpuPercentage * cpuCount));
            }
        }
        return cpuList;
    }

 //获取所有进程
    public List<String> getAllProcess() {
        List<String> allProcess = new ArrayList();
        File file = new File(path);
        File[] files = file.listFiles();
        for (File fileItem : files) {
            if (StringUtils.isNumeric(fileItem.getName())) {
                allProcess.add(fileItem.getName());
            }
        }
        return allProcess;
    }

//获取进程内存占用率
    public Map<String, String> getProcesMemory(String pid) {
        Map<String, String> processMap = new HashMap();
        String pathProces = "/proc/" + pid + "/status";
        if (StringUtils.isNotEmpty(pid) && StringUtils.isNumeric(pid)) {
            try (FileReader reader = new FileReader(pathProces);
                 BufferedReader bufferedReader = new BufferedReader(reader)) {
                String line;
                while (null != (line = bufferedReader.readLine())) {
                    //获取进程名
                    if (line.contains("Name")) {
                        String getName = getString(line);
                        processMap.put("NAME", getName);
                    }
                    //获取进程状态
                    if (line.contains("State")) {
                        String getState = getString(line);
                        processMap.put("STATE", formStatus(getState.split(" ")[0]));
                    }
                    //获取交换内存
                    if (line.contains("VmSwap")) {
                        String getSwap = getString(line);
                        String getProgressMemory = Pattern.compile("[^0-9]").matcher(getSwap).replaceAll("").trim();
                        processMap.put("VMSWAP", String.format("%.2f", Double.parseDouble(getProgressMemory) / 1024));
                    }
                    //获取虚拟内存
                    if (line.contains("VmSize")) {
                        String getSwap = getString(line);
                        String getProgressMemory = Pattern.compile("[^0-9]").matcher(getSwap).replaceAll("").trim();
                        processMap.put("VMSIZE", String.format("%.2f", Double.parseDouble(getProgressMemory) / 1024));
                    }
                    //获取进程内存占用
                    if (line.contains("VmRSS")) {
                        String getMemory = getString(line);
                        String getProgressMemory = Pattern.compile("[^0-9]").matcher(getMemory).replaceAll("").trim();
                        double percentage = Double.parseDouble(String.format("%.4f", Double.parseDouble(getProgressMemory) / Double.parseDouble(memoryTotal))) * 100;
                        processMap.put("RES", String.format("%.2f", Double.parseDouble(getProgressMemory) / 1024));
                        processMap.put("MEM", String.format("%.2f", percentage));
                        break;
                    } else {
                        processMap.put("RES", "0.00");
                        processMap.put("MEM", "0.00");
                    }
                }
            } catch (Exception e) {
                log.error("获取进程内存失败{}", ExceptionUtil.getMessage(e));
            }
        }
        return processMap;
    }
  //获取UID
    public String getProcesUser(String pid) {
        String pathProces = "/proc/" + pid + "/status";
        String procesUser = "";
        if (StringUtils.isNotEmpty(pid) && StringUtils.isNumeric(pid)) {
            try (
                    FileReader reader = new FileReader(pathProces);
                    BufferedReader bufferedReader = new BufferedReader(reader)) {
                String line;
                while (null != (line = bufferedReader.readLine())) {
                    String uid;
                    if (line.contains("Uid")) {
                        uid = line.split("\t")[1].trim();
                        //获取用户名
                        if (StringUtils.isNotBlank(searchUidByName.get(uid))) {
                            procesUser = searchUidByName.get(uid);
                        } else {
                            procesUser = UidUtil.getUserNameByUid(uid);
                            searchUidByName.put(uid, procesUser);
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("获取进程用户失败{}", ExceptionUtil.getMessage(e));
            }
        }
        return procesUser;
    }
 /**
     * 获取进程总内存
     */
    public String getAllMemory() {
        String pathProces = "/proc/meminfo";
        String line;
        String getMemoryTotal = "";
        try (FileReader reader = new FileReader(pathProces);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            while (null != (line = bufferedReader.readLine())) {
                if (line.contains("MemTotal")) {
                    String getName = getString(line);
                    getMemoryTotal = Pattern.compile("[^0-9]").matcher(getName).replaceAll("").trim();
                }
            }
        } catch (IOException e) {
            log.error("获取进程内存失败{}", ExceptionUtil.getMessage(e));
        }
        return getMemoryTotal;
    }
  //获取cpu占用率
    public CpuMessage getCpuInfo(String pid, CpuMessage cpuMessage) {
        //获取cpu全部时间
        Double totalTime = fillCpuTime();
        //获取当前进程时间
        Double processTime = processTime(pid);
        cpuMessage.setProcessTime(processTime);
        cpuMessage.setTotalTime(totalTime);
        cpuMessage.setPid(pid);
        return cpuMessage;
    }
//获取某进程瞬时cpu时间
    public Double processTime(String pid) {
        double processTime = 0.0;
        if (StringUtils.isNotEmpty(pid) && StringUtils.isNumeric(pid)) {
            String pathProces = "/proc/" + pid + "/stat";
            String line;
            try (FileReader reader = new FileReader(pathProces);
                 BufferedReader bufferedReader = new BufferedReader(reader)) {
                while (null != (line = bufferedReader.readLine())) {
                    if (line.contains("(")) {
                        String strReplace = "";
                        //抽取括号的空格
                        String regex = "\\(.*?\\)";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(line);
                        while (matcher.find()) {
                            strReplace = matcher.group().replaceAll(" ", "");
                        }
                        line = line.replaceAll(regex, strReplace);
                    }
                    String[] cpuMessages = line.split(" ");
                    processTime = Double.parseDouble(cpuMessages[13]) + Double.parseDouble(cpuMessages[14])
                            + Double.parseDouble(cpuMessages[15]) + Double.parseDouble(cpuMessages[16]);
                }
            } catch (Exception e) {
                log.error("获取进程cpu信息失败{}", ExceptionUtil.getMessage(e));
                //server单独重启之后,之前存的pid需要更新
                ltcsPid = getLtcsPid();
            }
        }
        return processTime;
    }

/**
     * 填充CPU各项时钟周期信息
     */
    public Double fillCpuTime() {
        CpuTime ret = new CpuTime();
        File file = new File("/proc/stat");
        try (FileInputStream fileInputStream = new FileInputStream(file);
             InputStreamReader in = new InputStreamReader(fileInputStream)) {
            //采集CPU时间
            BufferedReader br = new BufferedReader(in);
            StringTokenizer token = new StringTokenizer(br.readLine());
            token.nextToken();
            ret.setUser(Double.parseDouble(token.nextToken() + ""));
            ret.setNice(Double.parseDouble(token.nextToken() + ""));
            ret.setSystem(Double.parseDouble(token.nextToken() + ""));
            ret.setIdle(Double.parseDouble(token.nextToken() + ""));
            ret.setIoWait(Double.parseDouble(token.nextToken() + ""));
            ret.setIrq(Double.parseDouble(token.nextToken() + ""));
            ret.setSoftIrq(Double.parseDouble(token.nextToken() + ""));
            ret.setStealStolen(Double.parseDouble(token.nextToken() + ""));
            ret.setGuest(Double.parseDouble(token.nextToken() + ""));
        } catch (Exception e) {
            log.error("采集cpu时间失败{}", e.getMessage());
        }
        return ret.getTotalTIme();
    }

  //获取cpu描述信息
    public Integer getCpuCount() {
        String pathProces = "/proc/cpuinfo";
        String line;
        int couCount = 0;
        try (FileReader reader = new FileReader(pathProces);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            while (null != (line = bufferedReader.readLine())) {
                if (line.contains("processor")) {
                    couCount = couCount + 1;
                }
            }
        } catch (Exception e) {
            log.error("获取cpu核数信息失败{}", ExceptionUtil.getMessage(e));
        }
        return couCount;
    }

  //获取进程描述信息
    public String getProcessDes(String pid) {
        String pathProces = "/proc/" + pid + "/cmdline";
        String line;
        String des = "";
        if (StringUtils.isNotEmpty(pid) && StringUtils.isNumeric(pid)) {
            try (FileReader reader = new FileReader(pathProces);
                 BufferedReader bufferedReader = new BufferedReader(reader)) {
                while (null != (line = bufferedReader.readLine())) {
                    des = line;
                }
            } catch (Exception e) {
                log.error("获取进程描述信息失败{}", ExceptionUtil.getMessage(e));
            }
        }
        return des;
    }

    //获取指定值
    public String getString(String str) {
        String strTemp = str.substring(0, str.indexOf(":"));
        return str.substring(strTemp.length() + 1).trim();
    }

     private static String formStatus(String val) {
        String value = "未知";
        if ("S".equalsIgnoreCase(val)) {
            value = "睡眠";
        } else if ("R".equalsIgnoreCase(val)) {
            value = "运行";
        } else if ("T".equalsIgnoreCase(val)) {
            value = "跟踪/停止";
        } else if ("Z".equalsIgnoreCase(val)) {
            value = "僵尸进程 ";
        } else if ("D".equalsIgnoreCase(val)) {
            value = "不可中断的睡眠状态 ";
        } else if ("i".equalsIgnoreCase(val)) {
            value = "多线程 ";
        }
        return value;
    }

}
