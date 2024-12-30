package io.jpom.model.system;

import lombok.Data;

import java.io.File;

/**
 * @author zywang
 * @date 2022/10/27 17:25:21
 **/

@Data
public class FileInode {
    private File file;

    private String inode;
}
