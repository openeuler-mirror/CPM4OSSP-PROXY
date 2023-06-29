package io.mpms.model.system;

import cn.hutool.core.util.StrUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import io.jpom.common.commander.AbstractProjectCommander;
import io.jpom.model.BaseJsonModel;

import java.io.IOException;

/**
 * 进程信息实体
 *
 
 
 */
public class ProcessModel extends BaseJsonModel {
    /**
     * 进程id
     */
    private int pid;
    /**
     * 进程名
     */
    private String command;
    /**
     * 运行状态
     */
    private String status;
    /**
     * 进程仍然在使用的，没被交换出物理内存部分的大小
     */
    private String res;
    /**
     * 所有者
     */
    private String user;
    /**
     * 时间总计
     */
    private String time;
    /**
     * 优先级
     */
    private String pr = StrUtil.DASHED;
    /**
     * nice值
     */
    private String ni = StrUtil.DASHED;
    /**
     * 虚拟内存
     */
    private String virt = StrUtil.DASHED;
    /**
     * 共享内存大小
     */
    private String shr = StrUtil.DASHED;
    /**
     * 内存比重
     */
    private String mem = StrUtil.DASHED;
    /**
     * cpu比重
     */
    private String cpu = StrUtil.DASHED;
    /**
     * 端口
     */
    private String port = StrUtil.DASHED;
    /**
     * Jpom 项目名称
     */
    private String jpomName = StrUtil.DASHED;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getJpomName() {
        return jpomName;
    }
}
