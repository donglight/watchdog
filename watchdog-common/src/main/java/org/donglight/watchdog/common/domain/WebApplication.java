package org.donglight.watchdog.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * Web应用类
 *
 * @author donglight
 * @date 2019/4/17
 * @since 1.0.0
 */
@Data
public final class WebApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;

    private String name;

    private boolean health = true;

    private boolean alive = true;

    private String clientIP = "";

    private String clientPort = "";

    private String token;

    private List<URLInfo> urlInfoList;

    /**
     * 应用状态的提示信息
     */
    private String message = "";

    /**
     * 上一次更新时间
     */
    private long lastUpdateTimestamp;

    public WebApplication(String appId, String name, String clientIP, String clientPort, String token, List<URLInfo> urlInfoList) {
        this.appId = appId;
        this.name = name;
        this.clientIP = clientIP;
        this.clientPort = clientPort;
        this.token = token;
        this.urlInfoList = urlInfoList;
    }

    public boolean checkHealth() {
        return true;
    }

    public boolean checkAlive() {
        return true;
    }

    public void addUrl(URLInfo urlInfo) {
        this.urlInfoList.add(urlInfo);
    }

    public void removeUrl(URLInfo urlInfo) {
        this.urlInfoList.remove(urlInfo);
    }

}
