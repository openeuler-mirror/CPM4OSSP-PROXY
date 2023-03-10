package io.mpms.system.init;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.jiangzeyin.common.PreLoadClass;
import cn.jiangzeyin.common.PreLoadMethod;
import io.mpms.system.AgentAuthorize;
import io.mpms.system.AgentConfigBean;

import java.io.File;

/**
 * 检查授权信息
 *
 */
@PreLoadClass
public class CheckAuthorize {

    @PreLoadMethod
    private static void startAutoBackLog() {
        AgentAuthorize.getInstance();
    }

    /**
     * 修护脚本模板路径
     */
    @PreLoadMethod
    private static void repairScriptPath() {
        if (!JpomManifest.getInstance().isDebug()) {
            if (StrUtil.compareVersion(JpomManifest.getInstance().getVersion(), "2.4.2") < 0) {
                return;
            }
        }
        File oldDir = FileUtil.file(ExtConfigBean.getInstance().getPath(), AgentConfigBean.SCRIPT_DIRECTORY);
        if (!oldDir.exists()) {
            return;
        }
        File newDir = FileUtil.file(ConfigBean.getInstance().getDataPath(), AgentConfigBean.SCRIPT_DIRECTORY);
        FileUtil.move(oldDir, newDir, true);
    }
}
