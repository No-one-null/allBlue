package com.zhao.mapper;

import com.zhao.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

public interface UserMapper {
    @Select("select * from user")
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

    Set<String> selectRolesByUid(int uid);

    long countField(@Param("field") String field, @Param("key") String key);

    List<User> selectByKey(@Param("field") String field, @Param("key") String key);

    @Select("SELECT uid FROM user_roles WHERE role=#{0}")
    List<Integer> selectUidByRole(String role);

    @Select("SELECT uid FROM user ORDER BY uid")
    List<Integer> selectUid();

    int updateUser(User user);

    int updateField(@Param("uid") int uid, @Param("field") String field, @Param("param") String param);
}
