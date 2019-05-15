package org.donglight.watchdog.common.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 记录当前进程的线程信息
 *
 * @author donglight
 * @date 2019/5/3
 * @since 1.0.0
 */
@Setter
@Getter
public class ThreadsInfo implements Serializable {

    private static final long serialVersionUID = 42L;

    private int totalStartedThreadCount;

    private int activeThreadCount;

    private int daemonThreadCount;

    private int peakThreadCount;

    List<WdThreadInfo> wdThreadInfos = new ArrayList<>();
}
