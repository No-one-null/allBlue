package com.zhao.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    public static final Boolean isNumber(String str){
        if(str==null){
            return false;
        }
        // 正整数
        String regEx = "^[1-9]\\d*$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regEx);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        // 字符串是否与正则表达式相匹配
//        System.out.println("结果" + matcher.matches());
        return matcher.matches();
    }

    public static final Boolean isExist(String[] arr,String str){
        for (String item: arr){
            if(item.equals(str)){
                return true;
            }
        }
        return false;
    }
}
