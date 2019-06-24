> Instructions: Please mark in a conspicuous place `powered by monitor`
[中文 README](README.md)
## Document

[Document](https://github.com/Liuguozhu/monitor/#/)

The documentation is written using the open source tool [docsify](https://docsify.js.org/#/quickstart)

## Technology

- Spring-Boot
- Shiro
- MyBatis-Plus
- Bootstrap
- MySQL
- Freemarker
- Redis
- ElasticSearch

## Feature

- You can monitor the running of mysql, redis, mongodb, mq and other services. You can periodically ping a custom ip or domain name, and send an http request to the specified service for monitoring. If an exception occurs, you can send SMS, email, or corporate WeChat. Give the designated person.

## Getting Started

[Getting Started Document](https://github.com/Liuguozhu/monitor/#/getting-started)

**Special thanks to github user [@tomoya92](https://github.com/tomoya92) for helping to develop dockerfile**

## Manual package

```bash
mvn clean assembly:assembly
```

After the package is complete, a `monitor.tar.gz` file will be generated in the target directory under the project root directory, extract it and run `sh start.sh` to start the service.

## Feedback

- [issues](https://github.com/Liuguozhu/monitor/issues)

*Please clearly describe the problem recurring steps when asking questions*

## Contribution

Welcome everyone to submit issues and pr

## Donation

![image](https://coding-net-production-pp-ci.codehub.cn/587aa702-bb99-4587-8075-2c2f475643d5.jpeg)

**If you feel that this project is helpful to you, welcome to donate!**


