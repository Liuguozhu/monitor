package com.coder.monitor.service;

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
public class WeChatService {
    private static Logger log = LoggerFactory.getLogger(WeChatService.class);
    @Autowired
    private SystemConfigService systemConfigService;
    private static String ACCESS_TOKEN;//公众号的全局唯一票据
    private static long EXPIRES_TIME;//过期时间

    private static String APP_ID;
    private static String APP_SECRET;
    private static String TOKEN;
    private static String AGENT_ID;//企业号应用id,在企业微信创建某个应用，其实就是类似微信群，群里可以添加企业微信内的员工，这个群组的人都将收到通知的消息

    private void initProperties() {
        Object appId = systemConfigService.selectAllConfig().get("appId");
        Object appSecret = systemConfigService.selectAllConfig().get("appSecret");
        Object token = systemConfigService.selectAllConfig().get("token");
        Object agentId = systemConfigService.selectAllConfig().get("agentId");
        ApiAssert.notNull(appId, "未配置微信appId");
        ApiAssert.notNull(appSecret, "未配置微信appSecret");
        ApiAssert.notNull(token, "未配置微信token");
        ApiAssert.notNull(agentId, "未配置微信agentId");
        APP_ID = appId.toString();
        APP_SECRET = appSecret.toString();
        TOKEN = token.toString();
        AGENT_ID = agentId.toString();
    }

    /**
     * 验证token是否为空或过期
     */
    public void checkToken() {
        if (ACCESS_TOKEN == null || AGENT_ID == null || APP_ID == null)
            getWxToken();
        if (System.currentTimeMillis() > EXPIRES_TIME)
            getWxToken();
    }

    /**
     * LGZ
     * 获取微信的token
     */
    public void getWxToken() {
        log.debug("开始获取微信公众号的token");
        if (APP_ID == null || APP_SECRET == null || TOKEN == null || AGENT_ID == null)
            initProperties();
        //TODO 后续修改为通过后台配置选择订阅号还是服务号或者都使用
//        String reqUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APP_ID + "&secret=" + APP_SECRET;//订阅号和服务号
        String reqUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + APP_ID + "&corpsecret=" + APP_SECRET;//企业号
        HttpGet httpGet = new HttpGet(reqUrl);
        HttpResponse response = null;
        try {
            response = HttpClients.createDefault().execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                String result = EntityUtils.toString(response.getEntity());
                log.debug("获取token result=" + result);
                Map map = JsonUtil.jsonToObject(result, Map.class);
                ApiAssert.notNull(map, "获取微信企业号的token失败！请检查配置");
                ACCESS_TOKEN = (String) map.get("access_token");//获取到的凭证
                log.debug("ACCESS_TOKEN=" + ACCESS_TOKEN);
                Object expiresIn = map.get("expires_in");//凭证有效时间，单位：秒
                ApiAssert.notNull(expiresIn, "获取微信企业号的token失败！expires_in 为空");
                int time = (Integer)expiresIn;//凭证有效时间，单位：秒

                EXPIRES_TIME = System.currentTimeMillis() + time * 1000;
                log.debug("expires_in=" + time);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.error("获取微信 ACCESS_TOKEN 失败：" + response.getStatusLine().getStatusCode());
        }
    }

    /**
     * 订阅号验证
     *
     * @param request
     * @param response
     */
    public void checkSignatureDY(HttpServletRequest request, HttpServletResponse response) {
        String message = "success";
        String signature = request.getParameter("signature");//微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
        String timestamp = request.getParameter("timestamp");//时间戳
        String nonce = request.getParameter("nonce");//随机数
        String echostr = request.getParameter("echostr");//随机字符串
        log.debug("微信发送的消息signature=" + signature + " timestamp=" + timestamp + " nonce=" + nonce);
        log.debug("微信发送的消息echostr=" + echostr);

        if (echostr != null)
            message = echostr;

        if (APP_ID == null || APP_SECRET == null || TOKEN == null)
            initProperties();
        String[] arrays = {TOKEN, timestamp, nonce};
        Arrays.sort(arrays);
        StringBuilder str = new StringBuilder();
        for (String s : arrays) {
            str.append(s);
        }
        String sha1 = SHA1(str.toString());
        log.debug("微信发送加密字符是=" + signature);
        log.debug("生成的加密字符串是=" + sha1);
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert out != null;
        if (signature != null && signature.equals(sha1)) {
            log.debug("验证成功");
            String resultMessage = formatXmlAnswer(request);
            if (!TextUtils.isBlank(resultMessage)) {
                message = resultMessage;
            }
            out.write(message);
        } else {
            log.debug("验证失败");
            out.write("fail");
        }
        out.close();
    }

    /**
     * 企业号验证
     *
     * @param request
     * @param response
     */
    public void checkSignatureQY(HttpServletRequest request, HttpServletResponse response) {
        String message = "success";
//        String signature = request.getParameter("signature");//微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
        String signature = request.getParameter("msg_signature");//企业号的加密签名，和订阅号、服务号的参数名不一样
        String timestamp = request.getParameter("timestamp");//时间戳
        String nonce = request.getParameter("nonce");//随机数
        String echostr = request.getParameter("echostr");//随机字符串
        log.debug("微信发送的消息signature=" + signature + " timestamp=" + timestamp + " nonce=" + nonce);
        log.debug("微信发送的消息echostr=" + echostr);

        if (echostr != null)
            message = echostr;

        if (APP_ID == null || APP_SECRET == null || TOKEN == null)
            initProperties();
        String[] arrays = {TOKEN, timestamp, nonce};
        Arrays.sort(arrays);
        StringBuilder str = new StringBuilder();
        for (String s : arrays) {
            str.append(s);
        }
        String sha1 = SHA1(str.toString());
        log.debug("微信发送加密字符是=" + signature);
        log.debug("生成的加密字符串是=" + sha1);
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert out != null;
        if (signature != null && signature.equals(sha1)) {
            log.debug("验证成功");
            String resultMessage = formatXmlAnswer(request);
            if (!TextUtils.isBlank(resultMessage)) {
                message = resultMessage;
            }
            out.write(message);
        } else {
            log.debug("验证失败");
            out.write("fail");
        }
        out.close();
    }


