package com.zhao.test;


import com.zhao.pojo.Talk;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.zhao.util.CommonUtil.getImgSrc;

public class Test1 {

    @Test
    public void dateFormat() {
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar);
        System.out.print(calendar.getTime() + "|");
        System.out.print(Calendar.YEAR + "|");
        System.out.println(calendar.getWeekYear());
        Date date = new Date();
        System.out.println(date);
        System.out.println(date.getTime());
    }

    @Test
    public void substring() {
        String str = "/allBlue/images/news/1585191424633.jpg";
        System.out.println(str.substring(str.lastIndexOf('/') + 1));
    }

    @Test
    public void src() {
        String html = "<p><img src=\"/allBlue/images/news/1585191415548.jpg\" style=\"max-width:30%;\">1<img src=\"/allBlue/images/news/1585191424633.jpg\" style=\"max-width: 100%;\"><br></p>";
        System.out.println(getImgSrc(html));
        List<String> list = getImgSrc(html);
        String[] str = list.toArray(new String[0]);
        System.out.println(Arrays.toString(str));
        System.out.println(list.get(0));
        System.out.println(getImgSrc("").get(0));
    }

    @Test
    public void testThrow() throws Exception {
        throw new Exception("123");
    }

    @Test
    public void testCatch() {
        try {
            testThrow();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
            System.out.println(e.getMessage());
        }
    }
}
