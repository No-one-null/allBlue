package com.zhao.util;

/**
 * 共通的常量定义
 */
public interface Constant {
    public static final String[] TYPE_ARRAY = {"动画情报", "漫画情报", "其他情报", "周边资讯", "漫展资讯"};
    public static final String ReERR404 = "redirect:/error404";
    public static final String ERR404 = "/error/error404";
    public static final String UPLOAD_PATH = "D:/allBlue";
    public static final String UPLOAD_TOPIC = UPLOAD_PATH + "/topic";
    public static final String UPLOAD_COVER = UPLOAD_PATH + "/images/cover/";
    public static final int CONTENT_MAXSIZE = 500;
    public static final int COMMENT_MAXSIZE = 150;
    public static final String[] COMPLAINT_ARRAY = {"垃圾广告", "话题不相关",
            "引战", "色情", "人身攻击", "违法信息"};
    public static final String[] AC_CATEGORY = {"动画", "漫画"};
    public static final String[] COMMON_COUNTRY={"中国大陆","日本","韩国","港台","美国","其他"};
}
