package org.donglight.watchdog.client.web.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.donglight.watchdog.client.web.listener.RegistryServletContextListener;
import org.donglight.watchdog.client.comm.proxy.CommunicationProxy;
import org.donglight.watchdog.common.bean.URLInfo;
import org.donglight.watchdog.common.bean.URLState;
import org.donglight.watchdog.common.util.IDUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * AbstractURLFilter
 *
 * @author donglight
 * @date 2019/5/11
 * @since 1.0.0
 */
public abstract class AbstractURLFilter implements Filter {

    protected ExecutorService pool;

    protected String contextPath = "";

    protected CommunicationProxy communicationProxy;

    protected static final String FILTER_EXCLUDE_URL = "excludeUrl";


    protected AbstractURLFilter() {
        pool = new ScheduledThreadPoolExecutor(2,
                new BasicThreadFactory.Builder().namingPattern("watchdog-client-filter-schedule-pool-%d").daemon(true).build());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        if (communicationProxy == null) {
            Object attribute = servletContext.getAttribute(RegistryServletContextListener.WD_COMMUNICATION_PROXY);
            if (attribute instanceof CommunicationProxy) {
                communicationProxy = (CommunicationProxy) attribute;
            }
        }
        contextPath = filterConfig.getServletContext().getContextPath();
        String filterExcludeUrl = filterConfig.getInitParameter(FILTER_EXCLUDE_URL);
        if (StringUtils.isNotEmpty(filterExcludeUrl)) {
            filterExcludeUrl = filterExcludeUrl.trim();
            communicationProxy.setFilterExcludeUrl(filterExcludeUrl.split(","));
        }
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();
        if (communicationProxy.isFilterExcludeUrl(requestURI)) {
            // 不在统计的url范围之内
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //记录request起始时间
        LocalTime start = LocalTime.now();
        List<URLInfo> watchingURLInfos = communicationProxy.getWatchingURLInfos();
        URLInfo urlInfo;
        int position;
        if (StringUtils.isNotEmpty(contextPath)) {
            requestURI = requestURI.replace("/" + contextPath, "");
        }
        if ((position = binaryMatcherPath(watchingURLInfos, requestURI)) != -1) {
            urlInfo = watchingURLInfos.get(position);
            urlInfo.getMaxConcurrency().incrementAndGet();
        } else {
            // URL不存在正在监控的列表中，放行，然后return终止当前方法进行
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 放行
        try {
            // 即将执行filterChain.doFilter()放行，去执行业务逻辑，url的最大并发量加1
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            e.printStackTrace();
            urlInfo.getFailTimes().incrementAndGet();
        }

        //request结束时间
        LocalTime end = LocalTime.now();
        Duration duration = Duration.between(start, end);

        URLState urlState;
        URLState.URLStateBuilder builder = URLState.builder()
                .id(IDUtils.genUrlStateId())
                .fromIp(request.getRemoteAddr())
                .fromHost(request.getRemoteHost())
                .start(System.currentTimeMillis())
                .duration(duration.toMillis());
        // 包含键名requestURI，说明URL Map中有URLInfo，构建URLState来表示当前URLInfo的状态
        urlState = builder
                .urlId(urlInfo.getId())
                .url(urlInfo.getUrl())
                .currentConcurrency(urlInfo.getMaxConcurrency().get())
                .currentFailTimes(urlInfo.getFailTimes().get())
                .build();
        // 已执行完业务逻辑，url的最大并发量减1
        urlInfo.getMaxConcurrency().decrementAndGet();
        // 需要网络传输到监控中心，所以用线程池异步处理
        pool.execute(() -> communicationProxy.pushUrlState(urlState));
    }

    @Override
    public void destroy() {
        pool.shutdown();
    }

    /**
     * 二分匹配路径
     *
     * @param urlInfos   urlInfos
     * @param requestURL requestURL
     * @return position
     */
    protected abstract int binaryMatcherPath(List<URLInfo> urlInfos, String requestURL);
}
