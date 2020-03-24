package com.zhao.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 共通的常量定义
 */
public interface Constant {
    public static final String[] TYPE_ARRAY = {"动画情报", "漫画情报", "影视化情报", "游戏情报",
            "周边资讯", "漫展资讯", "其他情报"};
    public static final String ERR404 = "/error/err404";
    public static final String ERR403 = "/error/err403";
    public static final String UPLOAD_PATH = "D:/allBlue";
    public static final String UPLOAD_TOPIC = UPLOAD_PATH + "/topic";
    public static final String UPLOAD_COVER = UPLOAD_PATH + "/images/cover/";
    public static final String UPLOAD_AVATAR = UPLOAD_PATH + "/images/avatar/";
    public static final int CONTENT_MAXSIZE = 500;
    public static final int COMMENT_MAXSIZE = 150;
    public static final String[] COMPLAINT_ARRAY = {"垃圾广告", "话题不相关",
            "引战", "色情", "人身攻击", "违法信息"};
    public static final String[] COMMON_COUNTRY = {"中国大陆", "日本", "韩国", "港台", "美国", "其他"};
    public static final String[] PROGRESS_ARRAY = {"暂无", "想看", "在看", "看过", "搁置", "已弃"};
    public static final String[] ACCEPT_IMAGES = {".jpg", ".png", ".jpeg", ".jfif"};
    public final static Map<String, String> OPEN_CLOSE = new HashMap<String, String>() {{
        put("open", "高级搜索");
        put("close", "关闭");
    }};
}
