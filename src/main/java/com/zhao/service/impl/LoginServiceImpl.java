package com.zhao.service.impl;

import com.zhao.mapper.*;
import com.zhao.pojo.*;
import com.zhao.service.LoginService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

import static com.zhao.util.CommonUtil.*;
import static com.zhao.util.Constant.COMMENT_MAXSIZE;
import static com.zhao.util.Constant.CONTENT_MAXSIZE;

@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private TalkMapper talkMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private MsgContentMapper msgContentMapper;
    @Resource
    private MsgUserMapper msgUserMapper;

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
        return userMapper.selectUserByName(username);
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
        String key = "roles_" + username;
        this.redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Set.class));
        Object obj = this.redisTemplate.opsForValue().get(key);
        Set<String> roles = castSet(obj, String.class);
        if (roles == null) {
            User user = userMapper.selectUserByName(username);
            roles = userMapper.selectRolesByUid(user.getUid());
            this.redisTemplate.opsForValue().set(key, roles);
        }
        return roles;
    }

    @Override
    public String editUsername(String password, String confirm, String newPass, User user) {
        Md5Hash md5Hash = new Md5Hash(password, user.getUsername());
        if (!md5Hash.toString().equals(user.getPassword())) {
            return "密码不正确！";
        }
        if (!confirm.equals(newPass)) {
            return "密码不一致!";
        }
        if (newPass.equals(password)) {
            return "密码没有被修改！";
        }
        if (newPass.length() > 12 || newPass.length() < 6) {
            return "密码应在6~12字符之间！";
        }
        Md5Hash newMd5 = new Md5Hash(newPass, user.getUsername());
        int results = userMapper.updateField(user.getUid(), "password", newMd5.toString());
        if (results <= 0) {
            return "数据库更新失败！";
        }
        return "success";
    }

    @Transactional
    @Override
    public String editPassword(String password, String username, User user) throws Exception {
        Md5Hash md5Hash = new Md5Hash(password, user.getUsername());
        if (!md5Hash.toString().equals(user.getPassword())) {
            return "密码不正确！";
        }
        if (username.equals(user.getUsername())) {
            return "没有修改用户名！";
        }
        User exist = userMapper.selectUserByName(username);
        if (exist != null) {
            return "用户名已存在！";
        }
        Md5Hash newMd5 = new Md5Hash(password, username);
        int result1 = userMapper.updateField(user.getUid(), "username", username);
        int result2 = userMapper.updateField(user.getUid(), "password", newMd5.toString());
        if (result1 <= 0 || result2 <= 0) {
            throw new Exception("数据库更新失败!");
        }
        return "success";
    }

    @Override
    public Map<String, Object> showUnReadMsg(int uid) {
        Map<String, Object> msg = new HashMap<>();
        Long sysNum = msgUserMapper.countMsg(uid, 0, "sys");
        msg.put("sys", sysNum);
        Long userNum = msgUserMapper.countMsg(uid, 0, "user");
        msg.put("user", userNum);
        return msg;
    }

    @Transactional
    @Override
    public boolean addComment(Comment comment) throws Exception {
        String content = comment.getContent();
        if (content.trim().equals("")) {
            throw new Exception("内容不能为空!");
        } else {
            content = replaceBlank(content);
            if (content.length() > CONTENT_MAXSIZE) {
                content = content.substring(0, COMMENT_MAXSIZE);
            }
            comment.setContent(content);
        }
        comment.setTime(new Date());
        int result = commentMapper.insertOne(comment);
        if (result != 1) {
            throw new Exception("数据库操作异常!");
        }
        Talk talk = talkMapper.selectOne(comment.getTopicId());
        MsgContent msgcontent = new MsgContent();
        int uid = -1;
        if (talk.getUid() != comment.getUser().getUid() && comment.getToUid() == 0) {
            msgcontent.setTitle("@" + comment.getUser().getUsername() + "评论了您的动态");
            uid = talk.getUid();
        }
        if (comment.getToUid() != 0 && comment.getToUid() != comment.getUid()) {
            msgcontent.setTitle("@" + comment.getUser().getUsername() + "回复了你的评论");
            uid = comment.getToUid();
        }
        if (uid != -1) {
            msgcontent.setMessage((content.length() > 10 ? content.substring(0, 10) : content) +
                    "　<a href='/allBlue/topic/t" + comment.getTopicId() + "'>动态详情</a>");
            msgcontent.setCreateDate(new Date());
            int result1 = msgContentMapper.insertOne(msgcontent);
            MsgUser msgUser = new MsgUser(msgcontent.getId(), uid, "user");
            int result2 = msgUserMapper.insertOne(msgUser);
            if (result1 <= 0 || result2 <= 0) {
                throw new Exception("数据插入失败!");
            }
        }
        return true;
    }

    /**
     * 根据参数获取消息列表
     *
     * @param uid  用户id
     * @param read 是否已读
     * @param type 消息类型
     * @return 返回的消息列表
     */
    @Override
    public List<MsgUser> showMsg(int uid, boolean read, String type) {
        int status;
        if (read) {
            status = 1;
        } else {
            status = 0;
        }
        String t;
        if (type.equals("sys") || type.equals("user")) {
            t = type;
        } else {
            t = null;
        }
        return msgUserMapper.selectByType(uid, status, t);
    }

    @Override
    public boolean renewMessages(int uid, String type) {
        if (!type.equals("sys") && !type.equals("user")) {
            return false;
        }
        int result = msgUserMapper.updateMsg(uid, type);
        return result >= 0;
    }
}
