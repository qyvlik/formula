package io.github.qyvlik.formula.common.base;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

@Data
public class ResultMessageUtils {
    public static String i18nMessage(String[] args) {
        List<String> list = Lists.newArrayList();
        list.addAll(Arrays.asList(args));
        return JSON.toJSONString(list);
    }

    public static String i18nMessage(List<String> args) {
        return JSON.toJSONString(args);
    }

    public static String[] parse(String message) {
        if (StringUtils.isBlank(message)) {
            return null;
        }
        List<String> list = JSON.parseArray(message, String.class);
        String[] args = new String[list.size()];
        list.toArray(args);
        return args;
    }
}
