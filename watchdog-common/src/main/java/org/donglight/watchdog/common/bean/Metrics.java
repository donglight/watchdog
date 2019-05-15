package org.donglight.watchdog.common.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 衡量指标，对指标进行监控
 * 是一个存储所有指标信息的指标仓库类
 * @author donglight
 * @date 2019/5/2
 * @since 1.0.0
 */
@Getter
@Setter
public class Metrics implements Serializable {

    private static final long serialVersionUID = 42L;

    private JvmInfo jvmInfo;

    private OSInfo osInfo;

    private ThreadsInfo threadInfo;

}
