package io.mpms.model.data;

import java.io.Serializable;

/**
 * (Delayedtask)实体类
 *
 * 
 * @since 2021-10-21 14:34:07
 */
public class DelayedTask implements Serializable, Comparable<DelayedTask> {
    private static final long serialVersionUID = 530550411236437384L;

    private Integer id;

    private Integer taskPriority;

    private String taskAction;

    private String taskTarget;

    private Integer taskStatus;

    private String taskContent;

    private Integer taskViewTimes;

    private Integer taskGroupId;

    private String taskNodeId;

    private String taskExtra;
}