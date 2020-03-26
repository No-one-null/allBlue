package com.zhao.service.impl;

import com.zhao.mapper.UserMapper;
import com.zhao.pojo.User;
import com.zhao.service.LoginService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
        String u ="user_"+username;
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(User.class));
        User user= (User) this.redisTemplate.opsForValue().get(u);
        if(user==null){
            user=userMapper.selectUserByName(username);
            this.redisTemplate.opsForValue().set(u,user);
        }
        return user;
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
    public Set<String> checkRoles(String username) {
        String key="roles_"+username;
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(List.class));
        Set<String> roles = (Set<String>) this.redisTemplate.opsForValue().get(key);
        if(roles==null){
            roles=userMapper.selectRolesByUsername(username);
            this.redisTemplate.opsForValue().set(key, roles);
        }
        return roles;
    }
}
