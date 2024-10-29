package io.jpom.util;

import cn.hutool.core.io.FileUtil;
import cn.jiangzeyin.common.JsonMessage;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;

/**
 * @author hzw
 * @date 2023年07月04日 上午11:11
 */

@Slf4j
public class UidUtil {
    static final String PASSWD_PATH = "/etc/passwd";

    /**
     * uid 获取用户名
     */
    public static String getUserNameByUid(String uid) {
        File file = new File(PASSWD_PATH);
        String userName = "";
        if (file.exists() && file.isFile()) {
            ArrayList<String> readPasswdFiles = FileUtil.readUtf8Lines(file, new ArrayList<>());
            for (String passwordItem : readPasswdFiles) {
                if (passwordItem.contains(uid)) {
                    String[] passwordItemSplit = passwordItem.split(":");
                    if (uid.equals(passwordItemSplit[2])) {
                        userName = passwordItemSplit[0];
                        break;
                    }
                }
            }
        }
        if (userName.isEmpty()) {
            userName = "NULL";
        }
        return userName;
    }

    public static String getAllUid() {
        File file = new File(PASSWD_PATH);
        JSONArray retArray = new JSONArray();
        String retString;

        if (file.exists() && file.isFile()) {
            ArrayList<String> readPasswdFiles = FileUtil.readUtf8Lines(file, new ArrayList<>());
            for (String passwordItem : readPasswdFiles) {
                String[] items = passwordItem.split(":");
                JSONObject arrayItem = new JSONObject();
                arrayItem.put("username", items[0]);
                arrayItem.put("uid", items[2]);
                retArray.add(arrayItem);
            }
            retString = JsonMessage.getString(200, "查询成功", retArray);
        } else {
            retString = JsonMessage.getString(404, "passwd文件不存在");
        }
        return retString;
    }
}
