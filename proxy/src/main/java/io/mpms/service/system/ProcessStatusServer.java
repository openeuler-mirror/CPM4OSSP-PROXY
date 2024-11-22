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
