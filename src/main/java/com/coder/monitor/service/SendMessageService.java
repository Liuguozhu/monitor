package com.coder.monitor.service;

import com.coder.monitor.config.service.EmailService;
import com.coder.monitor.config.service.SmsService;
import com.coder.monitor.exception.ApiAssert;
import com.coder.monitor.util.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LGZ
 * @package com.coder.monitor.service
 * @className WeChatService
 * @description monitor WeChatService
 * @date 2019/6/21 13:51:09
 */
@Service
@Transactional
public class SendMessageService {
    private static Logger log = LoggerFactory.getLogger(SendMessageService.class);
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private WeChatService weChatService;
    /**
     * 主动向微信公众号推送消息
     *
     * @param message
     * @return
     */
    public String sendMessage(String message) {
        String notifyWay = systemConfigService.selectByKey("notify_way").getValue();
        String[] s = notifyWay.split(",");
        for (String way : s) {
            switch (way) {//TODO 这些个case要和数据库中notify_way的选项完全一致才能找到
                case "email":
                    sendMessageToEmail(message);
                    break;
                case "sms_tencent":
                    sendMessageToSMSTencent(message);
                    break;
                case "sms_ali":
                    sendMessageToSMSAliYun(message);
                    break;
                case "wechat":
                    sendMessageToQy(message);
                    break;
                    //TODO 增加微信订阅号或服务号（这两者相同），公众号的消息推送
                default:
                    log.info("notify way [{}] is nonexistent!", way);
                    break;
            }
        }
        return "0000";//TODO
    }


    private void sendMessageToQy(String message) {
        weChatService.sendMessageToQy(message);
    }

    private void sendMessageToSMSTencent(String message) {
        String mobile="";//TODO 获取接收异常通知的配置，拿到mobile号码
        smsService.sendSmsByTencent(mobile, message);
    }
    private void sendMessageToSMSAliYun(String message) {
        String mobile="";//TODO 获取接收异常通知的配置，拿到mobile号码
        smsService.sendSmsByAliYun(mobile, message);
    }

    private void sendMessageToEmail(String message) {
        String email="";//TODO 获取接收异常通知的配置，拿到email地址
        // 发送激活邮件
        new Thread(() -> {
            String title = "服务器异常通知";
            String content = "服务器异常通知：%s";
            emailService.sendEmail(email, title, String.format(content, message));
        }).start();
    }

}
