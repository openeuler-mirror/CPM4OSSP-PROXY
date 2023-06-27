/*
 *耗时任务队列实体类
 *
 * 
 
 */

package io.mpms.model;

import io.jpom.model.data.DelayedTaskModel;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class TaskQueueModel {


    private static volatile TaskQueueModel taskQueueInstance;

    private TaskQueueModel() {

    }

    public static TaskQueueModel getInstance() {
        if (taskQueueInstance == null) {
            synchronized (TaskQueueModel.class) {
                if (taskQueueInstance == null) {
                    taskQueueInstance = new TaskQueueModel();
                }
            }
        }

        return taskQueueInstance;
    }

    /*
     * 任务队列
     */
    private final PriorityBlockingQueue<DelayedTaskModel> taskModels = new PriorityBlockingQueue<>();

    private DelayedTaskModel findTask(Integer id) {

        for (DelayedTaskModel item : taskModels) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public boolean addTask(DelayedTaskModel task) {
        task.setTaskStatus(0);
        return taskModels.offer(task);
    }

    public DelayedTaskModel getTask(Integer id) {
        return findTask(id);
    }

    public boolean addTasks(List<DelayedTaskModel> tasks) {
        for (DelayedTaskModel item : tasks) {
            if (!addTask(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean removeTask(Integer id) {
        DelayedTaskModel tmpTask = findTask(id);

        return taskModels.remove(tmpTask);
    }

    public boolean modifyStatus(Integer id, Integer taskStatus, String taskContent) {
        DelayedTaskModel tmpTask = findTask(id);

        if (tmpTask != null) {
            tmpTask.setTaskStatus(taskStatus);
            tmpTask.setTaskContent(taskContent);
            return true;
        }

        return false;
    }

    public DelayedTaskModel peekHeadTask() {
        return taskModels.peek();
    }

    public DelayedTaskModel peekPraperTask() {
        for (DelayedTaskModel item : taskModels) {
            if (item.getTaskStatus().equals(0)) {
                return item;
            }
        }
        return null;
    }
}
