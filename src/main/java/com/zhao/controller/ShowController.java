package com.zhao.controller;

import com.zhao.pojo.*;
import com.zhao.service.DataService;
import com.zhao.service.ShowService;
import com.zhao.util.PageInfo;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static com.zhao.util.CommonUtil.*;
import static com.zhao.util.Constant.*;

@Controller
public class ShowController {
    @Resource
    private DataService dataServiceImpl;
    @Resource
    private ShowService showServiceImpl;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {
        System.out.println(request.getServletContext().getRealPath(""));
        List<AcItems> anime = showServiceImpl.sort("anime", 0, 12);
        List<AcItems> comic = showServiceImpl.sort("comic", 0, 12);
        Map<String, Object> map = new HashMap<>();
        map.put("anime", anime);
        map.put("comic", comic);
        model.addAttribute("acMap", map);
        return "/index";
    }

    @RequestMapping("/ac/{category}")
    public String redirect(@PathVariable String category) {
        String str=ERR404;
        if (category.equalsIgnoreCase("anime") || category.equalsIgnoreCase("comic")) {
            str = "redirect:/ac/" + category + "/p1";
        }
        if (category.equalsIgnoreCase("news")) {
            str= "redirect:/news/all";
        }
        if (category.equalsIgnoreCase("topic")) {
            str = "redirect:/topic";
        }
        return str;
    }

    @RequestMapping("/ac/{category}/p{page}")
    public String category(@PathVariable String category, @PathVariable String page,
                           Model model) {
        PageInfo pageInfo = showServiceImpl.showAcItems(category, "name", "ASC", page);
        if (pageInfo == null) {
            return ERR404;
        }
        model.addAttribute("page", pageInfo);
        return "/front/ac";
    }

    @RequestMapping("/news/{type}")
    public String news(@PathVariable String type, Model model, String pNum) {
        if (!isExist(TYPE_ARRAY, type) && !type.equals("all")) {
            return ERR404;
        }
        PageInfo pi = showServiceImpl.showPage("news", type, pNum, "10");
        model.addAttribute("page", pi);
        model.addAttribute("menu", TYPE_ARRAY);
        model.addAttribute("type", type);
        return "/front/news";
    }

    @RequestMapping("/news/article/{nid}")
    public String showNews(Model model, @PathVariable String nid) {
        AcNews news = showServiceImpl.showNews(nid);
        if (news == null) {
            return ERR404;
        }
        model.addAttribute("news", news);
        return "/front/news_content";
    }

    @RequestMapping("/topic")
    public String showTopic(Model model, String search, String conditions, String sort) {
        if (search != null) {
            model.addAttribute("keyword", search);
            model.addAttribute("sort", sort);
            model.addAttribute("conditions", conditions);
            model.addAttribute("more", OPEN_CLOSE);
            List<?> list;
            if ("".equals(search)) {
                list = null;
            } else {
                String pattern = "^#[\\s\\S]*#$";
                boolean isMatch = Pattern.matches(pattern, search);
                if (isMatch) {
                    conditions = "topic";
                    search = search.substring(1, search.length() - 1);
                    model.addAttribute("keyword", search);
                    model.addAttribute("conditions", conditions);
                }
                try {
                    list = showServiceImpl.findByWord("talk", conditions, search.trim(), sort);
                } catch (Exception e) {
                    e.printStackTrace();
                    list=new ArrayList<>();
                }
            }
            model.addAttribute("list", list);
            return "/front/topic_search";
        }
        List<Talk> talks = showServiceImpl.showAllTalk();
        model.addAttribute("talksList", talks);
        return "/front/topic";
    }

