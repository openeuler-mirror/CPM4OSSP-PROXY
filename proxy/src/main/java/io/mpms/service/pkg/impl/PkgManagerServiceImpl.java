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
