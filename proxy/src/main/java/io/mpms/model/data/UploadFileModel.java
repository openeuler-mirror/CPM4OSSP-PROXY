package io.mpms.model.data;

import cn.hutool.core.io.FileUtil;
import io.jpom.model.BaseModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class UploadFileModel extends BaseModel {
    private long size = 0;
    private long completeSize = 0;
    private String savePath;
    private String version;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCompleteSize() {
        return completeSize;
    }

    public void setCompleteSize(long completeSize) {
        this.completeSize = completeSize;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}
