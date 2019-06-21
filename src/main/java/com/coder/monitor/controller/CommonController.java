
package com.coder.monitor.controller;

import com.coder.monitor.model.SystemConfig;
import com.coder.monitor.service.CodeService;
import com.coder.monitor.service.SystemConfigService;
import com.coder.monitor.service.WeChatService;
import com.coder.monitor.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Controller
public class CommonController {
    private static Logger log = LoggerFactory.getLogger(CommonController.class);
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CodeService codeService;
    @Autowired
    private WeChatService weChatService;

    @RequestMapping(value = "/sendMassageAndRestartServer")
    public String sendMassageAndRestartServer(String massage) throws UnsupportedEncodingException {
        String encodemassage = new String(massage.getBytes("iso8859-1"), StandardCharsets.UTF_8);
        // host
        SystemConfig systemConfig = systemConfigService.selectByKey("receive_mobile");
        String receive_mobile = systemConfig.getValue();
        codeService.sendSms(receive_mobile);
//        SendSmsCodeUtil.sendMonitorMessage(encodemassage);
        BufferedReader br = null;
        boolean b = false;
        try {
            Process p = Runtime.getRuntime().exec("/root/shell/check_tomcat_wosdk.sh");
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            if (!(sb.toString().length() > 10)) {
                b = true;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(massage);
        return "1111";
    }


    /**
     * LGZ
     * 微信验证接口
     * 加密/校验流程如下：
     * 1. 将token、timestamp、nonce三个参数进行字典序排序
     * 2. 将三个参数字符串拼接成一个字符串进行sha1加密
     * 3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
     */
    @ResponseBody
    @RequestMapping(value = "/checkSignature")
    public void checkSignature(HttpServletRequest request, HttpServletResponse response) {
        log.debug("开始验证微信公众号接口请求");
        weChatService.checkSignatureQY(request, response);
    }


    /**
     * 主动向微信公众号推送消息接口
     *
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/pushMessage")
    public void pushMessage(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pushMsg = request.getParameter("message");
        String ip = RequestUtils.getIpAddress(request);
        log.error("请求向微信公众号发送消息 发送者 Ip=" + ip + " 消息内容 pushMsg=" + pushMsg);
        if (pushMsg == null)
            pushMsg = "推送测试消息";
        String message = weChatService.sendMessage(pushMsg);
//        message += " /r/n ";
//        message += sendMessage2("推送测试消息2");
        if (out != null) {
            out.print(message);
            out.close();
        }
    }


}

