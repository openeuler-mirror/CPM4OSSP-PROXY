/*
 *耗时任务实体
 *
 *

 */

package io.mpms.model.data;

public class DelayedTaskModel implements Comparable<DelayedTaskModel> {
    /*
     * id，由数据库设置
     */
    private Integer id;

    /*
     * 任务id，值等同id
     */
    private Integer taskId;

    /*
     * 节点id，由前端提供
     */
    private Integer nodeId;

    /*
     * 任务状态，“0、1、2、3”
     */
    private Integer taskStatus;

    /*
     * 任务优先级，“0、1、2...”
     */
    private Integer taskPriority;

    /*
     * 任务group id，由前端提供
     */
    private Integer taskGroupId;

    /*
     * 任务行为，“install、uninstall、update”
     */
    private String taskAction;

    /*
     * 任务目标，包名称
     */
    private String taskTarget;

    /*
     * 任务结果
     */
    private String taskContent;

    /*
     * 扩展项
     */
    private String taskExtra;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

}
