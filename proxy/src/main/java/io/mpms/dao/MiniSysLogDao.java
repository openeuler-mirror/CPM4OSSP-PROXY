package io.mpms.dao;

import io.jpom.model.data.MiniSysLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 凝思系统软件安装管理平台系统日志表(Minisyslog)表数据库访问层
 *
 *
 * @since 2021-10-22 09:53:54
 */
@Mapper
public interface MiniSysLogDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MiniSysLog queryById(Integer id);


    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<MiniSysLog> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);
}
