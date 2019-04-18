package org.donglight.watchdog.common.domain;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;


/**
 * URL的监控信息
 *
 * @author donglight
 * @date 2019/4/16
 * @since 1.0.0
 */
@Getter
public final class URLInfo implements Comparable<URLInfo>, Serializable {

    private static final long serialVersionUID = 1L;

    /** 唯一标识此Url的id */
    private final long id;

    /** 记录该url的路径 (eg: /user/1) */
    private final String url;

    /** 该url被访问或请求的次数 */
    private long requestTimes;

    /** 该Url的最大并发数 */
    private int maxConcurrency;

    /** 失败次数 */
    private int failTimes;

    /** 成功率 */
    private double successRate;

    /** 请求的HttpMethod */
    private final String httpMethod;

    /** URL列表 */
    private List<URLStat> urlStatList;

    /** 请求方法常量 */
    public static final String HTTP_GET = "Get";
    public static final String HTTP_POST = "Post";
    public static final String HTTP_PUT = "Put";
    public static final String HTTP_DELETE = "Delete";


    public void setRequestTimes(long requestTimes) {
        this.requestTimes = requestTimes;
    }

    public void setMaxConcurrency(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
    }

    public void setFailTimes(int failTimes) {
        this.failTimes = failTimes;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public void setUrlStatList(List<URLStat> urlStatList){
        this.urlStatList = urlStatList;
    }


    private URLInfo(URLInfo.Builder builder) {
        this.id = builder.id;
        this.url = builder.url;
        this.requestTimes = builder.requestTimes;
        this.maxConcurrency = builder.maxConcurrency;
        this.failTimes = builder.failTimes;
        this.successRate = builder.successRate;
        this.httpMethod = builder.httpMethod;
        this.urlStatList = builder.urlStatList;
    }

    public URLInfo.Builder toBuilder() {
        return new Builder(this);
    }

    public URLInfo.Builder builder() {
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
        private List<URLStat> urlStatList;

        Builder(URLInfo source) {
            this.id = source.id;
            this.url = source.url;
            this.requestTimes = source.requestTimes;
            this.maxConcurrency = source.maxConcurrency;
            this.failTimes = source.failTimes;
            this.successRate = source.successRate;
            this.httpMethod = source.httpMethod;
        }

        Builder() {

        }

        public Builder clear() {
            id = null;
            url = null;
            requestTimes = null;
            maxConcurrency = null;
            failTimes = null;
            successRate = null;
            httpMethod = null;
            urlStatList = null;
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
        public Builder urlStatList(List<URLStat> urlStatList) {
            this.urlStatList = urlStatList;
            return this;
        }

        public URLInfo build() {
            return new URLInfo(this);
        }
    }


    @Override
    public int compareTo(URLInfo o) {
        if (this == o) {
            return 0;
        }
        if (this.id != o.id) {
            return this.id > o.id ? 1 : -1;
        }
        if (!this.url.equals(o.url)) {
            return this.url.compareTo(o.url);
        }
        if (this.requestTimes != o.requestTimes) {
            return this.requestTimes > o.requestTimes ? 1 : -1;
        }
        if (this.failTimes != o.failTimes) {
            return this.failTimes > o.failTimes ? 1 : -1;
        }
        if (this.successRate != o.successRate) {
            return this.successRate > o.successRate ? 1 : -1;
        }
        if (!this.httpMethod.equalsIgnoreCase(o.httpMethod)) {
            return this.httpMethod.compareTo(o.httpMethod);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof URLInfo)) {
            return false;
        } else {
            URLInfo that = (URLInfo) o;
            return (this.id == that.id)
                    && (this.url.equals(that.url)
                    && (this.requestTimes == that.requestTimes)
                    && (this.maxConcurrency == that.maxConcurrency))
                    && (this.failTimes == that.failTimes)
                    && (this.successRate == that.successRate)
                    && (this.httpMethod.equalsIgnoreCase(that.httpMethod));
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (url == null ? 0 : url.hashCode());
        result = 31 * result + (int) (requestTimes ^ (requestTimes >>> 32));
        result = 31 * result + maxConcurrency;
        result = 31 * result + failTimes;
        long sr = Double.doubleToLongBits(successRate);
        result = 31 * result + (int) (sr ^ (sr >>> 32));
        result = 31 * result + (httpMethod == null ? 0 : httpMethod.hashCode());
        result = 31 * result + (urlStatList == null ? 0 : urlStatList.hashCode());
        return result;
    }
}
