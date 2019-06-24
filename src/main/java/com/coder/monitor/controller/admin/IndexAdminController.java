package com.coder.monitor.controller.admin;

import com.sun.management.OperatingSystemMXBean;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.management.ManagementFactory;

/**
 * Created by tomoya.
 * Copyright (c) 2018, All Rights Reserved.
 * https://yiiu.co
 */
@Controller
@RequestMapping("/admin")
public class IndexAdminController  {

    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    @RequiresAuthentication
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        // 获取操作系统的名字
        model.addAttribute("os_name", System.getProperty("os.name"));

        // 内存
        int kb = 1024;
        // 总的物理内存
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb;
        //已使用的物理内存
        long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / kb;
        // 获取系统cpu负载
        double systemCpuLoad = osmxb.getSystemCpuLoad();
        // 获取jvm线程负载
        double processCpuLoad = osmxb.getProcessCpuLoad();

        model.addAttribute("totalMemorySize", totalMemorySize);
        model.addAttribute("usedMemory", usedMemory);
        model.addAttribute("systemCpuLoad", systemCpuLoad);
        model.addAttribute("processCpuLoad", processCpuLoad);

        return "admin/index";
    }

}
