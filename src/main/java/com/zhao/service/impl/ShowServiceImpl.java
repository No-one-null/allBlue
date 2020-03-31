package com.zhao.service.impl;

import com.zhao.mapper.*;
import com.zhao.pojo.*;
import com.zhao.util.PageInfo;
import com.zhao.service.ShowService;

import org.apache.commons.io.FileUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zhao.util.CommonUtil.*;
import static com.zhao.util.Constant.*;

@Service
public class ShowServiceImpl implements ShowService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private AcItemsMapper acItemsMapper;
    @Resource
    private MarkMapper markMapper;
    @Resource
    private AcNewsMapper acNewsMapper;
    @Resource
    private TalkMapper talkMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ComplaintMapper complaintMapper;

    @Override
    public AcItems findById(String id) {
        return acItemsMapper.selectById(Integer.valueOf(id));
    }

    @Override
    public PageInfo showAcItems(String path, String orderField, String orderType, String page) {
        String word;
        switch (path) {
            case "anime":
                word = "A";
                break;
            case "comic":
                word = "C";
                break;
            default:
                word = path;
        }
        PageInfo pageInfo = new PageInfo();
        int pSize = 16;
        int pNum = 1;
        if (isNumber(page)) {
            pNum = Integer.parseInt(page);
        }
        pageInfo.setPageNumber(pNum);
        pageInfo.setPageSize(pSize);
        int pStart = pSize * (pNum - 1);
        long count = acItemsMapper.countByParam("category", word);
        List<AcItems> list = acItemsMapper.selectByParams("category", word, orderField, orderType, pStart, pSize);
        pageInfo.setList(list);
        pageInfo.setTotal(count % pSize == 0 ? count / pSize : count / pSize + 1);
        return pageInfo;
    }

    @Override
    public List<?> sort(String category, int start, int size) {
        String key = "index_" + category;
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(List.class));
        List<?> list = (List<?>) this.redisTemplate.opsForValue().get(key);
        if (list == null) {
            list = acItemsMapper.sort(category, start, size);
            this.redisTemplate.opsForValue().set(key, list);
        }
        return list;
    }

    @Transactional
    @Override
    public Boolean addMark(Mark mark) {
        Mark mark1 = markMapper.selectById(mark.getAcId(), mark.getUid());
        int flag = 0;
        if (mark1 == null) {
            flag = markMapper.insertMark(mark);
        }
        int flag2 = markMapper.updateMark(mark);
        return flag != 0 || flag2 != 0;
    }

    @Override
    public Mark findMarkOne(int uid, int acId) {
        return markMapper.selectById(uid, acId);
    }

    @Transactional
    @Override
    public float calRating(String acId, float rating) {
        float newRating = markMapper.avgRating(Integer.parseInt(acId));
        float realRating = (float) Math.round(newRating * 10) / 10;
        if (rating == realRating) {
            return rating;
        } else {
            int result = acItemsMapper.updateRating(realRating, Integer.parseInt(acId));
            System.out.println(result);
            return realRating;
        }
    }

    @Override
    public int sumRating(String acId) {
        return markMapper.countRating(Integer.parseInt(acId));
    }

    @Override
    public List<Mark> allComments(String acId) {
        return markMapper.selectComments(Integer.parseInt(acId));
    }

    @Override
    public PageInfo showPage(String path, String type, String pageNumber, String pageSize) {
        int pNum = 1;
        if (isNumber(pageNumber)) {
            pNum = Integer.parseInt(pageNumber);
        }
        int pSize = 10;
        if (isNumber(pageSize)) {
            pSize = Integer.parseInt(pageSize);
        }
        long count = 0;
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNumber(pNum);
        pageInfo.setPageSize(pSize);
        int pageStart = pSize * (pNum - 1);
        if (path.equalsIgnoreCase("news")) {
            count = acNewsMapper.countNotAll(type);
            pageInfo.setList(acNewsMapper.selectByParams(pageStart, pSize, type));
        }
        pageInfo.setTotal(count % pSize == 0 ? count / pSize : count / pSize + 1);
        return pageInfo;
    }

    @Override
    public AcNews showNews(String nid) {
        if (isNumber(nid)) {
            return acNewsMapper.selectById(Integer.parseInt(nid));
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public String addTalk(MultipartFile[] files, HttpServletRequest request) throws IOException {
        String message = "success";
        User user = (User) request.getSession().getAttribute("currentUser");
        if (user == null) {
            return "请登录!";
        }
        StringBuilder pictures = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyyMMddHHmmss");
        String userPath = UPLOAD_TOPIC + "/" + user.getUid();
        File userDir = new File(userPath);
        if (!userDir.exists()) {
            boolean res = userDir.mkdirs();
            if (!res) {
                System.out.println("用户文件夹创建失败");
            } else {
                System.out.println("创建用户文件夹");
            }
        }
        String newPath = "/" + sdf.format(new Date());
        String pathname = userPath + newPath;
        File dir = new File(pathname);
        if (files != null) {
            if (!dir.exists()) {
                boolean result = dir.mkdirs();
                if (!result) {
                    return "文件夹创建失败!";
                }
            }
            int index = 0;
            for (int i = files.length - 1; i >= 0; i--) {
                if (files[i].getSize() <= 0) {
                    continue;
                }
                //获取文件名
                String oldName = files[i].getOriginalFilename();
                System.out.println("files[" + i + "]的名字:" + oldName);
                String suffix = files[i].getOriginalFilename().substring(oldName.lastIndexOf("."));
                try {
                    String newName = "/" + (++index) + suffix;
                    files[i].transferTo(new File(pathname, newName));
                    pictures.append(newPath).append(newName).append(",");
                } catch (IOException e) {
                    e.printStackTrace();
                    return "上传失败!";
                }
            }
            if (!pictures.toString().equals("")) {
                pictures = new StringBuilder(pictures.substring(0, pictures.length() - 1));
            } else {
                FileUtils.forceDelete(dir);
            }
        }
        Talk talk = new Talk();
        talk.setPictures(pictures.toString());
        talk.setTopic(request.getParameter("topicStr"));
        String content = request.getParameter("content");
        if (content.length() > CONTENT_MAXSIZE) {
            content = content.substring(0, CONTENT_MAXSIZE);
        }
        talk.setContent(content);
        talk.setUid(user.getUid());
        System.out.println(talk);
        talk.setTime(new Date());
        int flag = 0;
        try {
            flag = talkMapper.insertTalk(talk);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (flag == 0) {
                if (dir.exists()) {
                    FileUtils.forceDelete(dir);
                }
                message = "保存失败!请检查输入是否有误";
            }
        }
        return message;
    }

    @Override
    public List<Talk> showAllTalk() {
        return talkMapper.selectAll(1, "time", "DESC");
    }

    @Override
    public Talk showTalk(String tid, User user) {
        System.out.println(user);
        if (tid != null && isNumber(tid)) {
            Talk talk = talkMapper.selectOne(Integer.parseInt(tid));
            if ((talk==null)||(talk.getStatus() > 1 &&( user == null || talk.getUid() != user.getUid()))) {
                return null;
            }
            return talk;
        }
        return null;
    }

    @Override
    public List<Comment> showComments(String tid) {
        if (tid != null && isNumber(tid)) {
            return commentMapper.selectAll(Integer.parseInt(tid));
        }
        return null;
    }

    @Override
    public boolean delComment(String id) {
        if (!isNumber(id)) {
            return false;
        }
        int flag = commentMapper.deleteOne(Integer.parseInt(id));
        return flag > 0;
    }

    @Transactional
    @Override
    public String removeTalk(String tid) throws IOException {
        if (!isNumber(tid)) {
            return "数据传输失败!";
        }
        Talk talk = talkMapper.selectOne(Integer.parseInt(tid));
        int result = talkMapper.deleteById(Integer.parseInt(tid));
        if (result > 0) {
            commentMapper.deleteByTid(Integer.parseInt(tid));
            String pic = talk.getPictures();
            if (!pic.equals("")) {
                pic = pic.substring(0, pic.indexOf('/', pic.indexOf('/') + 1));
                String path = UPLOAD_TOPIC + "/" + talk.getUser().getUid() + pic;
                System.out.println("删除" + path);
                File dir = new File(path);
                if (dir.exists()) {
                    FileUtils.forceDelete(dir);
                }
            }
        } else {
            return "删除失败!讨论不存在或者已经被删除!";
        }
        return "success";
    }

    @Transactional
    @Override
    public String sendComplaint(Complaint complaint) {
        List<Complaint> list = complaintMapper.selByUidTid(complaint.getFromUid(),
                complaint.getToId(), complaint.getToType());
        if (list.size() > 0) {
            return "您已举报过!";
        }
        complaint.setTime(new Date());
        int flag = complaintMapper.insertOne(complaint);
        if (flag <= 0) {
            return "发送失败!";
        }
        return "success";
    }

    @Override
    public Map<String, Object> showUserInfo(String uid, String page) {
        if (!isNumber(uid)) {
            return null;
        }
        if (page == null || page.equals("")) {
            page = "info";
        }
        System.out.println(uid);
        int id = Integer.parseInt(uid);
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        User user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        map.put("user", user);
        Set<String> roles = userMapper.selectRolesByUid(user.getUid());
        map.put("roles", roles);
        if (page.equalsIgnoreCase("talk")) {
            List<Talk> talks = talkMapper.selectByUid(id, "time", "DESC");
            map.put("talks", talks);
        } else if (page.equalsIgnoreCase("mark")) {
            List<Mark> marks = markMapper.selectByUid(id, "create_date", "DESC");
            map.put("marks", marks);
        } else if (page.equalsIgnoreCase("info")) {
            System.out.println("编辑用户信息");
        } else {
            return null;
        }
        return map;
    }

    @Override
    public String updateUser(User user, MultipartFile file) throws IOException {
        User original = userMapper.selectById(user.getUid());
        if (original.equals(user) && file.getSize() <= 0) {
            return "没有修改内容!";
        }
        String email = user.getEmail();
        if (!email.equalsIgnoreCase(original.getEmail())) {
            long num = userMapper.countField("email", email);
            if (num > 0) {
                return "邮箱已存在!";
            }
        }
        String fileName = user.getPhoto();
        if (file.getSize() > 0) {
            String prefix = user.getUid() + "-" + System.currentTimeMillis();
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            if (!isExist(ACCEPT_IMAGES, suffix)) {
                return "文件格式不正确!";
            }
            fileName = prefix + suffix;
            file.transferTo(new File(UPLOAD_AVATAR, fileName));
            user.setPhoto(fileName);
            if (!original.getPhoto().equals("")) {
                String dir = UPLOAD_AVATAR + original.getPhoto();
                FileUtils.forceDelete(new File(dir));
            }
        }
        System.out.println(fileName + original.getPhoto());
        int flag = userMapper.updateUser(user);
        if (flag > 0) {
            return "success";
        }
        if (!fileName.equals(original.getPhoto())) {
            String str = UPLOAD_AVATAR + fileName;
            FileUtils.forceDelete(new File(str));
        }
        return "数据库更新失败!";
    }

    @Override
    public User showUser(int uid) {
        return userMapper.selectById(uid);
    }

    @Override
    public List<?> findByWord(String tb, String conditions, String word, String order) throws Exception {
        if (isExist(SENSITIVE_WORDS, word)) {
            throw new Exception("敏感词!");
        }
        List<?> list;
        if (word == null || word.trim().equals("")) {
            return new ArrayList<>();
        }
        String field;
        if (conditions == null || conditions.equals("")) {
            field = "all";
        } else {
            field = conditions;
        }
        String key = repWildcard(word);
        String orderField = order;
        if (order == null || order.equals("") || order.equals("default")) {
            switch (tb) {
                case "acItems":
                    orderField = "name";
                    break;
                case "talk":
                    orderField = "time";
                    break;
                default:
                    orderField = "";
            }
        }
        String orderType = "ASC";
        if (orderField.equals("time") || orderField.equals("rating")) {
            orderType = "DESC";
        }
        switch (tb) {
            case "acItems":
                list = acItemsMapper.selectByParam(field, key, orderField, orderType);
                break;
            case "talk":
                list = talkMapper.selectByParams(field, key, orderField, orderType, 0, 0, 1);
                break;
            default:
                list = new ArrayList<>();
        }
        return list;
    }
}
