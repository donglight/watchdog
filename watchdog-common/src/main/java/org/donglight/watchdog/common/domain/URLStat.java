package org.donglight.watchdog.common.domain;

import lombok.Getter;

import java.io.Serializable;

/**
 * URLStat 记录url被访问的详细信息
 * url访问详情，1个 {@link URLInfo }对应 n 个 {@link URLStat }
 *
 * @author donglight
 * @date 2019/4/16
 * @since 1.0.0
 */
@Getter
public final class URLStat implements Comparable<URLStat>, Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * detail id
     */
    private final long id;
    /**
     * 访问用户的IP地址
     */
    private final String ip;

    /**
     * 访问用户的主机名
     */
    private final String host;

    /**
     * 此次起始时间戳
     *
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

    private URLStat(URLStat.Builder builder) {
        this.id = builder.id;
        this.ip = builder.ip;
        this.host = builder.host;
        this.start = builder.start;
        this.duration = builder.duration;
        this.urlId = builder.urlId;
    }

    public URLStat.Builder toBuilder(){
        return new URLStat.Builder(this);
    }

    public URLStat.Builder builder(){
        return new URLStat.Builder();
    }

    public static final class Builder {

        private Long id;
        private String ip;
        private String host;
        private Long start;
        private Long duration;
        private Long urlId;

        Builder (URLStat source) {
            this.id = source.id;
            this.ip = source.ip;
            this.host = source.host;
            this.duration = source.duration;
            this.start = source.start;
            this.urlId = source.urlId;
        }

        Builder() {

        }

        public URLStat.Builder clear() {
            id = null;
            ip = null;
            host = null;
            start = null;
            duration = null;
            urlId = null;
            return this;
        }

        /** @see URLStat#id */
        public URLStat.Builder id(long id) {
            this.id = id;
            return this;
        }

        /** @see URLStat#ip */
        public URLStat.Builder ip(String ip) {
            this.ip = ip;
            return this;
        }

        /** @see URLStat#host */
        public URLStat.Builder host(String host) {
            this.host = host;
            return this;
        }

        /** @see URLStat#start */
        public URLStat.Builder start(long start) {
            this.start = start;
            return this;
        }

        /** @see URLStat#duration */
        public URLStat.Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        /** @see URLStat#urlId */
        public URLStat.Builder urlId(long urlId) {
            this.urlId = urlId;
            return this;
        }

        public URLStat build() {
            return new URLStat(this);
        }
    }


    @Override
    public int compareTo(URLStat o) {
        if(this == o){
            return 0;
        }
        if(this.id != o.id){
            return this.id > o.id ? 1 : -1;
        }
        if(!this.ip.equals(o.ip)){
            return this.ip.compareTo(o.ip);
        }
        if(!this.host.equals(o.host)){
            return this.host.compareTo(o.host);
        }
        if(this.start != o.start){
            return this.start > o.start ? 1 : -1;
        }
        if(this.duration != o.duration){
            return this.duration > o.duration ? 1 : -1;
        }
        if(this.urlId != o.urlId){
            return this.urlId > o.urlId ? 1 : -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof URLStat)) {
            return false;
        } else {
            URLStat that = (URLStat)o;
            return (this.id == that.id)
                    && (this.ip.equals(that.ip)
                    && (this.host.equals(that.host))
                    && (this.start == that.start)
                    && (this.duration == that.duration)
                    && (this.urlId == that.urlId));
        }
    }
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (ip == null ? 0 : ip.hashCode());
        result = 31 * result + (host == null ? 0 : host.hashCode());
        result = 31 * result + (int) (start ^ (start >>> 32));
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (int) (urlId ^ (urlId >>> 32));
        return result;
    }
}
