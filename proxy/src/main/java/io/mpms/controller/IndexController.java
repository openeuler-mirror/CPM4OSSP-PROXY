package io.mpms.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import cn.jiangzeyin.common.JsonMessage;
import com.alibaba.fastjson.JSONObject;
import io.jpom.common.BaseAgentController;
import io.jpom.common.JpomManifest;
import io.jpom.common.RemoteVersion;
import io.jpom.common.interceptor.NotAuthorize;
import io.jpom.model.data.ProjectInfoModel;
import io.jpom.service.WhitelistDirectoryService;
import io.jpom.service.manage.ProjectInfoService;
import io.jpom.util.JvmUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 首页
 *
 */
@RestController
public class IndexController extends BaseAgentController {
    @Resource
    private WhitelistDirectoryService whitelistDirectoryService;
    @Resource
    private ProjectInfoService projectInfoService;

    @RequestMapping(value = {"index", "", "index.html", "/"}, produces = MediaType.TEXT_PLAIN_VALUE)
    @NotAuthorize
    public String index() {
        return "Jpom-Agent,Can't access directly,Please configure it to JPOM server";
    }

}
