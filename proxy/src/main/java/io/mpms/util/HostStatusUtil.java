package io.jpom.util;


import cn.hutool.core.exceptions.ExceptionUtil;
import io.jpom.model.system.FileInode;
import io.jpom.model.system.NetInodePort;
import io.jpom.model.system.ProcessSocket;
import io.jpom.model.system.ProgramNet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class HostStatusUtil {

    final private static String cpuStat = "/proc/stat";

    final private static String proc = "/proc";

    final private static String cpuInfoFile = "/proc/cpuinfo";

    final private static String memInfoFile = "/proc/meminfo";

    final private static String netTcpFile = "/proc/net/tcp";

    final private static String netTcp6File = "/proc/net/tcp6";

    final private static String netUdpFile = "/proc/net/udp";

    final private static String netUdp6File = "/proc/net/udp6";

    final private static String passwdFile = "/etc/passwd";

    final private static String hostNameFile = "/etc/hostname";

    final private static String TCP_PROTO = "TCP";

    final private static String TCP6_PROTO = "TCP6";

    final private static String UDP_PROTO = "UDP";

    final private static String UDP6_PROTO = "UDP6";

    final private static int UDP = 1;

    final private static int TCP = 2;

    final private static int UDP6 = 3;

    final private static int TCP6 = 4;


    static private String getInodeByFileKey(Object fileKey) {

        String keyStr = fileKey.toString();
        String[] splitStr = keyStr.split("=");
        String ret = null;
        if (splitStr.length >= 3) {
            ret = splitStr[2].replace(")", "");
        }
        return ret;
    }


    static private List<NetInodePort> getInodePort(String netFile, int netType) {
        List<NetInodePort> ret = new ArrayList<>();
        String proto = null;
        if (UDP == netType) {
            proto = UDP_PROTO;

        } else if (TCP == netType) {
            proto = TCP_PROTO;

        } else if (TCP6 == netType) {
            proto = TCP6_PROTO;
        } else if (UDP6 == netType) {
            proto = UDP6_PROTO;
        }

        try (FileReader reader = new FileReader(netFile);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;

            NetInodePort tmpPort;
            while (null != (line = bufferedReader.readLine())) {
                if (line.contains("sl")) {
                    continue;
                }
                tmpPort = new NetInodePort();
                line = line.trim();
                String[] tmp = line.split("\\s+");
                if (tmp.length >= 10) {
                    String[] local = tmp[1].split(":");

                    tmpPort.setInode(tmp[9]);
                    tmpPort.setStatus(tmp[3]);
                    tmpPort.setPort(local[1]);
                    tmpPort.setProto(proto);

                    ret.add(tmpPort);
                }
            }


        } catch (Exception e) {
            log.error("get inode and port error ! " + ExceptionUtil.getMessage(e));
        }

        return ret;
    }
 static private ProcessSocket getProcessSocket(File file) {
        ProcessSocket ret = new ProcessSocket();
        ret.setPid(file.getName());
        try (FileReader cmdReader = new FileReader(file.getAbsolutePath() + "/cmdline");
             BufferedReader pidCmdBufReader = new BufferedReader(cmdReader)) {
            String cmdLine = pidCmdBufReader.readLine();
            if (cmdLine != null) {
                ret.setPROGRAM(cmdLine.trim().replaceAll("\0", " "));
            }
            File fdPath = new File(file.getAbsolutePath() + "/fd");
            File[] fdFiles = fdPath.listFiles();
            BasicFileAttributes bfa;
            if (null != fdFiles) {
                for (File item : fdFiles) {
                    FileInode inode = new FileInode();
                    if (!item.exists()) {
                        continue;
                    }

                    bfa = Files.readAttributes(item.toPath(), BasicFileAttributes.class);
                    Object tmpKey = bfa.fileKey();

                    inode.setFile(item);
                    inode.setInode(getInodeByFileKey(tmpKey));
                    ret.addToLinkFiles(inode);
                }
            }


        } catch (Exception e) {
            log.error("get " + file.getAbsolutePath() + " inode error! ---------- " + ExceptionUtil.getMessage(e));
        }
        return ret;
    }
  static private List<ProcessSocket> getProcessSockets() {
        List<ProcessSocket> ret = new ArrayList<>();
        File file = new File(proc);
        File[] procFiles = file.listFiles();
        if (null == procFiles) {
            return null;
        }
        for (File item : procFiles) {
            if (StringUtils.isNumeric(item.getName())) {
                ret.add(getProcessSocket(item));
            }
        }

        return ret;
    }
    //网络采集
    static public List<ProgramNet> getProgramNets() {

        List<ProgramNet> ret = new ArrayList<>();
        List<ProcessSocket> processNetWorks = getProcessSockets();
        List<NetInodePort> netList = new ArrayList<>();
        if (new File(netTcp6File).exists()) {
            netList.addAll(getInodePort(netTcp6File, TCP6));
        }
        if (new File(netTcpFile).exists()) {
            netList.addAll(getInodePort(netTcpFile, TCP));
        }
        if (new File(netUdpFile).exists()) {
            netList.addAll(getInodePort(netUdpFile, UDP));
        }
        if (new File(netUdp6File).exists()) {
            netList.addAll(getInodePort(netUdp6File, UDP6));
        }
        if (null != processNetWorks) {
            ProgramNet tmpProgram;
            for (ProcessSocket item : processNetWorks) {
                for (FileInode it : item.getProcessInodeList()) {
                    for (NetInodePort iter : netList) {
                        if (it.getInode().equals(iter.getInode())) {
                            tmpProgram = new ProgramNet();
                            String getPROGRAM = item.getPROGRAM();
                            if (getPROGRAM.length() > 150) {
                                tmpProgram.setPROGRAM(getPROGRAM.substring(0, 150));
                            } else {
                                tmpProgram.setPROGRAM(getPROGRAM);
                            }
                            tmpProgram.setPROTO(iter.getProto());
                            tmpProgram.setPORT(Integer.parseInt(iter.getPort().trim(), 16));
                            tmpProgram.setPid(item.getPid());
                            ret.add(tmpProgram);
                        }
                    }
                }
            }
        }
        return ret;
    }
}
