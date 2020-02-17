package com.zhao.controller;

import com.zhao.pojo.AcItems;
import com.zhao.pojo.AcNews;
import com.zhao.util.PageInfo;
import com.zhao.pojo.User;
import com.zhao.service.DataService;
import com.zhao.service.LoginService;

import com.zhao.util.CommonUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.zhao.util.Constant.ERROR404;
import static com.zhao.util.Constant.TYPE_ARRAY;

@Controller
@RequestMapping("/back")
public class DataController {
    @Resource
    private LoginService loginServiceImpl;
    @Resource
    private DataService dataServiceImpl;

    CommonUtil commonUtil = new CommonUtil();

    /**
     * 分页获取信息
     *
     * @param model
     * @param path
     * @return
     */
    @RequestMapping("/{path}List{pSize}/p{pNum}")
    public String show(Model model, @PathVariable String path, @PathVariable String pNum, @PathVariable String pSize) {
        String str = ERROR404;
        System.out.println(pNum + pSize);
        if (path.equals("user")) {
            str = "/back/user/userInfo";
        }
        if (path.equals("ac")) {
            str = "back/acInfo/ac_Info";
        }
        if (path.equals("news")) {
            str = "/back/acNews/acNews";
        }
        PageInfo pageInfo = dataServiceImpl.showPage(path, pNum, pSize);
        model.addAttribute("pages", pageInfo);
//        System.out.println(pageInfo);
        return str;
    }

    @RequestMapping("/{path}/info")
    public String showAll(Model model, @PathVariable String path) {
        String str = "redirect:/back/" + path + "List/p1";
        return str;
    }

    @RequestMapping("/acNews")
    public String acNews() {
        return "redirect:/back/newsList/p1";
    }

    /**
     * 关键词搜索
     *
     * @param model
     * @param word
     * @param path
     * @return
     */
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
     *
     * @param uid
     * @param model
     * @return
     */
    @RequestMapping("/user/uid={uid}")
    public String userDetail(@PathVariable String uid, Model model) {
        User user = loginServiceImpl.showOne(uid);
        System.out.println(user);
        model.addAttribute("user", user);
        return "/back/user/userDetail";
    }

    /**
     * 增加条目
     *
     * @param request
     * @param path
     * @param model
     * @return
     */
    @RequestMapping("/add{path}")
    public String add(HttpServletRequest request, @PathVariable String path, Model model) {
        String str = "/error/error404";
        if (path.equalsIgnoreCase("Items")) {
            str = "back/acInfo/ac_add";
        }
        if (path.equalsIgnoreCase("News")) {
            model.addAttribute("type", TYPE_ARRAY);
            str = "back/acNews/addNews";
        }
        return str;
    }

    @RequestMapping("/addOne")
    public String addOk(HttpServletRequest request, MultipartFile file, @Valid AcItems acItems,
                        BindingResult result, Model model) throws IOException, ServletException {
        if (!result.hasErrors()) {
            if (file.getSize() > 0) {
                String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String fileName = UUID.randomUUID().toString() + suffix;
                upload(file, fileName, request);
                acItems.setImage(fileName);
            } else {
                acItems.setImage("");
            }
            System.out.println(acItems);
            int addResult = dataServiceImpl.addAcItems(acItems);
            if (addResult > 0) {
                return "redirect:/back/addSuccess";
            } else {
                return "/back/acInfo/ac_add";
            }
        } else {
            return "/back/acInfo/ac_add";
        }
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
    @RequestMapping("/nextNews")
    public int nextNews(String id) {
        int next = dataServiceImpl.findNextId(id);
        return next;
    }

    @RequestMapping("/addSuccess")
    public String addSuccess(HttpServletRequest request, Model model) {
        AcItems acItems = dataServiceImpl.findLastOne();
        model.addAttribute("ac", acItems);
        return "/back/acInfo/ac_add_ok";
    }

    @RequestMapping("/addSuccess/{name}")
    public String addOK(HttpServletRequest request, Model model, @PathVariable String name) {
        AcItems acItems = dataServiceImpl.findLastOne(name);
        model.addAttribute("ac", acItems);
        return "/back/acInfo/ac_add_ok";
    }

    @RequestMapping("/edit/{id}")
    public String editItem(@PathVariable String id, Model model) {
        AcItems ac = dataServiceImpl.findById(Integer.valueOf(id));
        model.addAttribute("ac", ac);
        return "/back/acInfo/ac_edit";
    }

    @RequestMapping("/editOk")
    public String editOk(@Valid AcItems acItems, BindingResult result, MultipartFile file,
                         HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!result.hasErrors()) {
            String fileName = "";
            if (!acItems.getImage().equals("")) {
                fileName = acItems.getImage();
            }
            if (file.getSize() > 0) {
                String prefix = UUID.randomUUID().toString();
                String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                fileName = prefix + suffix;
                upload(file, fileName, request);
                acItems.setImage(fileName);
            } else {
                System.out.println("不修改图片");
            }
            dataServiceImpl.updateOne(acItems);
        } else {
//            return "/back/acInfo/ac_edit";
        }
        return "redirect:/back/edit/" + acItems.getId();
    }

    @RequestMapping("/deleteOne/{id}")
    public String deleteOne(@PathVariable String id, HttpServletResponse response) {
        dataServiceImpl.DeleteOne(Integer.valueOf(id));
        return "redirect:/back/ac/info";
    }

    public void upload(MultipartFile file, String fileName, HttpServletRequest request) {
        String proj = System.getProperty("user.dir");
        String path = request.getServletContext().getRealPath("images") + "/cover/";
        String fullPath = path + fileName;
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(fullPath));
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(proj + "/src/main/resources/static/images/cover/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
