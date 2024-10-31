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


}
