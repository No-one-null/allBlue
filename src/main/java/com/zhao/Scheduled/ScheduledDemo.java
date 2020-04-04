package com.zhao.Scheduled;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class ScheduledDemo {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 定时任务方法
     *
     * @Scheduled 设置定时任务
     * cron 属性：cron 表达式。定时任务触发是时间的一个字符串表达形式
     */
    @Scheduled(cron = "0 0 0/6 * * ?")
    public void scheduledMethod() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

//    @Scheduled(cron = "0/1 * * * * ?")
//    public void renewUnread(){
//        System.out.println("定时器被触发"+new Date());
//    }
}
