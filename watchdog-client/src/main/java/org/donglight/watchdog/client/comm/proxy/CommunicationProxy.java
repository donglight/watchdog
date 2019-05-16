package org.donglight.watchdog.client.comm.proxy;

import org.donglight.watchdog.common.bean.Metrics;
import org.donglight.watchdog.common.bean.URLInfo;
import org.donglight.watchdog.common.bean.URLState;
import org.donglight.watchdog.common.bean.WatchDogClientConfigBean;

import java.util.List;

/**
 * 与监控服务器通信的客户端代理(rmi/http...)
 *
 * @author donglight
 * @date 2019/4/20
 * @since 1.0.0
 */
public interface CommunicationProxy {

    /**
     * 远程注册应用
     * @param configBean 客户端配置信息
     * @return true if success
     */
    boolean remoteRegister(WatchDogClientConfigBean configBean);

    /**
     * 把当前应用暴露的url接口信息推送到远程监控服务器
     *
     * @param urlInfos url列表
     */
    void pushUrls(List<URLInfo> urlInfos);

    /**
     * 记录url访问状态
     *
     * @param urlState url状态信息
     */
    void pushUrlState(URLState urlState);

    /**
     * 添加单条url信息
     *
     * @param urlInfo url信息
     */
    void pushOneUrl(URLInfo urlInfo);

    /**
     * 获得正在被监视的URLInfos
     *
     * @return List<URLInfo>
     */
    List<URLInfo> getWatchingURLInfos();

    /**
     * 是否过滤URL
     *
     * @param url url
     */
    boolean isFilterExcludeUrl(String url);

    /**
     * 应用续约
     *
     * @return boolean
     */
    boolean renew();

    /**
     * 推送metrics信息到监控服务端
     * @param metrics metrics
     * @return boolean
     */
    boolean pushMetrics(Metrics metrics);

    /**
     * Filter不需要过滤的URL
     * @param excludeUrls excludeUrls
     */
    void setFilterExcludeUrl(String[] excludeUrls);
}
