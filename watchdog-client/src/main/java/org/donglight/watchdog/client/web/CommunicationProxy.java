package org.donglight.watchdog.client.web;

import org.donglight.watchdog.common.domain.URLInfo;
import org.donglight.watchdog.common.domain.URLState;

import java.util.List;
import java.util.Map;

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
     * 远程注册应用
     *
     * @return true if success
     */
    boolean remoteRegister();

    /**
     * 把当前应用暴露的url接口信息推送到远程监控服务器
     *
     * @param urlInfos url列表
     */
    void pushUrlsToWatchServer(List<URLInfo> urlInfos);

    /**
     * 记录url访问状态
     *
     * @param urlState url状态信息
     */
    void recordUrlState(URLState urlState);

    /**
     * 添加单条url信息
     *
     * @param urlInfo url信息
     */
    void pushOneUrl(URLInfo urlInfo);

    /**
     * 添加单条url信息
     * @return Map<String, URLInfo> URL list
     */
    Map<String, URLInfo> getWatchingURLInfos();

    /**
     * 是否过滤URL
     * @param url url
     */
    boolean isFilterExcludeUrl(String url);
}
