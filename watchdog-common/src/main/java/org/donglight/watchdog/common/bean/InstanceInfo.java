package org.donglight.watchdog.common.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 应用的实例
 *
 * @author donglight
 * @date 2019/4/28
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstanceInfo implements Serializable {

    private static final long serialVersionUID = 42L;


    private String instanceId;
    /**
     * 属于哪个app下的实例
     */
    private String appName;
    private String serverInfo;
    private String ipAddr;

    private int port;

    private String homePageUrl;
    private String hostName;
    @Builder.Default
    private List<URLInfo> urlInfoList = new ArrayList<>();

    @Builder.Default
    private AtomicLong totalAccessCount = new AtomicLong(0);

    /**
     * 上一次更新时间
     */
    @Builder.Default
    private long lastUpdateTimestamp = System.currentTimeMillis();
    @Builder.Default
    private InstanceStatus status = InstanceStatus.UP;

    private LeaseInfo leaseInfo;

    @JsonIgnore
    private Metrics metrics;

    private int refreshMetricsIntervalInSecs;


    public enum InstanceStatus {
        /**
         * // 准备接收流量
         */
        UP,
        /**
         * // 不发送流量-健康检查回调失败
         */
        DOWN,
        /**
         * // 刚开始-要完成初始化-不要
         */
        STARTING,
        // send traffic
        /**
         * // 故意停车
         */
        OUT_OF_SERVICE,
        UNKNOWN;

        public static InstanceStatus toEnum(String s) {
            for (InstanceStatus e : InstanceStatus.values()) {
                if (e.name().equalsIgnoreCase(s)) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }
}
