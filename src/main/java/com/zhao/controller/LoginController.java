package com.zhao.controller;


import com.zhao.pojo.User;
import com.zhao.service.LoginService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;

import com.google.code.kaptcha.impl.DefaultKaptcha;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class LoginController {
    @Resource
    private DefaultKaptcha defaultKaptcha;
    @Resource
    private LoginService loginServiceImpl;

    @RequestMapping("/verifyCode")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
            String createText = defaultKaptcha.createText();
            httpServletRequest.getSession().setAttribute("verifyCode", createText);
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
            BufferedImage challenge = defaultKaptcha.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream =
                httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }

    @RequestMapping("/register")
    public String register(User user) {
        return "/register";
    }

    @RequestMapping("/save")
    public String saveUser(@Valid User user, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "/register";
        }
        Boolean isExist = loginServiceImpl.isExistName(user.getUsername());
        if (isExist) {
            request.setAttribute("errInfo", "用户名已存在");
            return "/register";
        }
        String sessionCode = (String) request.getSession().getAttribute("verifyCode");
        String userCode = request.getParameter("verifyCode");
        if (!sessionCode.equals(userCode)) {
            System.out.println("失败");
            request.setAttribute("errInfo", "验证码错误");
            return "/register";
        }
        Md5Hash md5Hash = new Md5Hash(user.getPassword(), user.getUsername());
        user.setPassword(md5Hash.toString());
        loginServiceImpl.addUser(user);
        return "redirect:/login";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        System.out.println(currentUser);
//        System.out.println("currentUser="+currentUser);
        if (currentUser != null) {
            return "redirect:/LoginSuccess";
        } else {
            return "/login";
        }
    }

    @PostMapping("/doLogin")
    public String login(User user, HttpSession session, HttpServletRequest request) {
        System.out.println("传入:" + user.getUsername() + user.getPassword() + user.isRemember());
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        System.out.println(token);
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            token.setRememberMe(user.isRemember());
            subject.login(token);
            session.setAttribute("user", subject.getPrincipal());
            User users = loginServiceImpl.findUserByName(user.getUsername());
            session.setAttribute("currentUser", users);
            System.out.println(user.getUsername() + "在" + sdf.format(date) + "登录成功!");
            return "redirect:/LoginSuccess";
        } catch (AuthenticationException e) {
            e.printStackTrace();
            System.out.println(user.getUsername() + "在" + sdf.format(date) + "登录失败!");
            request.setAttribute("errLogin", "登陆失败!请检查用户名或密码");
            return "/login";
        }
//        System.out.println("isAuthenticationToken:"+subject.isAuthenticated());
    }

    @RequestMapping("/LoginSuccess")
    public String ok() {
        return "/ok";
    }

    @RequestMapping("/exit")
    public String exit(HttpServletRequest request, HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "/login";
    }
}
