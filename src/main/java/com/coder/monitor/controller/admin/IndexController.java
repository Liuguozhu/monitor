package com.coder.monitor.controller.admin;

import com.coder.monitor.service.AdminUserService;
import com.coder.monitor.service.CodeService;
import com.coder.monitor.service.SystemConfigService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

@Controller
public class IndexController extends BaseController {

    private Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private CodeService codeService;
    @Autowired
    private AdminUserService adminUserService;

    // 登录后台
    @GetMapping("/adminlogin")
    public String adminlogin() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) return redirect("/admin/index");
        return "admin/login";
    }

    // 处理后台登录逻辑
    @PostMapping("/adminlogin")
    public String adminlogin(String username, String password, String code, HttpSession session,
                             @RequestParam(defaultValue = "0") Boolean rememberMe,
                             HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String captcha = (String) session.getAttribute("_captcha");
        if (captcha == null || StringUtils.isEmpty(code) || !captcha.equalsIgnoreCase(code)) {
            redirectAttributes.addFlashAttribute("error", "验证码不正确");
        } else {
            try {
                // 添加用户认证信息
                Subject subject = SecurityUtils.getSubject();
                if (!subject.isAuthenticated()) {
                    UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
                    //进行验证，这里可以捕获异常，然后返回对应信息
                    subject.login(token);
                }
            } catch (AuthenticationException e) {
                log.error(e.getMessage());
                redirectAttributes.addFlashAttribute("error", "用户名或密码错误");
                redirectAttributes.addFlashAttribute("username", username);
                return redirect("/adminlogin");
            }
        }
        String url = WebUtils.getSavedRequest(request) == null ? "/admin/index" : WebUtils.getSavedRequest(request).getRequestUrl();
        return redirect(url);
    }
    // 切换语言
    @GetMapping("changeLanguage")
    public String changeLanguage(String lang, HttpSession session, HttpServletRequest request) {
        String referer = request.getHeader("referer");
        if ("zh".equals(lang)) {
            session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.SIMPLIFIED_CHINESE);
        } else if ("en".equals(lang)) {
            session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.US);
        }
        return StringUtils.isEmpty(referer) ? redirect("/") : redirect(referer);
    }
}
