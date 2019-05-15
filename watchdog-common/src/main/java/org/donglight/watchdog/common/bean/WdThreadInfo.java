package org.donglight.watchdog.common.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * watch dog ThreadInfo
 *
 * @author donglight
 * @date 2019/5/6
 * @since 1.0.0
 */
@Getter
@Setter
public class WdThreadInfo implements Serializable {

    private static final long serialVersionUID = 42L;

    private String threadName;
    private long threadId;
    private long blockedTime;
    private long blockedCount;
    private long waitedTime;
    private long waitedCount;
    private Thread.State threadState;
    private String stackTrace;
    private long totalCpuTime;

}
