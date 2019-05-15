package org.donglight.watchdog.common.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * JvmInfo
 *
 * @author donglight
 * @date 2019/5/2
 * @since 1.0.0
 */
@Getter
@Setter
public class JvmInfo implements Serializable {

    private static final long serialVersionUID = 42L;

    private String vmName;

    private String vmVersion;

    private String vmVendor;

    private String vmSpecName;

    private String vmSpecVendor;

    private String vmSpecVersion;

    private long vmStartTime;

    private long vmUpTime;

    private String javaVersion;

    private String javaVendor;

    private String javaVendorUrl;

    private String javaSpecVersion;

    private String javaSpecName;

    private String javaSpecVendor;

    // Java 类格式版本号
    private String javaClassVersion;

    private String javaHome;

    // java.compiler 要使用的 JIT 编译器的名称
    private String javaCompiler;

    private String classPath;

    private String bootClassPath;

    private String libraryPath;

    private String tmpdir;

    private String extDirs;

    private List<String> inputArguments;

    private double maxMemory;

    private double totalMemory;

    private double freeMemory;

    private double usedRatio;

    private double heapInit;

    private double heapCommitted;

    private double heapUsed;

    private double heapMax;

    private double heapUsedRatio;

    private double nonHeapInit;

    private double nonHeapCommitted;

    private double nonHeapUsed;

    private double nonHeapMax;

    private double nonHeapUsedRatio;

    private List<MemoryUsageInfo> memoryPoolInfos = new ArrayList<>();

    private double cpuUseRatio;

    private long totalLoadedClassCount;

    private long loadedClassCount;

    private long unloadedClassCount;

    private int pid;

    private List<GarbageInfo> garbageInfos = new ArrayList<>();

}
