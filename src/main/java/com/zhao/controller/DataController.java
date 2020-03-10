package com.zhao.controller;

import com.zhao.pojo.AcItems;
import com.zhao.pojo.AcNews;
import com.zhao.util.PageInfo;
import com.zhao.pojo.User;
import com.zhao.service.DataService;
import com.zhao.service.LoginService;

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
import java.io.IOException;
import java.util.*;

import static com.zhao.util.CommonUtil.*;
import static com.zhao.util.Constant.*;

@Controller
@RequestMapping("/back")
public class DataController {
    @Resource
    private LoginService loginServiceImpl;
    @Resource
    private DataService dataServiceImpl;

    /*分页获取信息*/
    @RequestMapping("/{path}List{pSize}/p{pNum}")
    public String show(Model model, @PathVariable String path,
                       @PathVariable String pNum, @PathVariable String pSize) {
        String str = ERR404;
        if (path.equals("user")) {
            str = "/back/user/userInfo";
        }
        if (path.equals("ac")) {
            str = "back/acInfo/ac_Info";
        }
        if (path.equals("news")) {
            str = "/back/acNews/acNews";
        }
        if (path.equals("topic")) {
            str = "/back/topic/topic_list";
        }
        if (path.equals("complaint")) {
            str = "/back/topic/complaint_list";
        }
        if(path.equals("topic")){
            str="/back/topic/topic_list";
        }
        PageInfo pageInfo = dataServiceImpl.showPage(path, pNum, pSize);
        model.addAttribute("pages", pageInfo);
        return str;
    }

    @RequestMapping("/{path}/info")
    public String showAll(@PathVariable String path) {
        String str = "redirect:/back/" + path + "List/p1";
        System.out.println(str);
        return str;
    }

    @RequestMapping("/acNews")
    public String acNews() {
        return "redirect:/back/newsList/p1";
    }

    /*关键词搜索*/
    @RequestMapping("/{path}/search={word}")
    public String find(Model model, @PathVariable String word, @PathVariable String path) {
        List<?> list = new ArrayList<>();
        String p = "/error/error404";
        if (path.equalsIgnoreCase("user")) {
            list = loginServiceImpl.findByWord(word);
            p = "/back/user/userSearch";
        }
        if (path.equalsIgnoreCase("ac")) {
            p = "/back/acInfo/ac_search";
            list = dataServiceImpl.findByWord(path, word);
        }
        if (path.equalsIgnoreCase("news")) {
            p = "/back/acNews/newsList";
            list = dataServiceImpl.findByWord(path, word);
        }
        model.addAttribute("list", list);
        return p;
    }

    /**
     * 用户详情页
     */
    @RequestMapping("/user/uid={uid}")
    public String userDetail(@PathVariable String uid, Model model) {
        User user = loginServiceImpl.showOne(uid);
        System.out.println(user);
        model.addAttribute("user", user);
        return "/back/user/userDetail";
    }

    /**
     * 跳转新增页
     */
    @RequestMapping("/add{path}")
    public String add(@PathVariable String path, Model model, AcItems acItems) {
        System.out.println(acItems);//不使用会报警告
        String str = ERR404;
        if (path.equalsIgnoreCase("Items")) {
            model.addAttribute("countries",COMMON_COUNTRY);
            str = "back/acInfo/ac_add";
        }
        if (path.equalsIgnoreCase("News")) {
            model.addAttribute("type", TYPE_ARRAY);
            str = "back/acNews/addNews";
        }
        return str;
    }

    @RequestMapping("/addOne")
    public String addOk(MultipartFile file, @Valid AcItems acItems, BindingResult result, Model model) {
        String message = "";
        if (!result.hasErrors()) {
            try {
                message = dataServiceImpl.addAcItems(file, acItems);
            } catch (IOException e) {
                e.printStackTrace();
                message = "图片上传失败!";
            }
        }
        model.addAttribute("message", message);
        return "/back/acInfo/ac_add";
    }

    @RequestMapping("/edit/{id}")
    public String editItem(@PathVariable String id, Model model) {
        AcItems ac = dataServiceImpl.findById(Integer.valueOf(id));
        model.addAttribute("ac", ac);
        model.addAttribute("countries",COMMON_COUNTRY);
        return "/back/acInfo/ac_edit";
    }

    @ResponseBody
    @RequestMapping("/editOk")
    public Map<String,Object> editOk(@Valid AcItems acItems, MultipartFile file, BindingResult result) {
        System.out.println(acItems);
        String message;
        Map<String,Object> map=new HashMap<>();
        if (!result.hasErrors()) {
            try {
                message=dataServiceImpl.updateOne(acItems,file);
            } catch (IOException e) {
                message="上传图片时出错!";
                e.printStackTrace();
            }
        }else {
            message="传入数据错误!";
        }
        map.put("message", message);
        return map;
    }

    @RequestMapping("/deleteOne/{id}")
    public String deleteOne(@PathVariable String id) {
        try {
            boolean b=dataServiceImpl.DeleteOne(Integer.parseInt(id));
            System.out.println(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/back/ac/info";
    }

    @ResponseBody
    @RequestMapping("/submitNews")
    public String submitNews(HttpServletRequest request) {
        AcNews acNews = new AcNews();
        acNews.setNewsAuthor(request.getParameter("author"));
        acNews.setNewsContent(request.getParameter("html"));
        acNews.setNewsTitle(request.getParameter("title"));
        acNews.setNewsType(request.getParameter("type"));
        System.out.println(acNews);
        Boolean isWork = dataServiceImpl.addNews(acNews);
        if (isWork) {
            return "success";
        }
        return "fail";
    }

    @RequestMapping("/showNews{id}")
    public String showNews(Model model, @PathVariable String id) {
        AcNews acNews = dataServiceImpl.findNewsById(id);
        model.addAttribute("type", TYPE_ARRAY);
        model.addAttribute("news", acNews);
        return "/back/acNews/detailsNews";
    }

    @ResponseBody
    @RequestMapping("/editStatus")
    public String editStatus(String id, String status) {
        System.out.println(id + status);
        if (dataServiceImpl.updNews(id, status)) {
            return "success";
        }
        return "fail";
    }

    @ResponseBody
    @RequestMapping("/editNews")
    public String nextNews(String id,String content) {
        return dataServiceImpl.editNews(id,content);
    }

    @ResponseBody
    @RequestMapping("/showTalk")
    public Map<String, Object> show(String type, String tid) {
        Map<String, Object> map = new HashMap<>();
        if (!isNumber(tid)) {
            map.put("message", "数据传输错误!");
            return map;
        }
        map = dataServiceImpl.findTalk(type, Integer.parseInt(tid));
        return map;
    }

    @ResponseBody
    @PostMapping("/topicCheck")
    public Map<String, Object> complaint(String type, String tid, String deal, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("currentUser");
        Map<String, Object> map = new HashMap<>();
        if (!isNumber(tid)) {
            map.put("message", "数据传输错误!");
            return map;
        }
        System.out.println(type + tid + deal);
        String message = dataServiceImpl.checkTopic(type, Integer.parseInt(tid), deal, user.getUid());
        map.put("message", message);
        return map;
    }

}
