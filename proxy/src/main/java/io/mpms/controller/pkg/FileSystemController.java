package io.jpom.controller;

import cn.jiangzeyin.common.JsonMessage;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.jpom.api.req.FilePathReq;
import io.jpom.api.resp.FileResp;
import io.jpom.model.NorMalPathTreeEntity;
import io.jpom.util.FileOperatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/fs")
public class FileSystemController {
    private static Tika tika = new Tika();



    public JsonMessage ls(@RequestBody FilePathReq filePathReq) {
        String filterName = "";
        int pageNum = filePathReq.getPageNum();
        int pageSize = filePathReq.getPageSize();
        Page<FileResp> page = PageHelper.startPage(pageNum, pageSize);
        int startRow = (int) page.getStartRow();
        int endRow = (int) page.getEndRow();
        ArrayList<FileResp> fileResps = new ArrayList<>();
        String path = filePathReq.getPath();
        File file = new File(path);
        if (!file.exists() && !file.getParentFile().exists()) {
            return new JsonMessage(500, "目录或文件不存在", fileResps);
        }
        if (!file.exists() && file.getParentFile().exists()) {
            filterName = file.getName();
            file = file.getParentFile();
        }
        if (file.isFile()) {
            filterName = file.getName();
            file = file.getParentFile();
        }
        String finalFilterName = filterName;
        File[] files = file.listFiles((dir, name) -> name.equals(finalFilterName) || name.startsWith(finalFilterName));
        int index = 0;

        assert files != null;
        for (File file1 : files) {
            if (index < startRow) {
                index++;
                continue;
            }
            FileResp fileResp = new FileResp();
            fileResp.setName(file1.getName());
            if (file1.isDirectory()) {
                fileResp.setType("directory");
                fileResp.setIsRead(false);
            } else {
                if (file.getPath().equals("/dev")) {
                    try (FileInputStream fileInputStream = new FileInputStream(file1)) {
                        if (file1.canRead() && fileInputStream.available() > 0) {
                            String type = tika.detect(file1);
                            fileResp.setType(type);
                            fileResp.setIsRead(isFileRead(type));
                        } else {
                            fileResp.setType("不可读文件[设备文件]");
                            fileResp.setIsRead(false);
                        }
                    } catch (Exception e) {
                        log.error("解析文件类型异常\\n{}", ExceptionUtils.getStackTrace(e));
                    }
                } else {
                    try {
                        String type = tika.detect(file1);
                        fileResp.setType(type);
                        fileResp.setIsRead(isFileRead(type));
                    } catch (Exception e) {
                        log.error("解析文件类型异常\\n{}",  ExceptionUtils.getStackTrace(e));
                    }
                }
            }

            if (index >= startRow && index < endRow) {
                fileResps.add(fileResp);
            }
            if (index >= endRow) {
                break;
            }
            index++;

        }
        page.addAll(fileResps);
        page.setTotal(files.length);
        return new JsonMessage(200, "success", new PageInfo<>(page));
    }






}
