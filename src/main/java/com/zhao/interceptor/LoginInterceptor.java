package com.zhao.interceptor;

import com.zhao.pojo.User;
import com.zhao.service.LoginService;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private LoginService loginServiceImpl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        System.out.println("执行拦截器");
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            if (SecurityUtils.getSubject().getPrincipal() != null) {
                User user = loginServiceImpl.findUserByName((String) SecurityUtils.getSubject().getPrincipal());
                request.getSession().setAttribute("currentUser", user);
            }
        } else {
            Map<String, Object> msg = loginServiceImpl.showUnReadMsg(currentUser.getUid());
            request.getSession().setAttribute("msg", msg);
        }
        if (request.getSession().getAttribute("fakeInfo") != null) {
            throw new Exception("Exception未使用的警告");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (request.getSession().getAttribute("fakeInfo") != null) {
            throw new Exception("Exception未使用的警告");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        if (request.getSession().getAttribute("fakeInfo") != null) {
            throw new Exception("Exception未使用的警告");
        }
    }
}
