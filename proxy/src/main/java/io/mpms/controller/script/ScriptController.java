package io.mpms.controller.script;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.jiangzeyin.common.JsonMessage;
import cn.jiangzeyin.controller.multipart.MultipartFileBuilder;
import io.jpom.JpomApplication;
import io.jpom.common.BaseAgentController;
import io.jpom.model.data.ScriptModel;
import io.jpom.service.script.ScriptServer;
import io.jpom.system.AgentConfigBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * 脚本管理
 */
@RestController
@RequestMapping(value = "/script")
public class ScriptController extends BaseAgentController {

    @Resource
    private ScriptServer scriptServer;

    @RequestMapping(value = "list.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String list() {
        return JsonMessage.getString(200, "", scriptServer.list());
    }
}
