package org.donglight.watchdog.common.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * OSInfo 当前操作系统的一些信息
 *
 * @author donglight
 * @date 2019/5/2
 * @since 1.0.0
 */
@Getter
@Setter
public class OSInfo implements Serializable {

    private static final long serialVersionUID = 42L;

    private String osName;

    private String osVersion;

    private String osArch;

    private String userName;

    private String userHome;

    private String userDir;

    private String fileSeparator;

    private String pathSeparator;

    private String lineSeparator;

    private double osTotalPhysicalMemorySize;

    private double osFreePhysicalMemorySize;

    private double committedVirtualMemorySize;

    private double freeSwapSpaceSize;

    private double totalSwapSpaceSize;

    private int availableProcessors;

    private double systemCpuLoad;
}
