package io.jpom.model.system;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zywang
 * @date 2022/10/27 16:31:39
 **/

@Data
public class ProcessSocket {
    private String PROGRAM;
    private String pid;

    private List<FileInode> processInodeList;

    public ProcessSocket(){
        processInodeList = new ArrayList<>();
    }

    public void addToLinkFiles(FileInode fileInode){
        processInodeList.add(fileInode);
    }
}
