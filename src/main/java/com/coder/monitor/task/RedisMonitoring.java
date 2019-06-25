package com.coder.monitor.task;

import com.coder.monitor.service.SendMessageService;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

/**
 * redis监控
 */
public class RedisMonitoring {
    private static final Logger logger = LoggerFactory.getLogger(RedisMonitoring.class);
    private static final String key = "monitoring";
    @Autowired
    private SendMessageService sendMessageService;

    @Scheduled(cron = "0 0/5 * * * ?")//每5分钟执行一次
    public void checkRedis() {
        logger.debug("check Redis begin.");
        StringBuilder errorMessage = new StringBuilder();
        Config.initDataSourceList(Config.REDIS);
        if (Config.redisInfoList != null && Config.redisInfoList.size() > 0) {
            for (Config.RedisInfo redisInfo : Config.redisInfoList) {
                String ip = redisInfo.getIp();
                int port = Integer.parseInt(redisInfo.getPort());
                try {
                    executeOperate(ip, port);
                } catch (Exception e) {
                    errorMessage.append("redis ip=").append(ip).append(" port=").append(port).append(" error=").append(e.getMessage());
                }
            }
        }
        if (!TextUtils.isBlank(errorMessage.toString())) {
            logger.debug("redis开始发送异常消息");
            sendMessageService.sendMessage(errorMessage.toString());
        }
        logger.debug("check Redis end.");
    }


    private void executeOperate(String ip, int port) {
        Jedis jedis = null;
        Exception e = null;
        for (int i = 0; i < 3; i++) {
            try {
                jedis = new Jedis(ip, port, 30);
            } catch (Exception exc) {
                e = exc;
            }
            if (jedis != null)
                break;
        }
        if (jedis == null)
            throw new RuntimeException(e.getMessage());
        long id = put(jedis);
        if (id <= 0)
            throw new RuntimeException("insert error");
        del(jedis);
        jedis.close();
    }

    private Long put(Jedis jedis) {
        return jedis.append(key, "test");
    }

    private void del(Jedis jedis) {
        jedis.del(key);
    }
}
