package com.zhao.test;


import com.zhao.App;
import com.zhao.mapper.MarkMapper;
import com.zhao.mapper.MsgContentMapper;
import com.zhao.mapper.MsgUserMapper;
import com.zhao.mapper.UserMapper;
import com.zhao.pojo.MsgContent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.zhao.pojo.User;

import javax.annotation.Resource;
import java.util.*;

/**
 * Spring Data Redis测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private MarkMapper markMapper;
    @Resource
    private MsgContentMapper msgContentMapper;
    @Resource
    private MsgUserMapper msgUserMapper;

    /**
     * 添加一个字符串
     */
    @Test
    public void testSet() {
        this.redisTemplate.opsForValue().set("key", "北京尚学堂");
    }

    /**
     * 获取一个字符串
     */
    @Test
    public void testGet() {
        String value = (String) this.redisTemplate.opsForValue().get("key");
        System.out.println(value);
    }

    /**
     * 添加Users对象
     */
    @Test
    public void testSetUesrs() {
        User users = new User();
        users.setUid(20);
        users.setUsername("张三丰");
        users.setGender("M");
        //重新设置序列化器
        this.redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        this.redisTemplate.opsForValue().set("users", users);
    }

    /**
     * 取Users对象
     */
    @Test
    public void testGetUsers() {
        //重新设置序列化器
        this.redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        User users = (User) this.redisTemplate.opsForValue().get("users");
        System.out.println(users);
    }

    /**
     * 基于JSON格式存Users对象
     */
    @Test
    public void testSetUsersUseJSON() {
        User users = userMapper.selectById(4);
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(User.class));
        this.redisTemplate.opsForValue().set("users_json", users);
    }

    /**
     * 基于JSON格式取Users对象
     */
    @Test
    public void testGetUseJSON() {
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(User.class));
        User users = (User) this.redisTemplate.opsForValue().get("users_json");
        System.out.println(users);
    }

    /**
     * 基于JSON格式存list对象
     */
    @Test
    public void testSetListUseJSON() {
        Set<?> roles = userMapper.selectRolesByUid(4);
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(List.class));
        this.redisTemplate.opsForValue().set("user_roles", roles);
    }

    /**
     * 基于JSON格式取Users对象
     */
    @Test
    public void testGetListJSON() {
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(List.class));
        List<?> users = (List<?>) this.redisTemplate.opsForValue().get("user_roles");
        System.out.println(users);
    }

    @Test
    public void testRoles() {
        System.out.println(userMapper.selectRolesByUid(4));
    }

    @Test
    public void test() {
        List<Integer> admins = userMapper.selectUidByRole("admin");
        System.out.println(admins);
        List<Integer> users = userMapper.selectUid();
        System.out.println(users);
        users.removeAll(admins);
        System.out.println(users);
    }

    @Test
    public void testInsert() {
        MsgContent msgContent = new MsgContent("title", "test", new Date());
        int index = msgContentMapper.insertOne(msgContent);
        int index1 = msgContentMapper.insertOne(msgContent);
        System.out.println(index + "," + index1 + "," + msgContent.getId());
    }

    @Test
    public void testSelect() {
        System.out.println(msgUserMapper.selectByStatus(5, 1));
    }

    @Test
    public void testRating(){
        List<Map<String,Long>> list=markMapper.selectRating(3);
        System.out.println(list);
        Map<Long,Long> map=new HashMap<>();
        int i=0;
        long[] numbers={0,0,0,0,0};
        System.out.println(Arrays.toString(numbers));
        for (Map<String,Long> m: list) {
            map.put(m.get("rating"),m.get("count"));
            numbers[Integer.parseInt(""+(m.get("rating")))-1]=m.get("count");
        }
        System.out.println(map);
        System.out.println(Arrays.toString(numbers));
    }

    @Test
    public void testMsg(){
        Long max=msgUserMapper.selectMaxMidByUidAndType(4,"all");
        System.out.println(max);
    }
}
