package com.zhao.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wildcard {
    static String[] str = {"_", "%"};
    static List<String> stringList = new ArrayList<String>(Arrays.asList(str));

    static public String wildcard(String word) {
        if (stringList.contains(word)) {
            word = "\\" + word;
        }
        return word;
    }

    static public Boolean isWildcard() {
        return false;
    }
}
