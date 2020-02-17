package com.zhao.test;

import com.zhao.pojo.AcNews;
import com.zhao.service.DataService;
import com.zhao.service.impl.DataServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class TestController {
    @Resource
    private DataService dataServiceImpl;

    @ResponseBody
    @RequestMapping("/testNews")
    public AcNews showNews(HttpServletRequest request) {
        AcNews news = dataServiceImpl.findNewsById(request.getParameter("id"));
        System.out.println(news);
        return news;
    }

    @RequestMapping("/back/test")
    public String showNews(HttpServletRequest request, Model model) {
        String id = request.getParameter("id");
        if (id == null) {
            id = "1";
        }
        AcNews news = dataServiceImpl.findNewsById(id);
        model.addAttribute("news", news);
        System.out.println(news);
        return "/test/test";
    }
}
