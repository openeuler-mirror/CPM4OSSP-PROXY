package io.mpms.service.mysqldb;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.mpms.model.data.DelayedTask;

import java.util.List;

/**
 * (Delayedtask)表服务接口
 *
 *
 * @since 2021-10-21 14:34:09
 */
public interface DelayedTaskService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DelayedTask queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<DelayedTask> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param delayedtask 实例对象
     * @return 实例对象
     */
    DelayedTask insert(DelayedTask delayedtask);

    /**
     * 修改数据
     *
     * @param delayedtask 实例对象
     * @return 实例对象
     */
    int update(DelayedTask delayedtask);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

    /**
     * 查询数据表全部内容
     *
     *
     * @return 数据表内MiniSysLog容
     */
    List<DelayedTask> queryAllData();

    DelayedTask getOneNotStartedTask();

    boolean deleteTaskById(Integer id);

    JSONObject addAllTask(String taskList);

    JSONArray getTaskStatus();

    String getNodeId();

    void setNodeId(String nodeId);

    int deleteByTaskId(Integer id);
}