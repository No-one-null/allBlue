package com.zhao.controller;

import com.zhao.pojo.AcItems;
import com.zhao.pojo.Mark;
import com.zhao.pojo.User;
import com.zhao.service.DataService;
import com.zhao.service.ShowService;
import com.zhao.service.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ShowController {
    @Resource
    private LoginService loginServiceImpl;
    @Resource
    private DataService dataServiceImpl;
    @Resource
    private ShowService showServiceImpl;

//    @RequestMapping("/")
//    public String index(Model model, HttpServletRequest request) {
//        List animes = showServiceImpl.findByParam("category", "A", "DESC", 0, 16);
//        model.addAttribute("animes", animes);
//        List comics = showServiceImpl.findByParam("category", "C", "DESC", 0, 16);
//        model.addAttribute("comics", comics);
//        return "/index";
//    }

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {
        List animes = showServiceImpl.findByParam("category", "A", "DESC", 0, 16);
        model.addAttribute("animes", animes);
        List comics = showServiceImpl.findByParam("category", "C", "DESC", 0, 16);
        model.addAttribute("comics", comics);
        return "/index";
    }

    @RequestMapping("/acInfo={id}")
    public String findId(@PathVariable String id, Model model, HttpServletRequest request) {
        AcItems ac = dataServiceImpl.findById(Integer.valueOf(id));
//        System.out.println(ac);
        model.addAttribute("ac", ac);
        int userNum = showServiceImpl.sumRating(ac.getId() + "");
        model.addAttribute("userNum", userNum);
        return "/front/acInfo";
    }

    @RequestMapping("/ac/{category}")
    public String category(@PathVariable String category, Model model) {
        String path = "";
        if (category.equalsIgnoreCase("comic")) {
            path = "C";
        }
        if (category.equalsIgnoreCase("anime")) {
            path = "A";
        }
        List list = showServiceImpl.findByParam("category", path, null, 0, 0);
        model.addAttribute("list", list);
        path = "/front/" + category;
        return path;
    }

    @RequestMapping("/userInfo")
    public String userInfo() {
        return "/front/user";
    }

    @RequestMapping("/error{num}")
    public String error(@PathVariable String num) {
        String page = "/error/error" + num;
        return page;
    }

    @RequestMapping("/mark")
    @ResponseBody
    public String addMark(Mark mark, HttpServletRequest request, HttpServletResponse response) {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            return "请先登录!";
        }
        mark.setUid(currentUser.getUid());
        String acId = request.getParameter("acId");
        mark.setAcId(Integer.valueOf(acId));
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
    public Map<String, Object> rating(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String acId = request.getParameter("acId");
        System.out.println(acId);
        AcItems ac = showServiceImpl.findById(acId);
        float acRating = showServiceImpl.calRating(acId, ac.getRating());
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
        Mark mark = showServiceImpl.findMarkOne(Integer.valueOf(acId), currentUser.getUid());
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
//        System.out.println("acId:" + acId + "marks:" + marks);
        return map;
    }
}
