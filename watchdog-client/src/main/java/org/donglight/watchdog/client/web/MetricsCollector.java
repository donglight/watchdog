package org.donglight.watchdog.client.web;

import com.sun.management.OperatingSystemMXBean;
import org.donglight.watchdog.common.bean.*;
import org.donglight.watchdog.common.util.CommonUtil;

import java.lang.management.*;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * MetricsCollector 收集metrics {@link Metrics} 指标信息
 *
 * @author donglight
 * @date 2019/5/3
 * @see Metrics
 * @since 1.0.0
 */
public class MetricsCollector {

    private Metrics metrics;
    private static final int MB = 20;


    private void initMetrics() {
        metrics = new Metrics();
        //初始化不常变的属性
        //java jvm
        Runtime rt = Runtime.getRuntime();
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        Properties props = System.getProperties();
        //jdk的MemoryMXBean,可以获取堆和非堆的内存信息
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        // 堆的内存
        MemoryUsage headMemoryUsage = memory.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memory.getNonHeapMemoryUsage();
        JvmInfo jvmInfo = new JvmInfo();
        jvmInfo.setVmVersion(runtime.getVmVersion());
        jvmInfo.setVmName(runtime.getVmName());
        jvmInfo.setVmVendor(runtime.getVmVendor());
        jvmInfo.setVmSpecName(runtime.getSpecName());
        jvmInfo.setVmSpecVersion(runtime.getSpecVersion());
        jvmInfo.setVmSpecVendor(runtime.getSpecVendor());
        jvmInfo.setVmStartTime(runtime.getStartTime());

        jvmInfo.setPid(Integer.parseInt(runtime.getName().split("@")[0]));
        jvmInfo.setJavaVersion(props.getProperty("java.version"));
        jvmInfo.setJavaVendor(props.getProperty("java.vendor"));
        jvmInfo.setJavaVendorUrl(props.getProperty("java.vendor.url"));
        jvmInfo.setClassPath(runtime.getClassPath());
        jvmInfo.setBootClassPath(runtime.getBootClassPath());
        jvmInfo.setLibraryPath(runtime.getLibraryPath());
        jvmInfo.setJavaHome(props.getProperty("java.home"));
        jvmInfo.setJavaSpecName(props.getProperty("java.specification.name"));
        jvmInfo.setJavaSpecVersion(props.getProperty("java.specification.name"));
        jvmInfo.setJavaSpecVendor(props.getProperty("java.specification.vendor"));
        jvmInfo.setJavaClassVersion(props.getProperty("java.class.version"));
        jvmInfo.setTmpdir(props.getProperty("java.io.tmpdir"));
        jvmInfo.setExtDirs(props.getProperty("java.ext.dirs"));
        jvmInfo.setInputArguments(runtime.getInputArguments());
        jvmInfo.setMaxMemory(checkMemoryAndFormat(rt.maxMemory()));
        jvmInfo.setTotalMemory(CommonUtil.formatDouble(rt.totalMemory() >>> MB));
        jvmInfo.setHeapInit(checkMemoryAndFormat(headMemoryUsage.getInit()));
        jvmInfo.setHeapMax(checkMemoryAndFormat(headMemoryUsage.getMax()));
        jvmInfo.setNonHeapInit(checkMemoryAndFormat(nonHeapMemoryUsage.getInit()));
        jvmInfo.setNonHeapMax(checkMemoryAndFormat(nonHeapMemoryUsage.getMax()));


        // os
        OSInfo osInfo = new OSInfo();
        osInfo.setOsVersion(props.getProperty("os.version"));
        osInfo.setOsName(props.getProperty("os.name"));
        osInfo.setOsArch(props.getProperty("os.arch"));
        osInfo.setUserName(props.getProperty("user.name"));
        osInfo.setUserHome(props.getProperty("user.home"));
        osInfo.setFileSeparator(props.getProperty("file.separator"));
        osInfo.setPathSeparator(props.getProperty("path.separator"));
        osInfo.setLineSeparator(props.getProperty("line.separator"));

        //metrics是一个存储所有指标信息的指标仓库类
        metrics.setJvmInfo(jvmInfo);
        metrics.setOsInfo(osInfo);
        metrics.setThreadInfo(new ThreadsInfo());

        // 刷新metrics，更新经常变化的属性
        refreshMetrics();
    }

    public Metrics refreshMetrics() {
        if (metrics == null) {
            initMetrics();
        }
        refreshOSInfo();
        refreshJvmInfo();
        refreshThreadInfo();
        refreshGarbageCollectorInfo();
        return metrics;
    }


