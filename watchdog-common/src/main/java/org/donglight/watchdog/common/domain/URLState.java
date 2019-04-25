package org.donglight.watchdog.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * URLState 记录url被访问的详细信息
 * url访问详情，1个 {@link URLInfo }对应 n 个 {@link URLState }
 *
 * @author donglight
 * @date 2019/4/16
 * @since 1.0.0
 */
@Data
public final class URLState implements Comparable<URLState>, Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * detail id
     */
    private final long id;
    /**
     * 访问用户的fromIp地址
     */
    private final String fromIp;

    /**
     * 访问用户的主机名
     */
    private final String fromHost;

    /**
     * 此次起始时间戳
     */
    private final long start;


    /**
     * 此次访问消耗的时间戳
     */
    private final long duration;

    /**
     * 所属url的id
     */
    private final long urlId;

    /**
     * 所属url
     */
    private final String url;

    /**
     * 当前并发量
     */
    private final int currentConcurrency;

    public URLState() {
        id = 0;
        start = 0;
        duration = 0;
        urlId = 0;
        currentConcurrency = 0;
        fromIp = "";
        fromHost = "";
        url = "";
    }

    private URLState(URLState.Builder builder) {
        this.id = builder.id;
        this.fromIp = builder.fromIp != null ? builder.fromIp : "";
        this.fromHost = builder.fromHost != null ? builder.fromHost : "";
        this.start = builder.start != null ? builder.start : 0L;
        this.duration = builder.duration != null ? builder.duration : 0L;
        this.urlId = builder.urlId != null ? builder.urlId : 0L;
        this.url = builder.url != null ? builder.url : "";
        this.currentConcurrency = builder.currentConcurrency != null ? builder.currentConcurrency : 0;
    }

    public URLState.Builder toBuilder() {
        return new URLState.Builder(this);
    }

    public static URLState.Builder builder() {
        return new URLState.Builder();
    }

    public static final class Builder {

        private Long id;
        private String fromIp;
        private String fromHost;
        private Long start;
        private Long duration;
        private Long urlId;
        public String url;
        public Integer currentConcurrency;

        public Builder(URLState source) {
            this.id = source.id;
            this.fromIp = source.fromIp;
            this.fromHost = source.fromHost;
            this.duration = source.duration;
            this.start = source.start;
            this.urlId = source.urlId;
            this.url = source.url;
        }

        public Builder() {

        }

        public URLState.Builder clear() {
            id = null;
            fromIp = null;
            fromHost = null;
            start = null;
            duration = null;
            urlId = null;
            url = null;
            currentConcurrency = 0;
            return this;
        }

        /**
         * @see URLState#id
         */
        public URLState.Builder id(long id) {
            this.id = id;
            return this;
        }

        /**
         * @see URLState#fromIp
         */
        public URLState.Builder fromIp(String fromIp) {
            this.fromIp = fromIp;
            return this;
        }

        /**
         * @see URLState#fromHost
         */
        public URLState.Builder fromHost(String fromHost) {
            this.fromHost = fromHost;
            return this;
        }

        /**
         * @see URLState#start
         */
        public URLState.Builder start(long start) {
            this.start = start;
            return this;
        }

        /**
         * @see URLState#duration
         */
        public URLState.Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        /**
         * @see URLState#urlId
         */
        public URLState.Builder urlId(long urlId) {
            this.urlId = urlId;
            return this;
        }

        public URLState.Builder url(String url) {
            this.url = url;
            return this;
        }
        public URLState.Builder currentConcurrency(int currentConcurrency) {
            this.currentConcurrency = currentConcurrency;
            return this;
        }

        public URLState build() {
            return new URLState(this);
        }
    }


    @Override
    public int compareTo(URLState o) {
        if (this == o) {
            return 0;
        }
        if (this.id != o.id) {
            return this.id > o.id ? 1 : -1;
        }
        if (!this.fromIp.equals(o.fromIp)) {
            return this.fromIp.compareTo(o.fromIp);
        }
        if (!this.fromHost.equals(o.fromHost)) {
            return this.fromHost.compareTo(o.fromHost);
        }
        if (this.start != o.start) {
            return this.start > o.start ? 1 : -1;
        }
        if (this.duration != o.duration) {
            return this.duration > o.duration ? 1 : -1;
        }
        if (this.urlId != o.urlId) {
            return this.urlId > o.urlId ? 1 : -1;
        }
        if (!this.url.equals(o.url)) {
            return this.url.compareTo(o.url);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof URLState)) {
            return false;
        } else {
            URLState that = (URLState) o;
            return (this.id == that.id)
                    && (this.fromIp.equals(that.fromIp)
                    && (this.fromHost.equals(that.fromHost))
                    && (this.start == that.start)
                    && (this.duration == that.duration)
                    && (this.currentConcurrency == that.currentConcurrency)
                    && (this.urlId == that.urlId)
                    && (this.url.equals(that.url)));
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + fromIp.hashCode();
        result = 31 * result + fromHost.hashCode();
        result = 31 * result + (int) (start ^ (start >>> 32));
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (int) (urlId ^ (urlId >>> 32));
        result = 31 * result + url.hashCode();
        result = 31 * result + currentConcurrency;
        return result;
    }
}
