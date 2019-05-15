package org.donglight.watchdog.common.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * URL的监控信息
 *
 * @author donglight
 * @date 2019/4/16
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public final class URLInfo implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * 唯一标识此Url的id
     */
    private final long id;

    /**
     * 记录该url的路径 (eg: /user/1)
     */
    private final String url;

    /**
     * 该url被访问或请求的次数
     */
    @Setter
    private AtomicLong requestTimes;

    /**
     * 该Url的最大并发数
     */
    @Setter
    private AtomicInteger maxConcurrency;

    /**
     * 失败次数
     */
    @Setter
    private AtomicInteger failTimes;

    /**
     * 成功率
     */
    @Setter
    private double successRate;

    /**
     * 请求的HttpMethod
     */
    private final String httpMethod;

    /**
     * 请求耗费的总时间 ms
     */
    @Setter
    private long totalTime;

    /**
     * 请求最大耗时
     */
    @Setter
    private long maxDuration;


    /**
     * URL详情列表
     */
    @Setter
    @JsonIgnore
    private List<URLState> urlStatList = new ArrayList<>();

    /**
     * 请求方法常量
     */
    public static final String HTTP_GET = "Get";
    public static final String HTTP_POST = "Post";
    public static final String HTTP_PUT = "Put";
    public static final String HTTP_DELETE = "Delete";

    public URLInfo() {
        id = 0;
        httpMethod = HTTP_GET;
        url = "";
    }

    private URLInfo(URLInfo.Builder builder) {
        this.id = builder.id;
        this.url = builder.url != null ? builder.url : "";
        this.requestTimes = new AtomicLong(builder.requestTimes != null ? builder.requestTimes : 0L);
        this.maxConcurrency = new AtomicInteger(builder.maxConcurrency != null ? builder.maxConcurrency : 0);
        this.failTimes = new AtomicInteger(builder.failTimes != null ? builder.failTimes : 0);
        this.successRate = builder.successRate != null ? builder.successRate : 0D;
        this.httpMethod = builder.httpMethod;
        this.urlStatList = builder.urlStatList != null ? builder.urlStatList : this.urlStatList;
        this.totalTime = builder.totalTime != null ? builder.totalTime : 0;
        this.maxDuration = builder.maxDuration != null ? builder.maxDuration : 0;
    }

    public URLInfo.Builder toBuilder() {
        return new Builder(this);
    }

    public static URLInfo.Builder builder() {
        return new Builder();
    }

    public static final class Builder {


        private Long id;
        private String url;
        private Long requestTimes;
        private Integer maxConcurrency;
        private Integer failTimes;
        private Double successRate;
        private String httpMethod;
        private List<URLState> urlStatList;
        private Long totalTime;
        private Long maxDuration;

        public Builder(URLInfo source) {
            this.id = source.id;
            this.url = source.url;
            this.requestTimes = source.requestTimes.get();
            this.maxConcurrency = source.maxConcurrency.get();
            this.failTimes = source.failTimes.get();
            this.successRate = source.successRate;
            this.httpMethod = source.httpMethod;
            this.totalTime = source.totalTime;
            this.maxDuration = source.maxDuration;
        }

        public Builder() {

        }

        public Builder clear() {
            id = null;
            url = null;
            requestTimes = null;
            maxConcurrency = null;
            failTimes = null;
            successRate = null;
            httpMethod = null;
            totalTime = null;
            maxDuration = null;
            if (urlStatList != null) {
                urlStatList.clear();
            }
            return this;
        }

        /**
         * @see URLInfo#id
         */
        public Builder id(long id) {
            this.id = id;
            return this;
        }

        /**
         * @see URLInfo#url
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * @see URLInfo#requestTimes
         */
        public Builder requestTimes(long requestTimes) {
            this.requestTimes = requestTimes;
            return this;
        }

        public Builder maxConcurrency(long maxConcurrency) {
            this.requestTimes = maxConcurrency;
            return this;
        }

        public Builder failTimes(int failTimes) {
            this.failTimes = failTimes;
            return this;
        }

        public Builder successRate(double successRate) {
            this.successRate = successRate;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder totalTime(long totalTime) {
            this.totalTime = totalTime;
            return this;
        }

        public Builder urlStatList(List<URLState> urlStatList) {
            this.urlStatList = urlStatList;
            return this;
        }

        public Builder maxDuration(long maxDuration) {
            this.maxDuration = maxDuration;
            return this;
        }

        public URLInfo build() {
            return new URLInfo(this);
        }
    }
}
