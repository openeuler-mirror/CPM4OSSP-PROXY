package io.mpms.service.manage;

import cn.jiangzeyin.common.DefaultSystemLog;
import io.mpms.service.mysqldb.DelayedTaskService;
import io.mpms.service.mysqldb.MiniSysLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DelayedTaskRunner implements Runnable {

    @Resource
    private DelayedTaskService delayedtaskService;

    @Resource
    private MiniSysLogService miniSysLogService;

    private String nodeId;

    private String extra;

    private SystemCommanderResult performTask(DelayedTask task) {
        String action = task.getTaskAction();
        switch (action) {
            case "install": {
                extra = "安装软件包" + task.getTaskTarget();
                return AbstractSystemCommander.getInstance().installPackage(task.getTaskTarget().replace(" ", ""));
            }

            case "uninstall": {
                extra = "卸载软件包" + task.getTaskTarget();
                return AbstractSystemCommander.getInstance().uninstallPackage(task.getTaskTarget().replace(" ", ""));
            }

            case "update": {
                extra = "更新软件包" + task.getTaskTarget();
                return AbstractSystemCommander.getInstance().updatePackage(task.getTaskTarget().replace(" ", ""));
            }

            default:{
                // 删除本次循环的任务,可能为脏数据
                this.delayedtaskService.deleteById(task.getId());
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            delayedtaskService.setNodeId(nodeId);
            miniSysLogService.insert(0, "Start delayed task thread.", "启动后台任务调度线程!");
            while (true) {
                Thread.sleep(100);
                DelayedTask task = this.delayedtaskService.getOneNotStartedTask();
                if (task == null) {
                    continue;
                }
                miniSysLogService.insert(0, "Start performing background tasks.", "当前线程状态为："+Thread.currentThread().getState()+"，开始执行后台任务!任务目的："+task.getTaskAction()+" "+task.getTaskTarget());
                task.setTaskStatus(1);
                task.setTaskId(task.getId());
                this.delayedtaskService.update(task);
                SystemCommanderResult commanderResult = performTask(task);
                if (commanderResult != null) {
                    if (commanderResult.getCommanderStaus().equals(0)) {
                        task.setTaskStatus(2);
                        miniSysLogService.insert(0, task.getTaskAction() + " " + task.getTaskTarget() + " success.",
                                extra+"成功!");
                    } else {
                        task.setTaskStatus(3);
                        miniSysLogService.insert(0, task.getTaskAction() + " " + task.getTaskTarget() + " failed.",
                                extra+"失败!");
                    }
                    task.setTaskViewTimes(0);
                    task.setTaskContent(commanderResult.getCommanderresult());
                }
                this.delayedtaskService.update(task);

            }
        } catch (Exception e) {
            miniSysLogService.insert(0, "The background thread was aborted abnormally.", "当前线程状态为："+Thread.currentThread().getState()+"，后台任务调度线程异常中止!");
            DefaultSystemLog.getLog().error("********************************" + e.getMessage(), e + "*********************************************");
        }
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
