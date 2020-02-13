package com.zhao.service.impl;

import com.zhao.mapper.UserMapper;
import com.zhao.pojo.PageInfo;
import com.zhao.pojo.User;
import com.zhao.service.LoginService;
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
    public List<User> showAll() {
        return this.userMapper.showAll();
    }

    @Override
    public List<User> findByWord(String keyword) {
        return userMapper.selectByKeyword(keyword);
    }

    @Override
    public Boolean isExistName(String username) {
        List<User> users = userMapper.selectByName(username);
        if (users.size() > 0) {
            return true;
        } else {
            return false;
        }
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
        return userMapper.selectById(Integer.valueOf(uid));
    }

    @Override
    public int addUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public List checkRoles(String username) {
        return userMapper.selectRolesByUsername(username);
    }
}
