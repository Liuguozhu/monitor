package com.coder.monitor.controller.admin;

import com.coder.monitor.util.captcha.Captcha;
import com.coder.monitor.util.captcha.GifCaptcha;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/common")
public class CommonController {

  // gif 验证码
  @GetMapping("/captcha")
  public void captcha(HttpServletResponse response, HttpSession session) throws IOException {
    Captcha captcha = new GifCaptcha();
    captcha.out(response.getOutputStream());
    String text = captcha.text();
    session.setAttribute("_captcha", text);
  }

}
