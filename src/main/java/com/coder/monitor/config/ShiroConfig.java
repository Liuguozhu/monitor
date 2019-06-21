package com.coder.monitor.config;

import com.coder.monitor.config.realm.MyCredentialsMatcher;
import com.coder.monitor.config.realm.MyShiroRealm;
import com.coder.monitor.service.SystemConfigService;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    private Logger log = LoggerFactory.getLogger(ShiroConfig.class);

    @Autowired
    private MyShiroRealm myShiroRealm;
    @Autowired
    private SystemConfigService systemConfigService;

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        log.info("开始配置shiroFilter...");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //拦截器.
        Map<String, String> map = new HashMap<>();
        // 配置不会被拦截的链接 顺序判断  相关静态资源
        map.put("/static/**", "anon");

        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        map.put("/admin/logout", "logout");

        //<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;

        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        map.put("/admin/**", "authc");
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/adminlogin");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/admin/index");

        //未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/error");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);

//    Map<String, Filter> filters = new HashMap<>();
//    filters.put("anon", new AnonymousFilter());
//    filters.put("authc", new FormAuthenticationFilter());
//    filters.put("logout", new LogoutFilter());
//    shiroFilterFactoryBean.setFilters(filters);

        return shiroFilterFactoryBean;
    }

    // 配置加密方式
    // 配置了一下，这货就是验证不过，，改成手动验证算了，以后换加密方式也方便
    @Bean
    public MyCredentialsMatcher myCredentialsMatcher() {
        return new MyCredentialsMatcher();
    }

    // 安全管理器配置
    @Bean
    public SecurityManager securityManager() {
        log.info("注入Shiro的Web过滤器-->securityManager", ShiroFilterFactoryBean.class);
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        myShiroRealm.setCredentialsMatcher(myCredentialsMatcher());
        //设置Realm，用于获取认证凭证
        securityManager.setRealm(myShiroRealm);
        //注入Cookie(记住我)管理器(remenberMeManager)
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    //加入注解的使用，不加入这个注解不生效
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    // 配置记住我功能
    @Bean
    @DependsOn("mybatisPlusConfig")
    public SimpleCookie rememberMeCookie() {
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //如果httyOnly设置为true，则客户端不会暴露给客户端脚本代码，使用HttpOnly cookie有助于减少某些类型的跨站点脚本攻击；
        simpleCookie.setHttpOnly(true);
        // 记住我cookie生效时间 单位秒
        int adminRememberMeMaxAge = Integer.parseInt(systemConfigService.selectAllConfig().get("admin_remember_me_max_age").toString());
        simpleCookie.setMaxAge(adminRememberMeMaxAge * 24 * 60 * 60);
        return simpleCookie;
    }

    @Bean
    @DependsOn("mybatisPlusConfig")
    public CookieRememberMeManager rememberMeManager() {
        log.info("注入Shiro的记住我(CookieRememberMeManager)管理器-->rememberMeManager", CookieRememberMeManager.class);
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberme cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度（128 256 512 位），通过以下代码可以获取
        //KeyGenerator keygen = KeyGenerator.getInstance("AES");
        //SecretKey deskey = keygen.generateKey();
        //System.out.println(Base64.encodeToString(deskey.getEncoded()));
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.encode("coder best!".getBytes()));
//    cookieRememberMeManager.setCipherKey(Base64.decode("wGiHplamyXlVB11UXWol8g=="));
        return cookieRememberMeManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }

    public static void main(String[] args) {
        System.out.println("wGiHplamyXlVB11UXWol8g==".length());
        System.out.println(Base64.decode("wGiHplamyXlVB11UXWol8g==").length);
        System.out.println(Base64.encode("coder is the best!".getBytes()).length);
        System.out.println(Base64.encode("coder best!".getBytes()).length);
    }
}
