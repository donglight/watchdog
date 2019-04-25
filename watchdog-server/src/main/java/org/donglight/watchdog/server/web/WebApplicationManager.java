package org.donglight.watchdog.server.web;


import org.donglight.watchdog.common.domain.WebApplication;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 〈一句话功能简述〉<br>
 * Web状态管理接口
 * 管理Web应用的所有状态
 *
 * @author donglight
 * @date 2019/4/17
 * @since 1.0.0
 */
public interface WebApplicationManager {


    /**
     * 注册应用
     * @param fromClient 客户端发送过来的信息
     * @return Token
     */
    Optional<WebApplication> registerApplication(Map<String,String> fromClient);

    /**
     *  当应用长时间没有响应后，剔除应用
     * @param appId 应用Id
     * @return <tt>true</tt> 如果剔除应用成功
     */
    boolean unregisterApplication(Long appId);

    /**
     * 每隔一段时间应用回来续约
     * @param application 应用
     * @return <tt>true</tt> 如果应用续约成功
     */
    boolean renewApplication(WebApplication application);

    /**
     * 获得被监控的应用数
     * @return count
     */
    int getApplicationCount();

    /**
     * 获得所有应用列表
     * @return applications
     */
    List<WebApplication> getAllApplications();

    /**
     * 获得指定Id
     * @param appId 应用ID
     * @return applications
     */
    WebApplication getApplicationById(String appId);

}
