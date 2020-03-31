package com.zhao.service;

import com.zhao.pojo.Comment;
import com.zhao.pojo.MsgUser;
import com.zhao.pojo.User;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LoginService {

    List<User> findByWord(String keyword);

    Boolean isExistName(String username);

    User findUserByName(String username);

    User checkUser(User user);

    User showOne(String uid);

    String addUser(User user);

    Set<String> checkRoles(String username);

    String editUsername(String password, String confirm, String newPass, User user);

    String editPassword(String password, String username, User user) throws Exception;

    boolean addComment(Comment comment) throws Exception;

    Map<String, Object> showUnReadMsg(int uid);

    List<MsgUser> showMsg(int uid, boolean read, String type);

    boolean renewMessages(int uid, String type);
}
