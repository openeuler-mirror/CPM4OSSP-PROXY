package io.jpom.common.commander;

import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSONObject;
import io.jpom.common.commander.impl.LinuxSystemCommander;
import io.jpom.model.PkgEntity;
import io.jpom.model.data.SystemCommanderResult;
import io.jpom.model.system.ProcessModel;
import io.jpom.system.LinuxRuntimeException;
import io.jpom.util.CommandUtil;

import java.io.File;
import java.util.List;

/**
 * 系统监控命令
 */
public abstract class AbstractSystemCommander {

    private static AbstractSystemCommander abstractSystemCommander = null;

    public static AbstractSystemCommander getInstance() {
        if (abstractSystemCommander != null) {
            return abstractSystemCommander;
        }
        if (SystemUtil.getOsInfo().isLinux()) {
            // Linux系统
            abstractSystemCommander = new LinuxSystemCommander();
        } else {
            throw new LinuxRuntimeException("不支持的：" + SystemUtil.getOsInfo().getName());
        }
        return abstractSystemCommander;
    }

    /**
     * 获取整个服务器监控信息
     *
     * @return data
     */
    public abstract JSONObject getAllMonitor();

    /**
     * 获取节点包信息
     *
     * @return data
     */
    public abstract List<PkgEntity> getAllPackageInfo(String mode);

    /**
     * 安装节点软件包
     *llv
     * @param packageName 软件包名
     * @return 命令执行结果
     */
    public abstract SystemCommanderResult installPackage(String packageName);

    /**
     * 卸载节点软件包
     *
     * @param packageName 软件包名
     * @return 命令执行结果
     */
    public abstract SystemCommanderResult uninstallPackage(String packageName);

    /**
     * 更新节点软件包
     *
     * @param packageName 软件包名
     * @return 命令执行结果
     */
    public abstract SystemCommanderResult updatePackage(String packageName);

    /**
     * 获取当前服务器的所有进程列表
     *
     * @return array
     */
    public abstract List<ProcessModel> getProcessList();


    public abstract List<ProcessModel> getProcessListByFile();

    /**
     * 获取指定进程的 内存信息
     *
     * @param pid 进程id
     * @return json
     */
    public abstract ProcessModel getPidInfo(int pid);

    /**
     * 清空文件内容
     *
     * @param file 文件
     * @return 执行结果
     */
    public abstract String emptyLogFile(File file);

    /**
     * 磁盘占用
     *
     * @return 磁盘占用
     */
    protected static String getHardDisk() {
        File[] files = File.listRoots();
        double totalSpace = 0;
        double useAbleSpace = 0;
        for (File file : files) {
            double total = file.getTotalSpace();
            totalSpace += total;
            useAbleSpace += total - file.getUsableSpace();
        }
        if (totalSpace == 0) {
            return String.format("%.2f", 0F);
        }
        return String.format("%.2f", useAbleSpace / totalSpace * 100);
    }


}