    /**
     * 上传图片和话题
     *
     * @param files   图片
     * @param request 获取数据
     * @return 返回提示
     * @throws IOException 文件操作异常
     */
    @ResponseBody
    @PostMapping("/topic/submit")
    public String upload(MultipartFile[] files, HttpServletRequest request) throws IOException {
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
        int flag = 0;
        try {
            flag = showServiceImpl.addTalk(talk);
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

    /**
     * 显示话题和评论
     *
     * @param model Model接口
     * @param tid   话题id
     * @return 路径
     */
    @RequestMapping("/topic/t{tid}")
    public String talk(Model model, @PathVariable String tid, HttpServletRequest request) {
        Talk talk = showServiceImpl.showTalk(tid, request);
        if (talk == null) {
            return ERR404;
        }
        List<Comment> comments = showServiceImpl.showComments(tid);
        model.addAttribute("complaints", COMPLAINT_ARRAY);
        model.addAttribute("comments", comments);
        model.addAttribute("talk", talk);
        return "/front/topic_comment";
    }

    @ResponseBody
    @PostMapping("/submit/complaint")
    public String submit(Complaint complaint, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("currentUser");
        complaint.setFromUid(user.getUid());
        System.out.println(complaint);
        String message = showServiceImpl.sendComplaint(complaint);
        if (message == null) {
            return "失败";
        }
        return message;
    }

    @ResponseBody
    @PostMapping("/submit/comment")
    public String submitComment(Comment comment, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("currentUser");
        if (user == null) {
            return "请登录!";
        }
        String content = comment.getContent();
        if (content.trim().equals("")) {
            return "内容不能为空!";
        } else {
            content = replaceBlank(content);
            if (content.length() > CONTENT_MAXSIZE) {
                content = content.substring(0, COMMENT_MAXSIZE);
            }
            comment.setContent(content);
        }
        comment.setUid(user.getUid());
        System.out.println(comment);
        boolean flag = showServiceImpl.addComment(comment);
        if (!flag) {
            return "发送失败！";
        }
        return "success";
    }

    @ResponseBody
    @PostMapping("/remove/{s}")
    public String delete(HttpServletRequest request, String id, String uid, @PathVariable String s) {
        User user = (User) request.getSession().getAttribute("currentUser");
        if (user == null) {
            return "请登录!";
        }
        if (id == null || uid == null) {
            return "数据传输错误!";
        }
        if (!uid.equals("" + user.getUid())) {
            return "你没有权限删除该评论!";
        }
        if (s.equals("talk")) {
            String message;
            try {
                message = showServiceImpl.removeTalk(id);
            } catch (IOException e) {
                e.printStackTrace();
                return "删除文件失败!";
            }
            return message;
        }
        if (s.equals("comment")) {
            boolean flag = showServiceImpl.delComment(id);
            if (!flag) {
                return "删除失败!评论不存在或已经被删除!";
            }
            return "success";
        }
        return "404";
    }

    @RequestMapping("/acInfo/{id}")
    public String findId(@PathVariable String id, Model model) {
        AcItems ac = dataServiceImpl.findById(id);
        if (ac == null) {
            return ERR404;
        }
//        System.out.println(ac);
        model.addAttribute("ac", ac);
        int userNum = showServiceImpl.sumRating(ac.getId() + "");
        model.addAttribute("userNum", userNum);
        return "/front/acInfo";
    }

    @RequestMapping("/u{uid}")
    public String userInfo(@PathVariable String uid) {
        String path = "redirect:/u" + uid + "/info";
        System.out.println(path);
        return path;
    }

    @RequestMapping("/u{uid}/{page}")
    public String userInfo(@PathVariable String uid, @PathVariable String page, Model model, User user) {
        System.out.println("排除异常" + user);
        Map<String, Object> map = showServiceImpl.showUserInfo(uid, page);
        if (map == null) {
            return ERR404;
        }
        model.addAttribute("map", map);
        return "/front/user";
    }

    @RequestMapping("/error{num}")
    public String error(@PathVariable String num) {
        String page = "/error/error404";
        if (num != null && !num.equals("")) {
            page = "/error/error" + num;
        }
        System.out.println(page);
        return page;
    }

    @RequestMapping("/mark")
    @ResponseBody
    public String addMark(Mark mark, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            return "请先登录!";
        }
        mark.setUid(currentUser.getUid());
        String acId = request.getParameter("acId");
        mark.setAcId(Integer.parseInt(acId));
        System.out.println(mark);
        Boolean flag = showServiceImpl.addMark(mark);
        if (flag) {
            return "操作成功!";
        } else {
            return "操作失败!";
        }
    }

    @ResponseBody
    @RequestMapping("/rating")
    public Map<String, Object> rating(String acId) {
        Map<String, Object> map = new HashMap<>();
        System.out.println("acId" + acId);
        AcItems ac = showServiceImpl.findById(acId);
        float acRating = 0;
        try {
            acRating = showServiceImpl.calRating(acId, ac.getRating());
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("acRating", acRating);
        int userNum = showServiceImpl.sumRating(acId);
        map.put("userNum", userNum);
        return map;
    }

    @ResponseBody
    @RequestMapping("/myMark")
    public Map<String, Object> myMark(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String acId = request.getParameter("acId");
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        Mark mark = showServiceImpl.findMarkOne(Integer.parseInt(acId), currentUser.getUid());
        System.out.println(mark);
        map.put("myMark", mark);
        return map;
    }

    @RequestMapping("/showComments")
    @ResponseBody
    public Map<String, Object> showComments(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String acId = request.getParameter("acId");
        List<Mark> marks = showServiceImpl.allComments(acId);
        map.put("comment", marks);
        return map;
    }

    @RequestMapping("/submit/user")
    public String submitUser(@Valid User user, BindingResult result, MultipartFile file,
                             Model model, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (user.getUid() != currentUser.getUid()) {
            return ERR403;
        }
        System.out.println(user + file.getOriginalFilename());
        if (!result.hasErrors()) {
            String message;
            try {
                message = showServiceImpl.updateUser(user, file);
            } catch (IOException e) {
                e.printStackTrace();
                message = "文件操作异常!";
            }
            if (message.equalsIgnoreCase("success")) {
                User current = showServiceImpl.showUser(user.getUid());
                request.getSession().setAttribute("currentUser", current);
                String str="redirect:/u" + user.getUid();
                System.out.println(str);
                return str;
            } else {
                model.addAttribute("message", message);
            }
        }
        Map<String, Object> map = showServiceImpl.showUserInfo(user.getUid() + "", "info");
        map.put("user", user);
        model.addAttribute("map", map);
        return "/front/user";
    }

    @RequestMapping("/search")
    public String search(String s, String conditions, String sort, Model model) {
        model.addAttribute("keyword", s);
        model.addAttribute("sort", sort);
        model.addAttribute("conditions", conditions);
        model.addAttribute("more", OPEN_CLOSE);
        String str;
        if (s != null) {
            str = s.trim();
        } else {
            return "/front/results";
        }
        List<?> list;
        try {
            list = showServiceImpl.findByWord("acItems", conditions, str, sort);
        } catch (Exception e) {
            e.printStackTrace();
            list=new ArrayList<>();
        }
        model.addAttribute("list", list);
        return "/front/results";
    }
}
