package io.mpms.service.mysqldb.impl;

import io.mpms.dao.SourceConfigDao;
import io.mpms.model.system.SourceConfig;
import io.mpms.service.mysqldb.SourceConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Sourceconfig)表服务实现类
 *
 *
 * @since 2021-10-25 17:05:11
 */
@Service("sourceconfigService")
public class sourceConfigServiceImpl implements SourceConfigService {

    @Resource
    private SourceConfigDao sourceconfigDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public SourceConfig queryById(Integer id) {
        return this.sourceconfigDao.queryById(id);
    }

    @Override
    public List<SourceConfig> selectAll(){
        return this.sourceconfigDao.selectAll();
    }

    @Override
    public boolean isFoundByEntity(SourceConfig sourceconfig) {
        return !sourceconfigDao.queryAll(sourceconfig).isEmpty();
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<SourceConfig> queryAllByLimit(int offset, int limit) {
        return this.sourceconfigDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param sourceconfig 实例对象
     * @return 实例对象
     */
    @Override
    public SourceConfig insert(SourceConfig sourceconfig) {
        this.sourceconfigDao.insert(sourceconfig);
        return sourceconfig;
    }

    /**
     * 修改数据
     *
     * @param sourceconfig 实例对象
     * @return 实例对象
     */
    @Override
    public SourceConfig update(SourceConfig sourceconfig) {
        this.sourceconfigDao.update(sourceconfig);
        return this.queryById(sourceconfig.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.sourceconfigDao.deleteById(id) > 0;
    }
}