    /**
     * SHA1加密
     *
     * @param decript
     * @return
     */
    private static String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2)
                    hexString.append(0);

                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA1加密异常=" + e.getMessage());
        }
        return "";
    }

    /**
     * 主动向微信公众号推送消息
     *
     * @param message
     * @return
     */
    public String sendMessage(String message) {
        return sendMessageToQy(message);
    }

    /**
     * 主动向微信订阅号或服务号推送消息
     *
     * @param message
     * @return
     */
    public String sendMessageToDY(String message) {
        checkToken();
        String reqUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=" + ACCESS_TOKEN;

        CloseableHttpResponse resp;
        CloseableHttpClient client;

        HttpPost httpPost = new HttpPost(reqUrl);

        Map<String, Object> filter = new HashMap<>();
        filter.put("is_to_all", true);
        filter.put("group_id", 1);
        Map<String, Object> text = new HashMap<>();
        text.put("content", message);

        Map<String, Object> map = new HashMap<>();
        map.put("filter", filter);
        map.put("text", text);
        map.put("msgtype", "text");
        String params = JsonUtil.objectToJson(map);
        log.debug("请求微信推送的内容=" + params);
        StringEntity entityParams = new StringEntity(params, "utf-8");
        httpPost.setEntity(entityParams);

        client = HttpClients.createDefault();
        try {
            resp = client.execute(httpPost);
            HttpEntity entity = resp.getEntity();
            String s = EntityUtils.toString(entity, "utf-8");
            log.debug("微信响应=" + s);
            return s;
        } catch (IOException e) {
            log.error("ERROR=" + e.getMessage());
            return "send fail";
        }
    }

    /**
     * 主动向微信企业号推送消息
     *
     * @param message
     * @return
     */
    public String sendMessageToQy(String message) {
        checkToken();
        String reqUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + ACCESS_TOKEN;

        CloseableHttpResponse resp = null;
        CloseableHttpClient client = null;

        HttpPost httpPost = new HttpPost(reqUrl);

        Map<String, Object> text = new HashMap<String, Object>();
        text.put("content", message);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("touser", "@all");
        map.put("toparty", "@all");
        map.put("totag", "@all");
        map.put("msgtype", "text");
        map.put("agentid", AGENT_ID);
        map.put("text", text);
        map.put("safe", "0");
        String params = JsonUtil.objectToJson(map);
        log.debug("请求微信推送的内容=" + params);
        StringEntity entityParams = new StringEntity(params, "utf-8");
        httpPost.setEntity(entityParams);

        client = HttpClients.createDefault();
        try {
            resp = client.execute(httpPost);
            HttpEntity entity = resp.getEntity();
            String s = EntityUtils.toString(entity, "utf-8");
            log.debug("微信响应=" + s);
            return s;
        } catch (IOException e) {
            log.error("ERROR=" + e.getMessage());
            return "send fail";
        }
    }

    /**
     * 主动向微信公众号推送消息
     *
     * @param message
     * @return
     */
    public String sendMessage2(String message) {
        checkToken();
        String reqUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=" + ACCESS_TOKEN;
        CloseableHttpResponse resp = null;
        CloseableHttpClient client = null;

        HttpPost httpPost = new HttpPost(reqUrl);
        //TODO 这里填写指定用户的openId，后续会通过后台配置
        String[] toUser = {"oBkjww1msdpNLn-OigM7X5LKyyt0", "oBkjww1msdpNLn-OigM7X5LKyyt0"};

        Map<String, Object> text = new HashMap<String, Object>();
        text.put("content", message);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("touser", toUser);
        map.put("text", text);
        map.put("msgtype", "text");
        String params = JsonUtil.objectToJson(map);
        log.debug("sendMessage2 请求微信推送的内容=" + params);
        StringEntity entityParams = new StringEntity(params, "utf-8");
        httpPost.setEntity(entityParams);

        client = HttpClients.createDefault();
        try {
            resp = client.execute(httpPost);
            HttpEntity entity = resp.getEntity();
            String s = EntityUtils.toString(entity, "utf-8");
            log.debug("sendMessage2 微信响应=" + s);
            return s;
        } catch (IOException e) {
            log.error("sendMessage2 ERROR=" + e.getMessage());
            return "send fail";
        }
    }

    private static Map<String, Object> parseXml(HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();
        InputStream inputStream = request.getInputStream();
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        Element root = document.getRootElement();
        List<Element> list = root.elements();
        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        inputStream.close();
        return map;
    }


    private String formatXmlAnswer(HttpServletRequest request) {
        Map<String, Object> map;
        StringBuilder sb = new StringBuilder();
        try {
            map = parseXml(request);
            log.debug("接收到的消息 map=" + map);
            if (map == null)
                return "";

            sb.append("<xml><ToUserName>");
            sb.append(map.get("FromUserName"));
            sb.append("</ToUserName><FromUserName>");
            sb.append(map.get("ToUserName"));
            sb.append("</FromUserName><CreateTime>");
            sb.append(System.currentTimeMillis());
            sb.append("</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[");
            sb.append("自动回复消息：你好");
            sb.append("]]></Content></xml>");
        } catch (Exception e) {
            log.error("解析xml异常=" + e.getMessage());
            e.printStackTrace();
        }
        return sb.toString();
    }
}
