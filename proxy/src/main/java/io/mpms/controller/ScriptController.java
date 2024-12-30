package io.jpom.controller;

import cn.jiangzeyin.common.JsonMessage;
import io.jpom.api.req.ManageCommandAgentReq;
import io.jpom.api.req.ScriptParamReq;
import io.jpom.model.ManageComand;
import io.jpom.service.ManageCommandService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * 脚本管理
 */
@RestController
@RequestMapping(value = "/script")
@Slf4j
@EnableAsync
public class ScriptController {
    @Resource
    ManageCommandService manageCommandService;

    @RequestMapping("execute")
    public JsonMessage execute(MultipartFile file, ScriptParamReq scriptParamReq) {
        String manageCommandId = scriptParamReq.getManageCommandId();
        int manageCommandIdInteger = Integer.parseInt(manageCommandId);
        String param = scriptParamReq.getParam();
        if (param == null) {
            param = "";
        }
        String path = scriptParamReq.getPath();
        String originalFilename = file.getOriginalFilename();
        File targetFile = new File(path, originalFilename);
        if (targetFile.exists()) {
            ManageComand manageCommand = new ManageComand();
            manageCommand.setId(manageCommandIdInteger);
            manageCommand.setResult("下发位置已存在相同的文件");
            manageCommand.setExitValue(-1);
            int update = manageCommandService.update(manageCommand);
            return new JsonMessage(501, "下发位置已存在相同的文件");
        }
        try {
            FileUtils.createParentDirectories(targetFile);
            targetFile.createNewFile();
            file.transferTo(targetFile);
        } catch (IOException e) {
            log.error("文件下发失败{}", e);
            ManageComand manageCommand = new ManageComand();
            manageCommand.setId(manageCommandIdInteger);
            manageCommand.setResult("文件下发失败:" + ExceptionUtils.getStackTrace(e));
            manageCommand.setExitValue(-1);
            int update = manageCommandService.update(manageCommand);
            return new JsonMessage(501, "下发失败");
        }
        manageCommandService.doExecute(targetFile.getPath(), param, manageCommandIdInteger);
        return new JsonMessage(200, "success");
    }




}
