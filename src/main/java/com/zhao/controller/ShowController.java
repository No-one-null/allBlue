package com.zhao.controller;

import com.zhao.pojo.*;
import com.zhao.service.DataService;
import com.zhao.service.LoginService;
import com.zhao.service.ShowService;
import com.zhao.util.PageInfo;

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
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
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
    @Resource
    private LoginService loginServiceImpl;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {
        System.out.println(request.getServletContext().getRealPath(""));
        List<?> anime = showServiceImpl.sort("anime", 0, 12);
        List<?> comic = showServiceImpl.sort("comic", 0, 12);
        Map<String, Object> map = new HashMap<>();
        map.put("anime", anime);
        map.put("comic", comic);
        model.addAttribute("acMap", map);
        return "/index";
    }

    @RequestMapping("/ac/{category}")
    public String redirect(@PathVariable String category) {
        String str = ERR404;
        if (category.equalsIgnoreCase("anime") || category.equalsIgnoreCase("comic")) {
            str = "redirect:/ac/" + category + "/p1";
        }
        if (category.equalsIgnoreCase("news")) {
            str = "redirect:/news/all";
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
        return "/ac";
    }

    @RequestMapping("/news/{type}")
    public String news(@PathVariable String type, Model model, String pNum) {
        if (!isExist(NEWS_TYPE_ARRAY, type) && !type.equals("all")) {
            return ERR404;
        }
        PageInfo pi = showServiceImpl.showPage("news", type, pNum, "10");
        model.addAttribute("page", pi);
        model.addAttribute("menu", NEWS_TYPE_ARRAY);
        model.addAttribute("type", type);
        return "/news";
    }

    @RequestMapping("/news/article/{nid}")
    public String showNews(Model model, @PathVariable String nid) {
        AcNews news = showServiceImpl.showNews(nid);
        if (news == null) {
            return ERR404;
        }
        model.addAttribute("news", news);
        return "/news_content";
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
                    list = new ArrayList<>();
                }
            }
            model.addAttribute("list", list);
            return "/topic_search";
        }
        List<Talk> talks = showServiceImpl.showAllTalk();
        model.addAttribute("talksList", talks);
        return "/topic";
    }

    /**
     * 上传图片和话题
     *
     * @param files   图片
     * @param request 获取数据
     * @return 返回提示
     */
    @ResponseBody
    @PostMapping("/topic/submit")
    public String upload(MultipartFile[] files, HttpServletRequest request) {
        String message;
        try {
            message = showServiceImpl.addTalk(files, request);
        } catch (IOException e) {
            e.printStackTrace();
            message = "文件操作异常!";
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
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");
        System.out.println(user);
        Talk talk = showServiceImpl.showTalk(tid, user);
        if (talk == null) {
            return ERR404;
        }
        List<Comment> comments = showServiceImpl.showComments(tid);
        model.addAttribute("complaints", COMPLAINT_ARRAY);
        model.addAttribute("comments", comments);
        model.addAttribute("talk", talk);
        return "/topic_comment";
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
    public String findId(@PathVariable String id, Model model,HttpServletRequest request) {
        AcItems ac = dataServiceImpl.findById(id);
        if (ac == null) {
            return ERR404;
        }
        model.addAttribute("ac", ac);
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if(currentUser!=null){
            Mark mark = showServiceImpl.findMarkOne(ac.getId(), currentUser.getUid());
            model.addAttribute("myMark", mark);
        }
        int userNum = showServiceImpl.sumRating(ac.getId() + "");
        model.addAttribute("userNum", userNum);
        List<Mark> marks = showServiceImpl.allComments(ac.getId()+"");
        model.addAttribute("comment", marks);
        return "/acInfo";
    }

    @ResponseBody
    @PostMapping("/myMark")
    public Map<String, Object> myMark(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String acId = request.getParameter("acId");
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        Mark mark = showServiceImpl.findMarkOne(Integer.parseInt(acId), currentUser.getUid());
        System.out.println(mark);
        map.put("myMark", mark);
        return map;
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
        int userNum = showServiceImpl.sumRating(acId);
        map.put("userNum", userNum);
        if (userNum > 0) {
            acRating = showServiceImpl.calRating(acId, ac.getRating());
        }
        map.put("acRating", acRating);
        return map;
    }

    @RequestMapping("/user{uid}")
    public String userInfo(@PathVariable String uid) {
        String path = "redirect:/u" + uid + "/info";
        System.out.print(path);
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
                String str = "redirect:/user" + user.getUid();
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

    @RequestMapping("/account")
    public String setPassword(String password, String confirm, String newPass, String username,
                              Model model, HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("currentUser");
        String error = "";
        if (password != null && newPass != null && confirm != null) {
            if (!"".equals(password) && !"".equals(newPass) && !"".equals(confirm)) {
                String message = loginServiceImpl.editUsername(password, confirm, newPass, user);
                if (message.equals("success")) {
                    model.addAttribute("message", "success");
                } else {
                    error = message;
                }
            } else {
                error = "不能为空!";
            }
        }
        if (password != null && username != null) {
            if (!"".equals(password) && !"".equals(username)) {
                String message;
                try {
                    message = loginServiceImpl.editPassword(password, username, user);
                    if (message.equals("success")) {
                        model.addAttribute("message", "success");
                    } else {
                        error = message;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error = "数据库更新失败!";
                }
            } else {
                error = "不能为空!";
            }
        }
        model.addAttribute("error", error);
        return "/front/account";
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
            return "/results";
        }
        List<?> list;
        try {
            list = showServiceImpl.findByWord("acItems", conditions, str, sort);
        } catch (Exception e) {
            e.printStackTrace();
            list = new ArrayList<>();
        }
        model.addAttribute("list", list);
        return "/results";
    }

    @RequestMapping("/error{num}")
    public String error(@PathVariable String num) {
        String page = "/error/error404";
        if (num != null && !num.equals("")) {
            page = "/error/err" + num;
        }
        System.out.println(page);
        return page;
    }
}
