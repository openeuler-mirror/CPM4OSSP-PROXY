package io.mpms.system;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.jiangzeyin.common.spring.SpringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * proxy端外部配置
 */
@Configuration
public class AgentExtConfigBean {
    private static AgentExtConfigBean agentExtConfigBean;
    /**
     * 白名单路径是否判断包含关系
     */
    @Value("${whitelistDirectory.checkStartsWith:true}")
    public boolean whitelistDirectoryCheckStartsWith;

    /**
     * 自动备份控制台日志，防止日志文件过大，目前暂只支持linux 不停服备份  如果配置none 则不自动备份 默认10分钟扫描一次
     */
    @Value("${log.autoBackConsoleCron:0 0/10 * * * ?}")
    public String autoBackConsoleCron;
    /**
     * 当文件多大时自动备份
     *
     * @see ch.qos.logback.core.util.FileSize
     */
    @Value("${log.autoBackSize:50MB}")
    public String autoBackSize;
    /**
     * 控制台日志保存时长单位天
     */
    @Value("${log.saveDays:7}")
    private int logSaveDays;

    @Value("${mpms.agent.id:}")
    private String agentId;

    @Value("${mpms.agent.url:}")
    private String agentUrl;

    @Value("${mpms.server.url:}")
    private String serverUrl;

    @Value("${mpms.server.token:}")
    private String serverToken;

    /**
     * 项目状态禁用 调用jmx获取
     */
    @Value("${project.disableVirtualMachine:false}")
    private boolean disableVirtualMachine;

    /**
     * 停止项目等待的时长 单位秒，最小为1秒
     */
    @Value("${project.stopWaitTime:10}")
    private int stopWaitTime;

    /**
     * 包构架信息
     */
    @Value("${packageArchitecture.amd64:all amd64 i386}")
    public String amd64Arch;

    @Value("${packageArchitecture.arm64:all arm64}")
    public String arm64Arch;

    @Value("${packageArchitecture.i386:all i386}")
    public String i386Arch;

    public int getStopWaitTime() {
        return stopWaitTime;
    }

    public boolean isDisableVirtualMachine() {
        return disableVirtualMachine;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getServerToken() {
        return serverToken;
    }

    /**
     * 获取当前的url
     *
     * @return 如果没有配置将自动生成：http://+本地IP+端口
     */
    public String getAgentUrl() {
        if (StrUtil.isEmpty(agentUrl)) {
            String localhostStr = NetUtil.getLocalhostStr();
            int port = ConfigBean.getInstance().getPort();
            agentUrl = String.format("http://%s:%s", localhostStr, port);
        }
        if (StrUtil.isEmpty(agentUrl)) {
            throw new LinuxRuntimeException("获取Agent url失败");
        }
        return agentUrl;
    }


    /**
     * 创建请求对象
     *
     * @param openApi url
     * @return HttpRequest
     * @see ServerOpenApi
     */
    public HttpRequest createServerRequest(String openApi) {
        if (StrUtil.isEmpty(getServerUrl())) {
            throw new LinuxRuntimeException("请先配置server端url");
        }
        if (StrUtil.isEmpty(getServerToken())) {
            throw new LinuxRuntimeException("请先配置server端Token");
        }
        // 加密
        String md5 = SecureUtil.md5(getServerToken());
        md5 = SecureUtil.sha1(md5 + ServerOpenApi.HEAD);
        HttpRequest httpRequest = HttpUtil.createPost(String.format("%s%s", serverUrl, openApi));
        httpRequest.header(ServerOpenApi.HEAD, md5);
        return httpRequest;
    }

    /**
     * 配置错误或者没有，默认是7天
     *
     * @return int
     */
    public int getLogSaveDays() {
        if (logSaveDays <= 0) {
            return 7;
        }
        return logSaveDays;
    }

    /**
     * 是否开启日志备份
     *
     * @return 如果表达式配置为none 则不配置，重启也不备份
     */
    public boolean openLogBack() {
        String cron = StrUtil.emptyToDefault(autoBackConsoleCron, "none");
        return !"none".equalsIgnoreCase(cron.trim());
    }

    /**
     * 获取当前登录节点的包构架配置，如果没有则返回默认值
     *
     * @return Map<String, String>
     */
    public Map<String, String> getPackageArchitecture() {
        Map<String, String> matchArch = new HashMap<>();
        matchArch.put("amd64", amd64Arch);
        matchArch.put("arm64", arm64Arch);
        matchArch.put("i386", i386Arch);
        return matchArch;
    }

    /**
     * 单例
     *
     * @return this
     */
    public static AgentExtConfigBean getInstance() {
        if (agentExtConfigBean == null) {
            agentExtConfigBean = SpringUtil.getBean(AgentExtConfigBean.class);
        }
        return agentExtConfigBean;
    }
}
