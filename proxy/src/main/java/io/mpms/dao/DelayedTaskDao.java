package io.mpms.dao;

import io.jpom.model.data.DelayedTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Delayedtask)表数据库访问层
 *
 *
 * @since 2021-10-21 14:34:09
 */
@Mapper
public interface DelayedTaskDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    DelayedTask queryById(@Param("taskNodeId") String nodeId, @Param("id") Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<DelayedTask> queryAllByLimit(@Param("taskNodeId") String nodeId, @Param("offset") int offset,
                                      @Param("limit") int limit);

}