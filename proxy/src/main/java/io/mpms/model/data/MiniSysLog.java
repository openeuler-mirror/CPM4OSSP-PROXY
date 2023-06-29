package io.mpms.model.data;

import cn.hutool.core.date.DateTime;

import java.io.Serializable;

/**
 * 凝思系统软件安装管理平台系统日志表(Minisyslog)实体类
 *
 *
 * @since 2021-10-22 09:53:54
 */
public class MiniSysLog implements Serializable {
    private static final long serialVersionUID = -70056513892554940L;

    private Integer id;
    /**
     * 节点还是平台
     */
    private Integer type;

    private static String ip;

    private String time;

    private String content;

    private Integer level;

    private String extra;

    public MiniSysLog() {
        this.id = 1;
        this.type = 0;
        this.time = DateTime.now().toString();
    }

    public MiniSysLog(int level, String content, String extra) {
        this.id = 1;
        this.type = 0;
        this.time = DateTime.now().toString();
        this.level = level;
        this.content = content;
        this.extra = extra;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}