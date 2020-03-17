package com.zhao.service.impl;

import com.zhao.mapper.UserMapper;
import com.zhao.pojo.User;
import com.zhao.service.LoginService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<User> findByWord(String keyword) {
        return userMapper.selectByKeyword(keyword);
    }

    @Override
    public Boolean isExistName(String username) {
        User users = userMapper.selectByName(username, 0);
        return users == null;
    }

    @Override
    public User findUserByName(String username) {
        return userMapper.selectUserByName(username);
    }

    @Override
    public User checkUser(User user) {
        return userMapper.selectByUser(user);
    }

    @Override
    public User showOne(String uid) {
        return userMapper.selectById(Integer.parseInt(uid));
    }

    @Override
    public String addUser(User user) {
        User user1 = userMapper.selectUserByName(user.getUsername());
        if (user1 != null) {
            return "用户名已存在";
        }
        long num = userMapper.countField("email", user.getEmail());
        if (num > 0) {
            return "该邮箱已注册!";
        }
        Md5Hash md5Hash = new Md5Hash(user.getPassword(), user.getUsername());
        user.setPassword(md5Hash.toString());
        int result = userMapper.insertUser(user);
        if (result > 0) {
            return "success";
        }
        return "数据库更新失败!";
    }

    @Override
    public List<?> checkRoles(String username) {
        return userMapper.selectRolesByUsername(username);
    }
}
