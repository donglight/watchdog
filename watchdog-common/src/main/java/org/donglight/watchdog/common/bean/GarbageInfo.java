package org.donglight.watchdog.common.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 垃圾回收器信息
 *
 * @author donglight
 * @date 2019/5/4
 * @since 1.0.0
 */
@Setter
@Getter
public class GarbageInfo implements Serializable {

    private static final long serialVersionUID = 42L;

    private String name;

    private long collectionCount;

    private long collectionTime;

    private String[] memoryPoolNames;

}
