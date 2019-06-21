package com.coder.monitor.controller.admin;

import com.coder.monitor.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

  @Autowired
  private SystemConfigService systemConfigService;

  protected String redirect(String path) {
    return "redirect:" + path;
  }

  // 只针对前台页面的模板路径渲染，后台不变
  protected String render(String path) {
    return String.format("theme/%s/%s", systemConfigService.selectAllConfig().get("theme").toString(), path);
  }

}
