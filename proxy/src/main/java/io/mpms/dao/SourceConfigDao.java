package io.mpms.dao;

import io.jpom.model.system.SourceConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Sourceconfig)表数据库访问层
 *
 *
 * @since 2021-10-25 17:05:11
 */
@Mapper
public interface SourceConfigDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SourceConfig queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<SourceConfig> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);
}