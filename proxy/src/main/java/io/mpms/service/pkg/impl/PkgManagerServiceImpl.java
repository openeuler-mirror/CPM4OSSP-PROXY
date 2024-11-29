package io.jpom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jpom.common.commander.impl.LinuxSystemCommander;
import io.jpom.dao.NodeInfoMapper;
import io.jpom.model.NodeInfo;
import io.jpom.model.PkgEntity;
import io.jpom.service.PkgMessageService;
import io.jpom.service.pkg.PkgManagerService;
import io.jpom.util.IpAddressUtils;
import io.jpom.util.SpringUtils;
import io.jpom.util.SystemUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PkgManagerServiceImpl implements PkgManagerService {
    @Resource
    PkgMessageService pkgMessageService;

    @Resource
    NodeInfoMapper nodeInfoMapper;

    @Resource
    SystemUtils systemUtils;

    public static Integer proxyId;

    public static List<String> pkgEntityList = new ArrayList<>();

    /*
     注册时导入数据
     */
    @Override
    public void insertPkgMessage() {
        NodeInfo nodeInfo = nodeInfoMapper.selectByPrimaryKey(String.valueOf(getNodeId()));
        JSONObject version = systemUtils.getVersion();
        List<PkgEntity> allPackageInfo = new LinuxSystemCommander().getAllPackageInfo("0");

        nodeInfo.setSystem(version.getString("system").trim());
        nodeInfo.setKernel(version.getString("kernel").trim());
        nodeInfo.setInitialized(true);

        pkgMessageService.saveBatch(allPackageInfo);
        nodeInfoMapper.updateById(nodeInfo);
    }

    /*
      初始化
     */
    public void pkgInitialized() {
        NodeInfo nodeInfo = nodeInfoMapper.selectByUrl(IpAddressUtils.getIp() + ":2125");
        if (!ObjectUtils.isEmpty(nodeInfo)) {
            if (!nodeInfo.isInitialized()) {
                proxyId = nodeInfo.getId();
                insertPkgMessage();
            }
        }
    }


    /*
获取节点id
 */
    public Integer getNodeId() {
        if (ObjectUtils.isEmpty(proxyId)) {
            QueryWrapper<NodeInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("url", IpAddressUtils.getIp() + ":2125");
            NodeInfoMapper nodeInfoMapper = SpringUtils.getBean("nodeInfoMapper");
            NodeInfo nodeInfo = nodeInfoMapper.selectOne(queryWrapper);
            proxyId = nodeInfo.getId();
        }
        return proxyId;
    }
}
