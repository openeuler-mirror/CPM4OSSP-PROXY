package io.jpom.model.system;

import lombok.Data;

/**
 * @author zywang
 * @date 2022/10/27 17:58:13
 **/

@Data
public class NetInodePort {

    String inode;

    String status;

    String proto;

    String port;
}
