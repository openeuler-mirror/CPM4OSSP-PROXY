package io.mpms.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SourcePackageInfoConfig {
    private static String sourcePackageInfoIndexPath;

    public static String getSourcePackageInfoIndexPath() {
        return sourcePackageInfoIndexPath;
    }

    @Value("${pakgeinfo.indexPath:/usr/share/mpms_proxy/data_storage/index}")
    public void setSourcePackageInfoIndexPath(String sourcePackageInfoIndexPath) {
        SourcePackageInfoConfig.sourcePackageInfoIndexPath = sourcePackageInfoIndexPath;
    }
}
