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
}