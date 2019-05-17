## watchdog-client项目说明

### 项目介绍
```
	监控系统的客户端程序，需要被监控的Java项目必须依赖此项目。
	最终以依赖或jar包嵌入到受监控的Java项目中
	
```
### 项目目录配置项

```
原生Servlet项目或非springboot的springmvc项目的配置项请看当前目录下的watchdog.properties文件
servlet项目还可以在Web.xml配置所部署的监控服务端地址，并可以配置rmi和http两种通信方式，优先级比watchdog.properties文件的高
<context-param>
  	<param-name>watchdog.client.serverUrl</param-name>
  	<param-value>rmi://127.0.0.1:1099/watchDogRMIService</param-value>
 </context-param>
 <context-param>
  	<param-name>watchdog.client.proxyType</param-name>
  	<param-value>rmi</param-value>
 </context-param>


springboot项目看当前目录下的application.properties,和watchdog.properties配置项类似，注释在watchdog.properties



