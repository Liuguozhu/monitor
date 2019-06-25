package com.coder.monitor.task;

//import com.coder.monitor.service.MongoService;

import com.coder.monitor.service.SendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 数据库(mongodb)监控
 */
@Component
@EnableScheduling
public class MongodbMonitoring {
    private static final Logger logger = LoggerFactory.getLogger(MongodbMonitoring.class);
    @Autowired
    private SendMessageService sendMessageService;

//    @Scheduled(fixedDelay = 5000)
//    @Scheduled(cron = "0 0/5 * * * ?")//每5分钟执行一次
//    private void monitorMongo() {
//        String message = "mongo 异常:";
//        String oneHost = null;
//        String dbName = null;
//        boolean isSendMess = false;
//        try {
//            for (String key : MongoService.map.keySet()) {
//                MongoService.monitorOne(key, MongoService.map.get(key));
//                oneHost = key;
//                dbName = MongoService.map.get(key);
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            message += oneHost;
//            message += ":";
//            message += dbName;
//            isSendMess = true;
//        }
//        if (isSendMess) {
//            sendMessageService.sendMessage(message);
//        }
//    }

}
