package com.zhao.test;


import com.zhao.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.zhao.App;
import com.zhao.pojo.User;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Spring Data Redis测试
 *
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=App.class)
public class RedisTest {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Resource
	private UserMapper userMapper;
	
	/**
	 * 添加一个字符串
	 */
	@Test
	public void testSet(){
		this.redisTemplate.opsForValue().set("key", "北京尚学堂");
	}
	
	/**
	 * 获取一个字符串
	 */
	@Test
	public void testGet(){
		String value = (String)this.redisTemplate.opsForValue().get("key");
		System.out.println(value);
	}
	
	/**
	 * 添加Users对象
	 */
	@Test
	public void testSetUesrs(){
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
	public void testGetUsers(){
		//重新设置序列化器
		this.redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		User users = (User)this.redisTemplate.opsForValue().get("users");
		System.out.println(users);
	}
	
	/**
	 * 基于JSON格式存Users对象
	 */
	@Test
	public void testSetUsersUseJSON(){
		User users = userMapper.selectById(4);
		this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(User.class));
		this.redisTemplate.opsForValue().set("users_json", users);
	}
	
	/**
	 * 基于JSON格式取Users对象
	 */
	@Test
	public void testGetUseJSON(){
		this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(User.class));
		User users = (User)this.redisTemplate.opsForValue().get("users_json");
		System.out.println(users);
	}

	/**
	 * 基于JSON格式存list对象
	 */
	@Test
	public void testSetListUseJSON(){
		Set<?> roles = userMapper.selectRolesByUsername("zero");
		this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(List.class));
		this.redisTemplate.opsForValue().set("user_roles", roles);
	}

	/**
	 * 基于JSON格式取Users对象
	 */
	@Test
	public void testGetListJSON(){
		this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(List.class));
		List<?> users = (List<?>)this.redisTemplate.opsForValue().get("user_roles");
		System.out.println(users);
	}

	@Test
	public void testRoles(){
		System.out.println(userMapper.selectRolesByUsername("zero"));
	}
}
