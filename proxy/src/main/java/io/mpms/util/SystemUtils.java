package io.jpom.util;

import com.alibaba.fastjson.JSONObject;
import io.jpom.common.BaseFileOpr;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemUtils {


    public JSONObject getVersion() {
        JSONObject rel = new JSONObject();
        String system = BaseFileOpr.getFileContent("/etc/issue");
        String kernel = BaseFileOpr.getFileContent("/proc/version");

        if (!system.isEmpty()) {
            rel.put("system", system.replaceAll("\\\\n", "").replaceAll("\\\\l", "").replaceAll("\\n", "").replaceAll("\0", ""));
        }

        if (!kernel.isEmpty()) {
            rel.put("kernel", kernel.replaceAll("\\n", "").replaceAll("\0", ""));
        }

        return rel;
    }

}
