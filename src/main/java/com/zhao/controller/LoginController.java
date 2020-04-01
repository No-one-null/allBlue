package com.zhao.controller;

import com.zhao.pojo.Comment;
import com.zhao.pojo.MsgUser;
import com.zhao.pojo.User;
import com.zhao.service.LoginService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.google.code.kaptcha.impl.DefaultKaptcha;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
import java.util.*;

import static com.zhao.util.Constant.ERR403;

@Controller
public class LoginController {
    @Resource
    private DefaultKaptcha defaultKaptcha;
    @Resource
    private LoginService loginServiceImpl;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @RequestMapping("/verifyCode")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        byte[] captchaChallengeAsJpeg;
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
        System.out.print(user.equals(new Object()) ? "" : "");
        return "/register";
    }

    @RequestMapping("/save")
    public String saveUser(@Valid User user, BindingResult result,
                           Model model, HttpServletRequest request) {
        String sessionCode = (String) request.getSession().getAttribute("verifyCode");
        String userCode = request.getParameter("verifyCode");
        Set<String> errors = new HashSet<>();
        if (!sessionCode.equals(userCode)) {
            errors.add("验证码错误");
        }
        if (!result.hasErrors()) {
            String message = loginServiceImpl.addUser(user);
            if (message.equals("success")) {
                return "redirect:login";
            } else {
                errors.add(message);
            }
        } else {
            errors.add("参数错误");
        }
        model.addAttribute("errors", errors);
        model.addAttribute("user", user);
        return "/register";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser != null) {
            return "redirect:LoginSuccess";
        } else {
            return "/login";
        }
    }

    @PostMapping("/doLogin")
    public String login(User user, HttpServletRequest request) {
        System.out.println("传入:" + user.getUsername() + user.getPassword() + user.isRemember());
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        System.out.println(token);
        try {
            token.setRememberMe(user.isRemember());
            subject.login(token);
            request.getSession().setAttribute("user", subject.getPrincipal());
            User users = loginServiceImpl.findUserByName(user.getUsername());
            request.getSession().setAttribute("currentUser", users);
            System.out.println(user.getUsername() + "在" + sdf.format(new Date()) + "登录成功!");
            return "redirect:LoginSuccess";
        } catch (AuthenticationException e) {
            e.printStackTrace();
            request.setAttribute("error", "请检查用户名或密码……");
            System.out.println(user.getUsername() + "在" + sdf.format(new Date()) + "登录失败!");
        }
        return "/login";
    }

    @RequestMapping("/LoginSuccess")
    public String ok() {
        return "/ok";
    }

    @RequestMapping("/exit")
    public String exit() {
        Subject subject = SecurityUtils.getSubject();
        String key = "roles_" + subject.getPrincipal();
        System.out.println("删除"+key);
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Set.class));
        Object obj = this.redisTemplate.opsForValue().get(key);
        if(obj!=null){
            this.redisTemplate.delete(key);
        }
        System.out.println(subject.toString());
        subject.logout();// session 会销毁，在SessionListener监听session销毁，清理权限缓存
        return "/login";
    }

    @ResponseBody
    @PostMapping("/comment")
    public String submitComment(Comment comment, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("currentUser");
        if (user == null) {
            return "请登录!";
        }
        boolean flag;
        try {
            comment.setUser(user);
            comment.setUid(user.getUid());
            flag = loginServiceImpl.addComment(comment);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        if (flag) {
            return "success";
        }
        return "发送失败！";
    }

    @RequestMapping("/messages/{type}")
    public String message(HttpSession session, Model model, @PathVariable String type) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return ERR403;
        }
        List<MsgUser> unread = loginServiceImpl.showMsg(user.getUid(), false, type);
        model.addAttribute("unread", unread);
        List<MsgUser> read = loginServiceImpl.showMsg(user.getUid(), true, type);
        model.addAttribute("read", read);
        boolean iSuccess = loginServiceImpl.renewMessages(user.getUid(), type);
        if (iSuccess) {
            Map<String, Object> msg = loginServiceImpl.showUnReadMsg(user.getUid());
            session.setAttribute("msg", msg);
        }
        return "/front/messages";
    }

    @ResponseBody
    @PostMapping("/messages")
    public boolean renewMessages(HttpSession session, String type) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            System.out.println(1);
            return false;
        }
        boolean iSuccess = loginServiceImpl.renewMessages(user.getUid(), type);
        if (iSuccess) {
            Map<String, Object> msg = loginServiceImpl.showUnReadMsg(user.getUid());
            session.setAttribute("msg", msg);
            return true;
        }
        System.out.println(2);
        return false;
    }
}
