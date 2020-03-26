package com.zhao.service;

import com.zhao.pojo.User;

import java.util.List;
import java.util.Set;

public interface LoginService {

    List<User> findByWord(String keyword);

    Boolean isExistName(String username);

    User findUserByName(String username);

    User checkUser(User user);

    User showOne(String uid);

    String addUser(User user);

    Set<String> checkRoles(String username);
}
