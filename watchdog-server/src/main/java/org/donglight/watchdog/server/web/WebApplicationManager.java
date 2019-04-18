package org.donglight.watchdog.server.web;


import org.donglight.watchdog.common.domain.WebApplication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
     * @param application 需要注册的应用
     * @throws Exception 注册过程中可能发生异常
     * @return <tt>true</tt> 如果应用注册成功
     */
    boolean registerApplication(WebApplication application) throws Exception;

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

}
