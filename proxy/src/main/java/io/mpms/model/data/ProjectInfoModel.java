package io.mpms.model.data;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.request.XssFilter;
import cn.jiangzeyin.common.spring.SpringUtil;
import io.jpom.common.commander.AbstractProjectCommander;
import io.jpom.model.BaseJsonModel;
import io.jpom.model.BaseModel;
import io.jpom.model.RunMode;
import io.jpom.service.WhitelistDirectoryService;
import io.jpom.system.LinuxRuntimeException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 项目配置信息实体
 *
 */
public class ProjectInfoModel extends BaseModel {
    /**
     * 分组
     */
    private String group;
    private String mainClass;
    private String lib;
    /**
     * 白名单目录
     */
    private String whitelistDirectory;
    private String log;

    /**
     * 日志目录
     */
    private String logPath;

    /**
     * jvm 参数
     */
    private String jvm;
    /**
     * java main 方法参数
     */
    private String args;

    private List<JavaCopyItem> javaCopyItemList;
    /**
     * WebHooks
     */
    private String token;
    private boolean status;
    private String createTime;
    private String modifyTime;
    private String jdkId;
    /**
     * lib 目录当前文件状态
     */
    private String useLibDesc;
    /**
     * 当前运行lib 状态
     */
    private String runLibDesc;
    /**
     * 最后修改人
     */
    private String modifyUser;

    private RunMode runMode;
    /**
     * 节点分发项目，不允许在项目管理中编辑
     */
    private boolean outGivingProject;
    /**
     * 实际运行的命令
     */
    private String runCommand;

    /**
     * -Djava.ext.dirs=lib -cp conf:run.jar
     * 填写【lib:conf】
     */
    private String javaExtDirsCp;
}
