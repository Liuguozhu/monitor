package com.coder.monitor.config;

//import com.coder.monitor.directive.*;
import com.coder.monitor.common.BaseModel;
import freemarker.template.TemplateModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.annotation.PostConstruct;

@Configuration
public class FreemarkerConfig {

  private Logger log = LoggerFactory.getLogger(FreeMarkerConfig.class);

  @Autowired
  private freemarker.template.Configuration configuration;
//  @Autowired
//  private TopicListDirective topicListDirective;
  @Autowired
  private BaseModel baseModel;
  @Autowired
  private ShiroTag shiroTag;

  @PostConstruct
  public void setSharedVariable() throws TemplateModelException {
    //注入全局配置到freemarker
    log.info("开始配置freemarker全局变量...");
//    configuration.setSharedVariable("model", baseModel);
    // shiro鉴权
    configuration.setSharedVariable("sec", shiroTag);
    log.info("freemarker全局变量配置完成!");

    log.info("开始配置freemarker自定义标签...");
//    configuration.setSharedVariable("tag_topics", topicListDirective);
//    configuration.setSharedVariable("tag_tags", tagsDirective);

    log.info("freemarker自定义标签配置完成!");
  }

}
