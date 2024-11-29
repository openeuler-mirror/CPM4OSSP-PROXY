package io.jpom.service.pkg;

/*
软件包管理
 */
public interface PkgManagerService {
    void insertPkgMessage();

    void pkgInitialized();

    void deletePkgByNodeId(Integer nodeId);

    void packageRefresh(Integer nodeId);
}
