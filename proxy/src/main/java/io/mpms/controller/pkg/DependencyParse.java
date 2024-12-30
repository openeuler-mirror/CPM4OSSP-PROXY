package io.jpom.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DependencyParse {
    public static JSONObject parseDependency(String dependencyString) {
        JSONObject data = new JSONObject();
        JSONArray dependencies = new JSONArray();
        String[] lines = dependencyString.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String[] dependence = lines[i].trim().split("\\s+");
            if (1 < dependence.length) {
                int tmpi = i + 1;
                JSONObject item = new JSONObject();
                JSONArray rel = new JSONArray();
                while (tmpi < lines.length && (lines[tmpi].trim().split("\\s+").length < 2)) {  //递归检索某个依赖包的列表内容
                    rel.add(lines[tmpi].trim());
                    tmpi++;
                    if (tmpi > lines.length - 1 || 1 != lines[tmpi].trim().split("\\s+").length) {
                        JSONObject itemValue = new JSONObject();
                        itemValue.put(lines[i].trim().split("\\s")[1].trim(), rel);
                        item.put(lines[i].trim().split("\\s")[0].trim().replace(":", ""), itemValue);
                        break;
                    }
                }
                if (tmpi == i + 1) {
                    item.put(dependence[0].trim().replace(":", ""), dependence[1].trim());
                }
                dependencies.add(item);
            }
        }
        data.put(lines[0], dependencies);
        return data;
    }
}
