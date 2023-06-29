package io.mpms.service.mysqldb.impl;

import cn.hutool.core.date.DateTime;
import io.mpms.dao.MiniSysLogDao;
import io.mpms.model.data.MiniSysLog;
import io.mpms.service.mysqldb.MiniSysLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

/**
 * 凝思系统软件安装管理平台系统日志表(Minisyslog)表服务实现类
 *
 * 
 * @since 2021-10-22 09:53:54
 */
@Service("miniSysLogService")
public class MiniSysLogServiceImpl implements MiniSysLogService {

    @Resource
    private MiniSysLogDao minisyslogDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public MiniSysLog queryById(Integer id) {
        return this.minisyslogDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<MiniSysLog> queryAllByLimit(int offset, int limit) {
        return this.minisyslogDao.queryAllByLimit(offset, limit);
    }

    /**
     * 查询所有日志数据
     *
     * @return 对象列表
     */
    @Override
    public List<MiniSysLog> queryAll() {
        List<MiniSysLog> logList = this.minisyslogDao.queryAll();
        return logList;
    }

    /**
     * 新增数据
     *
     * @return 影响行数
     */
    @Override
    public Integer insert(int level, String content, String extra) {
        if (MiniSysLog.getIp() == null || MiniSysLog.getIp() == ""){
            try{
                Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
                // 遍历网络接口
                while (enumeration.hasMoreElements()) {
                    NetworkInterface face = enumeration.nextElement();
                    if (face.isLoopback() || face.isVirtual()) { //
                        continue;
                    }
                    Enumeration<InetAddress> inets = face.getInetAddresses();
                    // 遍历网络地址
                    while (inets.hasMoreElements()) {
                        InetAddress addr = inets.nextElement();
                        if (addr.isLoopbackAddress() || !addr.isSiteLocalAddress() || addr.isAnyLocalAddress()) {
                            continue;
                        }
                        // 获取本机ip地址
                        String hostAddress = addr.getHostAddress();
                        if(hostAddress != null && hostAddress != ""){
                            MiniSysLog.setIp(hostAddress);
                            break;
                        }
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        // 二次校验是否为空，仍为设置默认值，保证显示的时候肯定有ip
        if (MiniSysLog.getIp() == null || MiniSysLog.getIp() == ""){
            MiniSysLog.setIp("127.0.0.1");
        }
        MiniSysLog miniSysLog = new MiniSysLog(level, content, extra);
        return this.minisyslogDao.insert(miniSysLog);
    }

    /**
     * 新增数据
     *
     * @return 影响行数
     */
    @Override
    public Integer insertMultiple(List<MiniSysLog> insertList) {
        if (insertList.isEmpty()) {
            return -1;
        } else {
            return this.minisyslogDao.insertMultiple(insertList);
        }
    }

    /**
     * 修改数据
     */
    @Override
    public MiniSysLog update(int id, int type, String ip, int level, DateTime time, String content, String extra) {
        MiniSysLog miniSysLog = new MiniSysLog();
        miniSysLog.setId(0);
        miniSysLog.setType(0);
        miniSysLog.setLevel(level);
        miniSysLog.setTime(DateTime.now().toString());
        miniSysLog.setContent(content);
        miniSysLog.setExtra(extra);
        this.minisyslogDao.update(miniSysLog);
        return this.queryById(miniSysLog.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.minisyslogDao.deleteById(id) > 0;
    }
}
