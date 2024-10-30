package io.jpom.common;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.jiangzeyin.controller.base.AbstractController;
import io.jpom.model.data.ProjectInfoModel;
import io.jpom.service.manage.ProjectInfoService;
import io.jpom.system.ConfigBean;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * proxy 端
 */
public abstract class BaseAgentController extends BaseJpomController {
    @Resource
    protected ProjectInfoService projectInfoService;

    protected String getUserName() {
        return getUserName(getRequest());
    }

    /**
     * *获取客户端IP
     *
     * @return agentIp
     */
    public static String getAgentIp() {

        String agentIp = getClientIP();
        return agentIp;
    }


}