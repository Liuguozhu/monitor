package com.coder.monitor.controller.admin;

import com.coder.monitor.common.Result;
import com.coder.monitor.service.SystemConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/system")
public class SystemConfigAdminController extends BaseController {

  @Autowired
  private SystemConfigService systemConfigService;

  @RequiresPermissions("system:edit")
  @GetMapping("/edit")
  public String edit(Model model) {
    model.addAttribute("systems", systemConfigService.selectAll());
    return "admin/system/edit";
  }

  @RequiresPermissions("system:edit")
  @PostMapping("/edit")
  @ResponseBody
  public Result edit(@RequestBody List<Map<String, String>> list) {
    systemConfigService.update(list);
    return success();
  }
}
