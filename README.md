## watchdog项目说明

### 项目介绍
```
功能点：
    对Java项目的运维，有各项的监控指标。
技术点：
    项目整体核心技术为springboot+vue，都是开源框架。vue项目不在本工程下，watchdog-frontend-vue是前端工程
	核心框架：springboot 2.1.4.RELEASE Vue3.0
	数据库持久化方面还没有完善，大多数用不了
```
### 项目目录结构说明

```
├─watchdog----------------------------父项目，公共依赖
│  │
│  ├─watchdog-client--------------------------Java客户端需要导入的项目代码，以maven或jar的形式导入项目中配置好即可使用监控功能
│  │
│  ├─watchdog-client-test-----------------------测试监控服务的springboot应用，并使用了watchdog-spring-boot-starter自动配置
│  │
│  ├─watchdog-common-------------------------公共依赖(JavaBean util)
│  │
|  ├─watchdog-frontend-vue-------------------------前端vue项目
│  │
│  ├─watchdog-server--------------------------监控服务端，提供RMI服务和API
│  │
│  ├─watchdog-springboot-stater--------------------------springboot starter
```

### 配置项
使用watchdog-client客户端的配置项可以查看watchdog-client-test测试客户端应用下的配置文件

### 效果图
- 堆内存监控
![堆内存](https://github.com/donglight/watchdog/wiki/1.png)
- 线程监控
![线程监控](https://github.com/donglight/watchdog/wiki/2.png)
- URL监控
![URL监控](https://github.com/donglight/watchdog/wiki/3.png)
