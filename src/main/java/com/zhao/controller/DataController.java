package com.zhao.controller;

import com.zhao.pojo.AcItems;
import com.zhao.pojo.PageInfo;
import com.zhao.pojo.User;
import com.zhao.service.DataService;
import com.zhao.service.LoginService;

import com.zhao.service.ShowService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/back")
public class DataController {
    @Resource
    private LoginService loginServiceImpl;
    @Resource
    private DataService dataServiceImpl;
    @Resource
    private ShowService showServiceImpl;

    /**
     * 全部信息展示
     *
     * @param model
     * @param pageNumber
     * @param request
     * @param path
     * @return
     */
    @RequestMapping("/{path}/info/{pageNumber}")
    public String showAllUser(Model model, @PathVariable String pageNumber, HttpServletRequest request, @PathVariable String path) {
        PageInfo pageInfo = showServiceImpl.showPage(path, pageNumber, request.getParameter("pageSize"));
        model.addAttribute("list", pageInfo.getList());
        model.addAttribute("pages", pageInfo);
        if (path.equalsIgnoreCase("user")) {
            return "/back/user/userInfo";
        }
        if (path.equalsIgnoreCase("ac")) {
            return "back/acInfo/ac_Info";
        }
        return "/error/error404";
    }

    /*
    跳转到acNews编辑页
     */
    @RequestMapping("/acNews")
    public String acNews() {
        return "/back/acNews/acNews";
    }

    /**
     * 关键词搜索
     *
     * @param model
     * @param request
     * @param word
     * @param path
     * @return
     */
    @RequestMapping("/{path}/search={word}")
    public String find(Model model, HttpServletRequest request, @PathVariable String word, @PathVariable String path) {
        List<?> list = null;
        if (path.equalsIgnoreCase("user")) {
            list = loginServiceImpl.findByWord(word);
        }
        if (path.equalsIgnoreCase("ac")) {
            list = dataServiceImpl.findByWord(word);
        }
        int listSize = list.size();
        model.addAttribute("listSize", listSize);
        model.addAttribute("list", list);
        if (path.equalsIgnoreCase("user")) {
            return "/back/user/userSearch";
        }
        if (path.equalsIgnoreCase("ac")) {
            return "/back/acInfo/ac_search";
        }
        return "/error/error404";
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

    @RequestMapping("/add")
    public String add(HttpServletRequest request) {
        return "back/acInfo/ac_add";
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
        return "redirect:/back/ac/info/1";
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