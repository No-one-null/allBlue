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

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private LoginService loginServiceImpl;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        System.out.println("执行拦截器");
        if (httpServletRequest.getSession().getAttribute("currentUser") == null) {
            if (SecurityUtils.getSubject().getPrincipal() != null) {
                User user = loginServiceImpl.findUserByName((String) SecurityUtils.getSubject().getPrincipal());
                httpServletRequest.getSession().setAttribute("currentUser", user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
