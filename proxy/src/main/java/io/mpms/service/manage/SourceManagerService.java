package io.mpms.service.manage;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.system.SystemUtil;
import io.mpms.model.system.SourceConfig;
import io.mpms.service.mysqldb.MiniSysLogService;
import io.mpms.service.mysqldb.SourceConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

@Service
public class SourceManagerService implements Runnable {


    @Resource
    MiniSysLogService miniSysLogService;

    @Resource
    private SourceConfigService sourceconfigService;
    public static final String SourceFileName = "/etc/apt/sources.list";
    private final String osInfoFile = "/etc/issue";
    private long Cycle = 1;
    private String netCheckContent;

    public SourceManagerService() {

    }

    private String getOsVersion() {
        return FileOperator.getFileContent(osInfoFile);
    }

    private SourceConfig fillBySourceStr(String sourcestr) {
        SourceConfig sourceconfig = new SourceConfig();
        String[] sourceList = sourcestr.split(" ");
        sourceconfig.setOsVersion(getOsVersion().split(" ")[1]);
        sourceconfig.setArch(SystemUtil.getOsInfo().getArch());
        sourceconfig.setCodename(sourceList[2]);
        sourceconfig.setUri(sourceList[1]);
        sourceconfig.setPackageType(sourceList[0]);
        sourceconfig.setComponents(sourcestr.substring(sourcestr.indexOf(sourceList[3])));
        return sourceconfig;
    }

    private boolean isAvailableSource(SourceConfig sourceconfig) {
        String sourceIndex = "";
        String uriStr = sourceconfig.getUri();
        if (uriStr.startsWith("http://")) {
            String http = "http://";
            sourceIndex = uriStr.substring(http.length() + 1);
        } else if (uriStr.startsWith("https://")) {
            String http = "https://";
            sourceIndex = uriStr.substring(http.length() + 1);
        } else {
            netCheckContent = "Source list(" + sourceconfig.getUri() + ") format error.";
            return false;
        }
        String sourceAdress = sourceIndex.substring(0, sourceIndex.indexOf("/"));
        String cmdResult = CommandUtil.execSystemCommand("ping -c 2 -w 2" + sourceAdress + "grep \"packet loss\"");

        if (cmdResult.indexOf("%") > 3 &&
                (cmdResult.startsWith("100", cmdResult.indexOf("%") - 3))) {
            netCheckContent = "Source list(" + sourceconfig.getUri() + ") cannot be connected.";
            return false;
        }
        return true;
    }

    public static String getFile() {
        return FileOperator.getFileContent(SourceFileName);
    }
}
