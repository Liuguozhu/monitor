package com.coder.monitor.task;

import com.coder.monitor.config.service.RedisService;
import com.coder.monitor.controller.CommonController;
import com.coder.monitor.service.WeChatService;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

/**
 * redis监控
 */
public class RedisMonitoring {
    private static final Logger logger = LoggerFactory.getLogger(RedisMonitoring.class);

    private static final String key = "monitoring";

    @Resource(name = "redisService")
    private RedisService redisService;

    @Autowired
    private WeChatService weChatService;

    public void checkRedis() {
        logger.debug("check Redis begin.");
        String errorMessage = "";
        Config.initDataSourceList(Config.REDIS);
        if (Config.redisInfoList != null && Config.redisInfoList.size() > 0) {
            for (Config.RedisInfo redisInfo : Config.redisInfoList) {
                String ip = redisInfo.getIp();
                int port = Integer.parseInt(redisInfo.getPort());
                try {
                    executeOperate(ip, port);
                } catch (Exception e) {
                    errorMessage += "redis ip=" + ip + " port=" + port + " error=" + e.getMessage();
                }
            }
        }
        if (!TextUtils.isBlank(errorMessage)) {
            logger.debug("redis开始发送异常消息");
            weChatService.sendMessage(errorMessage);
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
