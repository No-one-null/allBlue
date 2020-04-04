package com.zhao.service.impl;

import com.zhao.mapper.*;
import com.zhao.pojo.*;
import com.zhao.service.DataService;
import com.zhao.util.PageInfo;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.zhao.util.CommonUtil.getImgSrc;
import static com.zhao.util.CommonUtil.isNumber;
import static com.zhao.util.Constant.*;

@Service
public class DataServiceImpl implements DataService {
    @Resource
    private AcItemsMapper acItemsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private AcNewsMapper acNewsMapper;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private TalkMapper talkMapper;
    @Resource
    private MsgContentMapper msgContentMapper;
    @Resource
    private MsgUserMapper msgUserMapper;

    /**
     * 分页
     */
    @Override
    public PageInfo showPage(String tbName, String pageNumber, String pageSize) {
        int pSize = 10;
        if (isNumber(pageSize)) {
            pSize = Integer.parseInt(pageSize);
        }
        int pNum = 1;
        if (isNumber(pageNumber)) {
            pNum = Integer.parseInt(pageNumber);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNumber(pNum);
        pageInfo.setPageSize(pSize);
        int pageStart = pSize * (pNum - 1);
        long count = 0;
        if (tbName.equalsIgnoreCase("user")) {
            pageInfo.setList(userMapper.selectByPage(pageStart, pSize));
            count = userMapper.selectCount();
        }
        if (tbName.equalsIgnoreCase("ac")) {
            pageInfo.setList(acItemsMapper.selectByPage(pageStart, pSize));
            count = acItemsMapper.selectCount();
        }
        if (tbName.equalsIgnoreCase("news")) {
            pageInfo.setList(acNewsMapper.selectByPage(pageStart, pSize));
            count = acNewsMapper.CountAll();
        }
        if (tbName.equals("complaint")) {
            count = complaintMapper.countAll();
            pageInfo.setList(complaintMapper.selectByPage(pageStart, pSize));
        }
        if (tbName.equals("topic")) {
            count = talkMapper.countTalk(-1);
            pageInfo.setList(talkMapper.selectByPage(pageStart, pSize, 2,
                    "status", "ASC"));
        }
        pageInfo.setCount(count);
        pageInfo.setTotal(count % pSize == 0 ? count / pSize : count / pSize + 1);
        return pageInfo;
    }

    @Transactional
    @Override
    public String addAcItems(MultipartFile file, AcItems acItems) throws IOException {
        String message;
        String fullName = "";
        List<?> list = acItemsMapper.selectByNames(acItems);
        if (list.size() > 0) {
            return "作品可能已存在!";
        }
        if (acItems.getInfo().length() > 300) {
            return "简介太长！";
        }
        if (file.getSize() > 0) {
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;
            String path = UPLOAD_PATH + "/images/cover/";
            fullName = path + fileName;
            file.transferTo(new File(path, fileName));
            acItems.setImage(fileName);
        } else {
            acItems.setImage("");
        }
        System.out.println(acItems);
        int addResult = acItemsMapper.insertAcItem(acItems);
        if (addResult > 0) {
            message = "success";
        } else {
            message = "数据库新增失败!";
            FileUtils.forceDeleteOnExit(new File(fullName));
        }
        return message;
    }

    @Override
    public int addAcItems(AcItems acItems) {
        return this.acItemsMapper.insertAcItem(acItems);
    }

    @Override
    public List<User> showAll() {
        return this.userMapper.showAll();
    }

    @Override
    public List<AcItems> allAcItems() {
        return this.acItemsMapper.selectAll();
    }

    @Override
    public List<?> findByWord(String table, String keyword) {
        if ((keyword.equals("_")) || (keyword.equals("%"))) {
            keyword = "\\" + keyword;
        }
        if (table.equals("ac")) {
            return acItemsMapper.selectByKey(keyword);
        }
        if (table.equals("news")) {
            return acNewsMapper.selectByKeyword(keyword);
        }
        return null;
    }

    @Override
    public AcItems findById(String id) {
        if (!isNumber(id)) {
            return null;
        }
        return acItemsMapper.selectById(Integer.valueOf(id));
    }

    @Transactional
    @Override
    public String updateOne(AcItems acItems, MultipartFile file) throws IOException {
        AcItems ac = acItemsMapper.selectById(acItems.getId());
        if (ac.equals(acItems) && file.getSize() == 0) {
            return "未修改内容!";
        }
        String message;
        String fileName;
        if (file.getSize() > 0) {
            if (!acItems.getImage().equals("")) {
                String dir = UPLOAD_COVER + acItems.getImage();
                FileUtils.forceDelete(new File(dir));
                System.out.println(dir);
            }
            String prefix = UUID.randomUUID().toString();
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            fileName = prefix + suffix;
            file.transferTo(new File(UPLOAD_COVER, fileName));
            acItems.setImage(fileName);
        } else {
            System.out.println("不修改图片");
        }
        System.out.println("\n【" + acItems + "】\n");
        int result = acItemsMapper.updateById(acItems);
        if (result > 0) {
            message = "success";
        } else {
            message = "数据库更新失败";
        }
        return message;
    }

    @Transactional
    @Override
    public boolean DeleteOne(int id) throws IOException {
        AcItems ac = acItemsMapper.selectById(id);
        if (!ac.getImage().equals("")) {
            FileUtils.forceDelete(new File(UPLOAD_COVER + ac.getImage()));
        }
        int flag = acItemsMapper.updateStatus(id, 0);
        return flag > 0;
    }

    @Transactional
    @Override
    public String addNews(AcNews acNews, String[] filenames) throws IOException {
        List<String> list = getImgSrc(acNews.getNewsContent());
        acNews.setNewsDate(new Date());
        int flag = acNewsMapper.insertAcNews(acNews);
        if (flag != 0) {
            saveFile(list);
            return "success";
        }
        return "update_fail";
    }

    @Override
    public AcNews findNewsById(String id) {
        if (id != null) {
            return acNewsMapper.selectOneById(Integer.parseInt(id));
        } else {
            return null;
        }
    }

    @Override
    public String editNews(String id, String content, String type, String status, String[] filenames) throws IOException {
        if (!isNumber(id)) {
            return "错误的数据！";
        }
        AcNews acNews = acNewsMapper.selectById(Integer.parseInt(id));
        int result = acNewsMapper.updateContent(Integer.parseInt(id), content, type, Integer.parseInt(status));
        if (result > 0) {
            List<String> oldImgs = getImgSrc(acNews.getNewsContent());
            List<String> newImgs = getImgSrc(content);
            saveFile(newImgs);
            oldImgs.removeAll(newImgs);
            for (String s : oldImgs) {
                String filename = s.substring(s.lastIndexOf('/') + 1);
                String path = UPLOAD_PATH + "/images/news/" + filename;
                System.out.println("删除" + path);
                File file = new File(path);
                if (file.exists()) {
                    FileUtils.forceDelete(file);
                }
            }
            return "success";
        }
        return "数据库更新失败！";
    }

    @Override
    public Map<String, Object> findTalk(String type, int tid) {
        Map<String, Object> map = new HashMap<>();
        if (type.equals("topic")) {
            Talk talk = talkMapper.selectOne(tid);
            if (talk != null) {
                map.put("message", "success");
                System.out.println(talk);
                map.put("object", talk);
            } else {
                map.put("message", "查询失败!");
            }
        }
        return map;
    }

    @Override
    public String checkTopic(String type, int tid, String deal, int uid) {
        int status;
        switch (deal) {
            case "pass":
                status = 1;
                break;
            case "hide":
                status = 2;
                break;
            default:
                return "未能识别参数,请检查!";
        }
        if (type.equals("topic")) {
            int flag = talkMapper.updateStatus(tid, status);
            if (flag > 0) {
                int update = complaintMapper.updateStatus(1, tid, type, uid);
                return "成功！受影响投诉记录" + update + "条";
            } else {
                return "修改" + type + "失败!";
            }
        } else {
            return "参数错误!";
        }
    }

    @Override
    public Map<String, Object> showUserAndRoles(String uid) {
        if (!isNumber(uid)) {
            return null;
        }
        User user = userMapper.selectById(Integer.parseInt(uid));
        if (user == null) {
            return null;
        }
        Set<String> roles = userMapper.selectRolesByUid(user.getUid());
        if (roles.size() <= 0) {
            roles.add("user");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("roles", roles);
        return map;
    }

    @Override
    public String[] uploadImg(MultipartFile[] files, HttpServletRequest request) throws IOException {
        String str = request.getParameter("dir");
        if (str == null || str.equals("")) {
            return null;
        }
        String ctx = request.getContextPath();
        String path = UPLOAD_TEMP_PATH + "/images/news/";
        File dir = new File(path);
        if (!dir.exists()) {
            boolean b = dir.mkdirs();
            if (b) {
                System.out.println(path + "文件夹创建成功!");
            }
        }
        List<String> filenames = new ArrayList<>();
        for (MultipartFile file : files) {
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = UUID.randomUUID() + suffix;
            System.out.println(file.getOriginalFilename() + "--rename--" + fileName);
            file.transferTo(new File(path, fileName));
            filenames.add(ctx + "/images/news/" + fileName);
        }
        return filenames.toArray(new String[0]);
    }

    public void saveFile(List<String> list) throws IOException {
        for (String f : list) {
            String filename = f.substring(f.lastIndexOf('/') + 1);
            String oldParent = UPLOAD_TEMP_PATH + "/images/news/";
            File dir1 = new File(oldParent);
            if (!dir1.exists()) {
                boolean b = dir1.mkdirs();
                System.out.println(b);
            }
            String newParent = UPLOAD_PATH + "/images/news/";
            File dir2 = new File(newParent);
            if (!dir2.exists()) {
                boolean b = dir2.mkdirs();
                System.out.println(b);
            }
            String oldPath = oldParent + filename;
            String newPath = newParent + filename;
            System.out.println(oldPath + "--(cp)-->" + newPath);
            File file = new File(oldPath);
            if (file.exists()) {
                FileUtils.copyFile(file, new File(newPath));
                FileUtils.forceDelete(file);
            }
        }
    }

    @Override
    public boolean addMessage(MsgContent msgContent) {
        msgContent.setCreateDate(new Date());
        int result=msgContentMapper.insertOne(msgContent);
        return result>0;
    }
}
