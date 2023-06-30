package io.mpms.model.data;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import io.jpom.model.BaseModel;
import io.jpom.system.LinuxRuntimeException;

import java.io.File;
import java.io.InputStream;

/**
 * tomcat 对象实体
 *
 *
 */
public class TomcatInfoModel extends BaseModel {

    private String path;
    private int port;
    private int status;
    private String appBase;
    private String creator;
    private String createTime;
    private String modifyUser;
    private String modifyTime;

    public String getPath() {
        if (path == null) {
            return null;
        }
        return FileUtil.normalize(path + "/");
    }

    /**
     * 检测路径是否正确
     *
     * @return path
     */
    public String pathAndCheck() {
        String path = getPath();
        if (path == null) {
            return null;
        }
        if (isTomcatRoot(path)) {
            return path;
        }
        throw new RuntimeException(String.format("没有在路径：%s 下检测到Tomcat", path));
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
