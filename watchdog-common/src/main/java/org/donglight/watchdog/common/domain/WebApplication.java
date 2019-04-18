package org.donglight.watchdog.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * Web应用类
 *
 * @author donglight
 * @date 2019/4/17
 * @since 1.0.0
 */
@EqualsAndHashCode
@ToString
public final class WebApplication {

    private final long appId;

    private final String name;

    private boolean health;

    private boolean alive;

    private List<URLInfo> urlInfoList;

    private String message;



    public WebApplication(long appId, String name,List<URLInfo> urlInfoList) {
        this.appId = appId;
        this.name = name;
        this.urlInfoList = urlInfoList;
    }

    public long getAppId() {
        return appId;
    }

    public String getName() {
        return name;
    }

    public boolean checkHealth(){
        return true;
    }

    public boolean checkAlive(){
        return true;
    }


    public boolean isHealth() {
        return health;
    }

    public void setHealth(boolean health) {
        this.health = health;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<URLInfo> getUrlInfoList() {
        return urlInfoList;
    }

    public void setUrlInfoList(List<URLInfo> urlInfoList) {
        this.urlInfoList = urlInfoList;
    }

    public void addUrl(URLInfo urlInfo) {
        this.urlInfoList.add(urlInfo);
    }

    public void removeUrl(URLInfo urlInfo) {
        this.urlInfoList.remove(urlInfo);
    }

}
