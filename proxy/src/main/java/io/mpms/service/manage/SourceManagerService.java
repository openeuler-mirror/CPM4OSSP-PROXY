package io.mpms.service.manage;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import cn.hutool.system.SystemUtil;
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
}
