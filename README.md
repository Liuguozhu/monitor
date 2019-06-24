## 特色

可以监控mysql，redis，mongodb，mq等服务的运行，可以定时ping自定义的ip或域名，发送http请求到指定服务进行监控是否正常；如发生异常，可通过发送短信、邮件、企业微信等方式给指定的人员。

> 使用说明：请在醒目的地方标明 powered by monitor

[English README](README.en_US.md)

当前版本：1.0

- 后台地址：[http://192.168.1.66:/adminlogin](https://demo.yiiu.co/adminlogin) 用户名: test 密码: 123456

如果发现了bug或者有好的建议，欢迎提issue，当然更欢迎 pr。

## 鸣谢

[pybbs](https://github.com/tomoya92/pybbs)

[Spring-Boot项目集成Flyway和MybatisPlus](https://tomoya92.github.io/2019/01/03/spring-boot-flyway-mybatis-plus/)

## 技术栈

- Spring-Boot
- Shiro
- MyBatis-Plus
- Bootstrap
- MySQL
- Freemarker
- Redis
- ElasticSearch
- WebSocket
- I18N

## 运行

参考文档吧，更详细 [传送门](https://github.com/Liuguozhu/monitor)

## 手动打包

```bash
mvn clean assembly:assembly
```
打包完成后，会在项目根目录下的target目录里生成一个`monitor.tar.gz`文件，解压运行 `sh start.sh` 即可启动服务。

## 反馈
欢迎大家提 issues 及 pr
- [issues](https://github.com/Liuguozhu/monitor/issues)

*提问题的时候请将问题重现步骤描述清楚*

## 贡献

- 感谢 [@tomoya92](https://github.com/tomoya92) 大佬帮忙提供的技术支持

## 所有版本

|               | master                                            |
| :-----------  | :------------------------------------------       |
| 开源地址       | [传送门](https://github.com/Liuguozhu/monitor)    |
| 开发框架       | Spring-Boot, Mybatis-Plus                         |
| 数据库         | MySQL                                             |
| 前台           | &radic;                                           |
| 后台           | &radic;                                           |
| 编辑器         | [IntelliJ IDEA](https://www.jetbrains.com/idea/)  |
| 权限           | RBAC                                              |
| 搜索           | Elasticsearch                                     |


## 捐赠

![image](https://coding-net-production-pp-ci.codehub.cn/587aa702-bb99-4587-8075-2c2f475643d5.jpeg)

**如果觉得这个项目对你有帮助，欢迎捐赠！**



