package com.zhao.service;

import com.zhao.pojo.User;

import java.util.List;

public interface LoginService {

    List<User> findByWord(String keyword);

    Boolean isExistName(String username);

    User findUserByName(String username);

    User checkUser(User user);

    User showOne(String uid);

    int addUser(User user);

    List checkRoles(String username);
}
