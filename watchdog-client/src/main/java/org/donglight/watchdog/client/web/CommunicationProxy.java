package org.donglight.watchdog.client.web;

/**
 * 〈一句话功能简述〉<br>
 * 与监控服务器通信的http客户端
 *
 * @author donglight
 * @date 2019/4/20
 * @since 1.0.0
 */
public interface CommunicationProxy {

    /**
     * 远程注册
     * @return
     */
    boolean remoteRegister(String appName);

}
