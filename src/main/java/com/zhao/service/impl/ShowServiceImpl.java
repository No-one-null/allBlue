package com.zhao.service.impl;

import com.zhao.mapper.*;
import com.zhao.pojo.*;
import com.zhao.util.PageInfo;
import com.zhao.service.ShowService;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static com.zhao.util.CommonUtil.*;
import static com.zhao.util.Constant.*;

@Service
public class ShowServiceImpl implements ShowService {
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
    public List<AcItems> sort(String category, int start, int end) {
        return acItemsMapper.sort(category, start, end);
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
    public float calRating(String acId, float rating)  throws Exception{
        float newRating = markMapper.avgRating(Integer.parseInt(acId));
        float realRating = (float) Math.round(newRating * 10) / 10;
        System.out.println("newRating" + newRating + "RealRating" + realRating);
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
        System.out.println(pageInfo);
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
    public int addTalk(Talk talk) {
        talk.setTime(new Date());
        return talkMapper.insertTalk(talk);
    }

    @Override
    public List<Talk> showAllTalk() {
        return talkMapper.selectAll(1, "time", "DESC");
    }

    @Override
    public Talk showTalk(String tid, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("currentUser");
        if (tid != null && isNumber(tid)) {
            Talk talk = talkMapper.selectOne(Integer.parseInt(tid));
            if (talk == null) {
                return null;
            }
            if (talk.getStatus() >= 2 && talk.getUid() != user.getUid()) {
                return null;
            } /*else {
                System.out.println(talk.getStatus() <= 0 && talk.getUid() != user.getUid());
            }*/
            return talk;
        }
        return null;
    }

    @Transactional
    @Override
    public boolean addComment(Comment comment) {
        comment.setTime(new Date());
        int result = commentMapper.insertOne(comment);
        return result == 1;
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
        List<String> roles = userMapper.selectRolesByUsername(user.getUsername());
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
    public List<?> findByWord(String tb, String conditions, String word, String order) throws Exception{
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
        if (orderField.equals("time")||orderField.equals("rating")) {
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