    private void refreshOSInfo() {
        // -------------------填充os信息
        OSInfo osInfo = metrics.getOsInfo();
        osInfo.setUserDir(System.getProperty("user.dir"));
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        osInfo.setOsTotalPhysicalMemorySize(CommonUtil.formatDouble(osmxb.getTotalPhysicalMemorySize() >>> MB));
        osInfo.setOsFreePhysicalMemorySize(CommonUtil.formatDouble(osmxb.getFreePhysicalMemorySize() >>> MB));
        osInfo.setAvailableProcessors(osmxb.getAvailableProcessors());
        osInfo.setSystemCpuLoad(CommonUtil.formatDouble(osmxb.getSystemCpuLoad() * 100));
        osInfo.setTotalSwapSpaceSize(CommonUtil.formatDouble(osmxb.getTotalSwapSpaceSize() >>> MB));
        osInfo.setCommittedVirtualMemorySize(CommonUtil.formatDouble(osmxb.getCommittedVirtualMemorySize() >>> MB));
        osInfo.setFreeSwapSpaceSize(CommonUtil.formatDouble(osmxb.getFreeSwapSpaceSize() >>> MB));
        metrics.getJvmInfo().setCpuUseRatio(CommonUtil.formatDouble(osmxb.getProcessCpuLoad()));
    }

    private void refreshJvmInfo() {

        // -------------------填充jvm内存信息
        JvmInfo jvmInfo = metrics.getJvmInfo();
        Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        jvmInfo.setVmUpTime(runtimeMXBean.getUptime());
        jvmInfo.setFreeMemory(CommonUtil.formatDouble(runtime.freeMemory() >>> MB));
        double totalMemory = jvmInfo.getTotalMemory();
        jvmInfo.setUsedRatio(CommonUtil.formatDouble(totalMemory - jvmInfo.getFreeMemory() * 100 / totalMemory));
        jvmInfo.setJavaCompiler(System.getProperty("java.compiler"));

        //jdk的MemoryMXBean,可以获取堆和非堆的内存信息
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        // 堆的内存
        MemoryUsage headMemory = memory.getHeapMemoryUsage();
        double heapUsed = CommonUtil.formatDouble(headMemory.getUsed() >>> MB);
        double heapCommitted = CommonUtil.formatDouble(headMemory.getCommitted() >>> MB);
        jvmInfo.setHeapUsed(heapUsed);
        jvmInfo.setHeapCommitted(heapCommitted);
        jvmInfo.setHeapMax(checkMemoryAndFormat(headMemory.getMax()));
        jvmInfo.setHeapUsedRatio(CommonUtil.formatDouble(heapUsed * 100 / heapCommitted));
        //非堆的
        MemoryUsage nonHeapMemory = memory.getNonHeapMemoryUsage();
        double nonHeapUsed = CommonUtil.formatDouble(nonHeapMemory.getUsed() >>> MB);
        double nonHeapCommitted = CommonUtil.formatDouble(nonHeapMemory.getCommitted() >>> MB);
        jvmInfo.setNonHeapUsed(nonHeapUsed);
        jvmInfo.setNonHeapCommitted(nonHeapCommitted);
        jvmInfo.setNonHeapMax(checkMemoryAndFormat(nonHeapMemory.getMax()));
        jvmInfo.setNonHeapUsedRatio(CommonUtil.formatDouble(nonHeapUsed * 100 / nonHeapCommitted));

        //MemoryUsageInfo
        List<MemoryUsageInfo> memoryPoolInfos = jvmInfo.getMemoryPoolInfos();
        // jvm具体内存池信息, 如old gen, eden, survivor, metaspace, code cache
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        if (pools != null && !pools.isEmpty()) {
            if (memoryPoolInfos.size() == 0) {
                for (MemoryPoolMXBean pool : pools) {
                    MemoryUsage usage = pool.getUsage();
                    MemoryUsageInfo memoryPoolInfo = new MemoryUsageInfo(pool.getName(),
                            checkMemoryAndFormat(usage.getInit()), checkMemoryAndFormat(usage.getMax()),
                            CommonUtil.formatDouble(usage.getCommitted() >>> MB),
                            CommonUtil.formatDouble(usage.getUsed() >>> MB));
                    memoryPoolInfos.add(memoryPoolInfo);
                }
            } else {
                Iterator<MemoryUsageInfo> infoIterator = memoryPoolInfos.iterator();
                Iterator<MemoryPoolMXBean> poolMXBeanIterator = pools.iterator();
                if (poolMXBeanIterator.hasNext() && infoIterator.hasNext()) {
                    MemoryPoolMXBean pool = poolMXBeanIterator.next();
                    MemoryUsageInfo memoryUsageInfo = infoIterator.next();
                    MemoryUsage usage = pool.getUsage();
                    memoryUsageInfo.setName(pool.getName());
                    memoryUsageInfo.setInit(checkMemoryAndFormat(usage.getInit()));
                    memoryUsageInfo.setMax(checkMemoryAndFormat(usage.getMax()));
                    double poolCommitted = CommonUtil.formatDouble(usage.getCommitted() >>> MB);
                    double poolUsed = CommonUtil.formatDouble(usage.getUsed() >>> MB);
                    memoryUsageInfo.setCommitted(poolCommitted);
                    memoryUsageInfo.setUsed(poolUsed);
                    memoryUsageInfo.setUsedRatio(CommonUtil.formatDouble(poolUsed * 100 / poolCommitted));
                }
            }
        }
        ClassLoadingMXBean classLoad = ManagementFactory.getClassLoadingMXBean();
        jvmInfo.setTotalLoadedClassCount(classLoad.getTotalLoadedClassCount());
        jvmInfo.setLoadedClassCount(classLoad.getTotalLoadedClassCount());
        jvmInfo.setUnloadedClassCount(classLoad.getUnloadedClassCount());
    }

