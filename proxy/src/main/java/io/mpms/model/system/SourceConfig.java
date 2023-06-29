package io.mpms.model.system;

import java.io.Serializable;

/**
 * (SourceConfig)实体类
 *
 *
 * @since 2021-10-25 17:05:11
 */

public class SourceConfig implements Serializable {
    private static final long serialVersionUID = 515241386935083527L;

    private Integer id;

    private String osVersion;

    private String arch;

    private String packageType;

    private String uri;

    private String codename;

    private String components;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getArch() {
        return arch;
    }
}