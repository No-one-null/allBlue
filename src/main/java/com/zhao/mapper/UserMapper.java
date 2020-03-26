package com.zhao.mapper;

import com.zhao.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface UserMapper {
    List<User> showAll();

    List<User> selectByPage(@Param("pageStart") int pageStart, @Param("pageSize") int pageSize);

    User selectByName(@Param("username") String username, @Param("status") int status);

    User selectUserByName(@Param("username") String username);

    //    List<User> selectByUser(User user);
    User selectByUser(User user);

    List<User> selectByKeyword(@Param("keyword") String keyword);

    long selectCount();

    User selectById(int uid);

    int insertUser(User user);

    Set<String> selectRolesByUsername(String username);

    long countField(@Param("field") String field, @Param("key") String key);

    List<User> selectByKey(@Param("field") String field, @Param("key") String key);

    int updateUser(User user);
}