    private void refreshThreadInfo() {
        // -------------------填充线程信息
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        threadMXBean.setThreadContentionMonitoringEnabled(true);
        ThreadsInfo threadsInfo = metrics.getThreadInfo();
        threadsInfo.setDaemonThreadCount(threadMXBean.getDaemonThreadCount());
        threadsInfo.setPeakThreadCount(threadMXBean.getPeakThreadCount());
        threadsInfo.setTotalStartedThreadCount((int) threadMXBean.getTotalStartedThreadCount());
        threadsInfo.setActiveThreadCount(threadMXBean.getThreadCount());

        List<WdThreadInfo> wdThreadInfos = threadsInfo.getWdThreadInfos();
        wdThreadInfos.clear();
        addWdThreadInfo(threadMXBean, threadMXBean.getAllThreadIds(), wdThreadInfos);
    }

    private void addWdThreadInfo(ThreadMXBean threadMXBean, long[] threadIds, List<WdThreadInfo> wdThreadInfos) {
        if(threadIds != null){
            for (long threadId : threadIds) {
                ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId, 20);
                WdThreadInfo wdThreadInfo = new WdThreadInfo();
                wdThreadInfo.setBlockedCount(threadInfo.getBlockedCount());
                wdThreadInfo.setBlockedTime(threadInfo.getBlockedTime());
                StackTraceElement[] stackTraces = threadInfo.getStackTrace();
                StringBuilder stackTrace = new StringBuilder();
                if(stackTraces != null){
                    for (StackTraceElement stackTraceElement : stackTraces) {
                        stackTrace.append(stackTraceElement.toString()).append("<br>");
                    }
                }
                wdThreadInfo.setStackTrace(stackTrace.toString());
                wdThreadInfo.setThreadId(threadInfo.getThreadId());
                wdThreadInfo.setThreadName(threadInfo.getThreadName());
                wdThreadInfo.setThreadState(threadInfo.getThreadState());
                wdThreadInfo.setWaitedCount(threadInfo.getWaitedCount());
                wdThreadInfo.setWaitedTime(threadInfo.getWaitedTime());
                wdThreadInfo.setTotalCpuTime(threadMXBean.getThreadCpuTime(threadId));
                wdThreadInfos.add(wdThreadInfo);
            }
        }
    }

    private void refreshGarbageCollectorInfo() {
        List<GarbageInfo> garbageInfos = metrics.getJvmInfo().getGarbageInfos();
        garbageInfos.clear();
        List<GarbageCollectorMXBean> garbages = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean garbage : garbages) {
            GarbageInfo garbageInfo = new GarbageInfo();
            garbageInfo.setName(garbage.getName());
            garbageInfo.setCollectionCount(garbage.getCollectionCount());
            garbageInfo.setCollectionTime(garbage.getCollectionTime());
            garbageInfo.setMemoryPoolNames(garbage.getMemoryPoolNames());
            garbageInfos.add(garbageInfo);
        }
    }

    /**
     * 检查用户应用是否设置了内存的初始值，最小值或最大值
     *
     * @param memory memory
     * @return double 保留两位小数后的内存
     */
    private double checkMemoryAndFormat(long memory) {
        return memory == -1 ? memory : CommonUtil.formatDouble(memory >>> MB);
    }

    public Metrics getMetrics() {
        return metrics;
    }

}
