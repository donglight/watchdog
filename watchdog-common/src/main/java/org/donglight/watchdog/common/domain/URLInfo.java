package org.donglight.watchdog.common.domain;

import lombok.Getter;

import java.io.Serializable;


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
    private final long requestTimes;

    /**
     * 该Url的最大并发数
     */
    private final int maxConcurrency;

    private URLInfo(URLInfo.Builder builder) {
        this.id = builder.id;
        this.url = builder.url;
        this.requestTimes = builder.requestTimes;
        this.maxConcurrency = builder.maxConcurrency;
    }

    public URLInfo.Builder toBuilder(){
        return new Builder(this);
    }

    public URLInfo.Builder builder(){
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String url;
        private Long requestTimes;
        private Integer maxConcurrency;

        Builder (URLInfo source) {
            this.id = source.id;
            this.url = source.url;
            this.requestTimes = source.requestTimes;
            this.maxConcurrency = source.maxConcurrency;
        }

        Builder() {

        }

        public Builder clear() {
            id = null;
            url = null;
            requestTimes = null;
            maxConcurrency = null;
            return this;
        }

        /** @see URLInfo#id */
        public Builder id(long id) {
            this.id = id;
            return this;
        }
        /** @see URLInfo#url */
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        /** @see URLInfo#requestTimes */
        public Builder requestTimes(long requestTimes) {
            this.requestTimes = requestTimes;
            return this;
        }

        public Builder maxConcurrency(long maxConcurrency) {
            this.requestTimes = maxConcurrency;
            return this;
        }

        public URLInfo build() {
            return new URLInfo(this);
        }
    }


    @Override
    public int compareTo(URLInfo o) {
        if(this == o){
            return 0;
        }
        if(this.id != o.id){
            return this.id > o.id ? 1 : -1;
        }
        if(!this.url.equals(o.url)){
            return this.url.compareTo(o.url);
        }
        if(this.requestTimes != o.requestTimes){
            return this.requestTimes > o.requestTimes ? 1 : -1;
        }
        if(this.maxConcurrency != o.maxConcurrency){
            return this.maxConcurrency > o.maxConcurrency ? 1 : -1;
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
            URLInfo that = (URLInfo)o;
            return (this.id == that.id)
                    && (this.url.equals(that.url)
                    && (this.requestTimes == that.requestTimes)
                    && (this.maxConcurrency == that.maxConcurrency));
        }
    }
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (url == null ? 0 : url.hashCode());
        result = 31 * result + (int) (requestTimes ^ (requestTimes >>> 32));
        result = 31 * result + maxConcurrency;
        return result;
    }
}
