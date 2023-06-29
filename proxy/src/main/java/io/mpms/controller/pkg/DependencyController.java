package io.mpms.controller.pkg;

import cn.jiangzeyin.common.JsonMessage;
import com.alibaba.druid.util.StringUtils;
import io.jpom.api.pkg.DebPkg;
import io.jpom.common.interceptor.NotAuthorize;
import io.jpom.model.data.SystemCommanderResult;
import io.jpom.util.CommandUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "pkg/dependency")
public class DependencyController {
    @RequestMapping(value = "queryDependency")
    @NotAuthorize
    public String queryDependency(@RequestBody DebPkg debPkg) {
        String command = String.format("apt-cache depends %s", debPkg.getPackageName());
        SystemCommanderResult systemCommanderResult = CommandUtil.execAgentSystemCommand(command);

        if (StringUtils.equals(systemCommanderResult.getCommanderStaus() + "", "0")) {
            String commanderresult = systemCommanderResult.getCommanderresult();
            return JsonMessage.getString(200, "", DependencyParse.parseDependency(commanderresult));
        } else {
            return JsonMessage.getString(500, String.format("[%d] %s", systemCommanderResult.getCommanderStaus(), systemCommanderResult.getCommanderresult()), "");
        }
    }
}
