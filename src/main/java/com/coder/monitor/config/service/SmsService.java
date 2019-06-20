package com.coder.monitor.config.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.coder.monitor.service.SystemConfigService;
import com.coder.monitor.util.JsonUtil;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

@Component
@DependsOn("mybatisPlusConfig")
public class SmsService {

    private Logger log = LoggerFactory.getLogger(SmsService.class);

    @Autowired
    private SystemConfigService systemConfigService;

    private IAcsClient client;
    private String signName;
    private String templateCode;
    private String regionId;
    // 短信应用 SDK AppID
    private int appid = 1400209480; // SDK AppID 以1400开头
    // 短信应用 SDK AppKey
    private String appkey = "22088250b177d33164cc9383b0fe1bf4";
    // 短信模板 ID，需要在短信应用中申请
    private int templateId = 330850; // NOTE: 这里的模板 ID`7839`只是示例，真实的模板 ID 需要在短信控制台中申请
    private boolean useAliYunSms = false;

    private SmsService() {
    }

    public IAcsClient instance() {
        if (client != null) return client;
        String accessKeyId = (String) systemConfigService.selectAllConfig().get("sms_access_key_id");
        String secret = (String) systemConfigService.selectAllConfig().get("sms_secret");
        signName = (String) systemConfigService.selectAllConfig().get("sms_sign_name");
        templateCode = (String) systemConfigService.selectAllConfig().get("sms_template_code");
        regionId = (String) systemConfigService.selectAllConfig().get("sms_region_id");
        appid = (Integer) systemConfigService.selectAllConfig().get("sms_tencent_app_id");
        appkey = (String) systemConfigService.selectAllConfig().get("sms_tencent_app_key");
        useAliYunSms = systemConfigService.selectAllConfig().get("use_aliyun_sms").toString().equals("1");
        if (StringUtils.isEmpty(accessKeyId)
                || StringUtils.isEmpty(secret)
                || StringUtils.isEmpty(signName)
                || StringUtils.isEmpty(templateCode)
                || StringUtils.isEmpty(regionId)) {
            return null;
        }
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        this.client = client;
        return client;
    }

    // 发短信
    public boolean sendSms(String mobile, String code) {
        if (useAliYunSms) {//使用阿里云的短信服务
            return sendSmsByAliYun(mobile, code);
        }
        return sendSmsByTencent(mobile, code);//默认使用腾讯云的短信服务
//        return sendSmsByTencentWithTemplate(mobile, code);//默认使用腾讯云的短信服务
    }

    // 发短信
    private boolean sendSmsByAliYun(String mobile, String code) {
        try {
            if (StringUtils.isEmpty(mobile)) return false;
            // 获取连接
            if (this.instance() == null) return false;
            // 构建请求体
            CommonRequest request = new CommonRequest();
            //request.setProtocol(ProtocolType.HTTPS);
            request.setMethod(MethodType.POST);
            request.setDomain("dysmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SendSms");
            request.putQueryParameter("RegionId", regionId);
            request.putQueryParameter("PhoneNumbers", mobile);
            request.putQueryParameter("SignName", signName);
            request.putQueryParameter("TemplateCode", templateCode);
            request.putQueryParameter("TemplateParam", String.format("{\"code\": \"%s\"}", code));
            CommonResponse response = client.getCommonResponse(request);
            //{"Message":"OK","RequestId":"93E35E66-B2B2-4D7A-8AC9-2BDD97F5FB18","BizId":"689615750980282428^0","Code":"OK"}
            Map responseMap = JsonUtil.jsonToObject(response.getData(), Map.class);
            if (responseMap != null && responseMap.get("Code").equals("OK")) return true;
        } catch (ClientException e) {
            log.error(e.getMessage());
        }
        return false;
    }


    // 发短信
    private boolean sendSmsByTencent(String mobile, String code) {
        log.info("mobile = {}  code ={}", mobile, code);
        if (appkey == null) {
            appid = 1400209480; // SDK AppID 以1400开头
            appkey = "22088250b177d33164cc9383b0fe1bf4";  // 短信应用 SDK AppKey
        }
        System.out.println();
        try {
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            //type 0表示普通短信，1表示营销短信
            SmsSingleSenderResult result = ssender.send(0, "86", mobile,
                    "【掌中飞天】您的验证码是" + code + "，请于5分钟内填写.如非本人操作，请忽略此短信。", "", "");
            System.out.println(result);
            log.info("result = {} ", result);
            if (result.result == 0) {
                return true;
            }
        } catch (HTTPException | JSONException | IOException e) {
            // HTTP 响应码错误
            e.printStackTrace();
        }
        return false;
    }

    // 发短信
    private boolean sendSmsByTencentWithTemplate(String mobile, String code) {
        log.info("mobile = {}  code ={}", mobile, code);
        if (appkey == null) {
            appid = 1400209480; // SDK AppID 以1400开头
            appkey = "22088250b177d33164cc9383b0fe1bf4";  // 短信应用 SDK AppKey
        }
        try {
            String[] params = {code};
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.sendWithParam("86", mobile,
                    templateId, params, null, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            System.out.println(result);
            log.info("result = {} ", result);
            if (result.result == 0) {
                return true;
            }
        } catch (HTTPException | JSONException | IOException e) {
            // HTTP 响应码错误
            e.printStackTrace();
        }
        return false;
    }

}
