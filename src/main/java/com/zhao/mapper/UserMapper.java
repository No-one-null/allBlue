package com.zhao.mapper;

import com.zhao.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {
    List<User> showAll();

    List<User> selectByPage(@Param("pageStart") int pageStart, @Param("pageSize") int pageSize);

    List<User> selectByName(@Param("username") String username);

    User selectUserByName(@Param("username") String username);

    //    List<User> selectByUser(User user);
    User selectByUser(User user);

    List<User> selectByKeyword(@Param("keyword") String keyword);

    long selectCount();

    User selectById(int uid);

    int insertUser(User user);

    List selectRolesByUsername(String username);
}
