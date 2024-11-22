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
