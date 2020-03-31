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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
            str = "/back/news/acNews";
        }
        if (path.equals("topic")) {
            str = "/back/topic/topic_list";
        }
        if (path.equals("complaint")) {
            str = "/back/topic/complaint_list";
        }
        if (path.equals("topic")) {
            str = "/back/topic/topic_list";
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
        String str = "redirect:/back/newsList/p1";
        System.out.print("");
        return str;
    }

    /*关键词搜索*/
    @RequestMapping("/{path}/search={word}")
    public String find(Model model, @PathVariable String word, @PathVariable String path) {
        List<?> list = new ArrayList<>();
        String p = ERR404;
        if (path.equalsIgnoreCase("user")) {
            list = loginServiceImpl.findByWord(word);
            p = "/back/user/userSearch";
        }
        if (path.equalsIgnoreCase("ac")) {
            p = "/back/acInfo/ac_search";
            list = dataServiceImpl.findByWord(path, word);
        }
        if (path.equalsIgnoreCase("news")) {
            p = "/back/news/newsList";
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
        Map<String, Object> map = dataServiceImpl.showUserAndRoles(uid);
        if (map == null) {
            return ERR404;
        }
        System.out.println(map);
        model.addAttribute("user", map.get("user"));
        model.addAttribute("roles", map.get("roles"));
        return "/back/user/userDetail";
    }

    /**
     * 跳转新增页
     */
    @RequestMapping("/add{path}")
    public String add(@PathVariable String path, Model model, AcItems acItems) {
        String str = ERR404;
        if (path.equalsIgnoreCase("Items")) {
            System.out.println(acItems);//不使用会报警告
            model.addAttribute("countries", COMMON_COUNTRY);
            str = "back/acInfo/ac_add";
        }
        if (path.equalsIgnoreCase("News")) {
            model.addAttribute("type", NEWS_TYPE_ARRAY);
            str = "back/news/addNews";
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
        AcItems ac = dataServiceImpl.findById(id);
        model.addAttribute("ac", ac);
        model.addAttribute("countries", COMMON_COUNTRY);
        return "/back/acInfo/ac_edit";
    }

    @ResponseBody
    @RequestMapping("/editOk")
    public Map<String, Object> editOk(@Valid AcItems acItems, MultipartFile file, BindingResult result) {
        System.out.println(acItems);
        String message;
        Map<String, Object> map = new HashMap<>();
        if (!result.hasErrors()) {
            try {
                message = dataServiceImpl.updateOne(acItems, file);
            } catch (IOException e) {
                message = "上传图片时出错!";
                e.printStackTrace();
            }
        } else {
            message = "传入数据错误!";
        }
        map.put("message", message);
        return map;
    }

    @RequestMapping("/deleteOne/{id}")
    public String deleteOne(@PathVariable String id) {
        String str = "redirect:/back/ac/info";
        try {
            boolean b = dataServiceImpl.DeleteOne(Integer.parseInt(id));
            System.out.println(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    @ResponseBody
    @RequestMapping("/submitNews")
    public String submitNews(AcNews news, String[] paths) {
        System.out.println(news);
        String message;
        try {
            message = dataServiceImpl.addNews(news, paths);
        } catch (IOException e) {
            e.printStackTrace();
            message = "文件操作异常!";
        }
        return message;
    }

    @RequestMapping("/showNews{id}")
    public String showNews(Model model, @PathVariable String id) {
        AcNews acNews = dataServiceImpl.findNewsById(id);
        model.addAttribute("type", NEWS_TYPE_ARRAY);
        model.addAttribute("news", acNews);
        return "/back/news/detailsNews";
    }

    @ResponseBody
    @RequestMapping("/editNews")
    public String nextNews(String id, String content, String type, String status, String[] paths) {
        System.out.println(Arrays.toString(paths));
        String message;
        try {
            message = dataServiceImpl.editNews(id, content, type, status, paths);
        } catch (IOException e) {
            e.printStackTrace();
            message = "文件操作异常!";
        }
        return message;
    }

    @GetMapping("news/notices")
    public String notice() {
        return "back/news/notice-add";
    }

    @PostMapping("news/notices")
    public String notice(Model model, String message, String title, String type) {
        if (message == null || message.equals("") || title == null || title.equals("") || type == null || type.equals("")) {
            return "redirect:notices";
        }
        boolean result = dataServiceImpl.addMessage(title, message, type);
        if (result) {
            model.addAttribute("message", "success");
        }
        return "back/news/notice-add";
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
    public Map<String, Object> complaint(String type, String tid, String deal, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
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

    @ResponseBody
    @RequestMapping("/uploadImg")
    public Map<String, Object> upload(MultipartFile[] files, HttpServletRequest request) {
        System.out.println(request.getParameter("dir"));
        Map<String, Object> map = new HashMap<>();
        int errno = -1;
        System.out.println("执行上传图片");
        String[] data = new String[0];
        try {
            data = dataServiceImpl.uploadImg(files, request);
            if (data != null) {
                errno = 0;
            }
            System.out.println(Arrays.toString(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("data", data);
        map.put("errno", errno);
        return map;
    }

}
