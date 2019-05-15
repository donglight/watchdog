package org.donglight.watchdog.common.bean;

import lombok.Getter;
import lombok.Setter;
import org.donglight.watchdog.common.util.CommonUtil;

import java.io.Serializable;

/**
 * 堆，非堆，内存池内存信息,比如 old gen, eden, survivor, code cache, metaspace...
 *
 * @author donglight
 * @date 2019/5/3
 * @since 1.0.0
 */
@Getter
@Setter
public class MemoryUsageInfo implements Serializable {

    private static final long serialVersionUID = 42L;

    private String name;

    private double init;

    private double max;

    private double committed;

    private double used;

    private double usedRatio;

    public MemoryUsageInfo(String name, double init, double max, double committed, double used) {
        this.name = name;
        this.init = init;
        this.max = max;
        this.committed = committed;
        this.used = used;
        this.usedRatio = CommonUtil.formatDouble(used * 100 / committed);
    }

}
