package com.zhao.test;

import com.zhao.mapper.TalkMapper;
import com.zhao.pojo.AcNews;
import com.zhao.pojo.Talk;
import com.zhao.service.DataService;
import com.zhao.service.impl.DataServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private DataService dataServiceImpl;
    @Resource
    private TalkMapper talkMapper;

    @RequestMapping("")
    public String test(){
        return "测试";
    }

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

    @RequestMapping("/talk/{status}")
    public Map<String,Object> testTalk(@PathVariable String status){
        if(status==null){
            status="-1";
        }
        Map<String,Object> map=new HashMap<>();
        List<Talk> talk=talkMapper.selectAll(Integer.parseInt(status),"","");
        map.put("talk",talk);
        return map;
    }

    @RequestMapping("/userSearch")
    public String userSearch() {
        return "/back/user/userSearch";
    }
}
