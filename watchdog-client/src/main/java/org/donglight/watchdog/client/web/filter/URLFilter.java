package org.donglight.watchdog.client.web.filter;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.donglight.watchdog.client.web.CommunicationProxy;
import org.donglight.watchdog.common.domain.URLInfo;
import org.donglight.watchdog.common.domain.URLState;
import org.donglight.watchdog.common.util.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 〈一句话功能简述〉<br>
 * 拦截、过滤URL，记录并推送到监控端
 *
 * @author donglight
 * @date 2019/4/22
 * @since 1.0.0
 */
@Order(1)
public class URLFilter implements Filter {

    @Autowired
    private CommunicationProxy communicationProxy;

    private ExecutorService pool;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        pool = new ScheduledThreadPoolExecutor(2,
                new BasicThreadFactory.Builder().namingPattern("watchdog-client-schedule-pool-%d").daemon(true).build());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();
        if (communicationProxy.isFilterExcludeUrl(requestURI)) {
            // 静态资源，不在统计的url范围之内
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //记录request起始时间
        LocalTime start = LocalTime.now();
        Map<String, URLInfo> watchingURLInfos = communicationProxy.getWatchingURLInfos();

        if (watchingURLInfos.containsKey(requestURI)) {
            // 即将执行filterChain.doFilter()放行，去执行业务逻辑，url的最大并发量加1
            watchingURLInfos.get(requestURI).getMaxConcurrency().incrementAndGet();
        } else {
            // url不存在，重新构建一个URLInfo
            URLInfo newURLInfo = URLInfo.builder()
                    .id(IDUtils.genUrlId())
                    .url(requestURI)
                    .httpMethod(request.getMethod())
                    .requestTimes(1)
                    .successRate(1)
                    .maxConcurrency(1)
                    .build();
            communicationProxy.getWatchingURLInfos().put(newURLInfo.getUrl(), newURLInfo);

            // 需要网络传输，所以用线程池异步处理
            pool.execute(() -> {
                communicationProxy.pushOneUrl(newURLInfo);
            });
        }

        // 放行
        filterChain.doFilter(servletRequest, servletResponse);

        //request结束时间
        LocalTime end = LocalTime.now();
        Duration duration = Duration.between(start, end);

        URLState urlState;
        URLState.Builder builder = URLState.builder()
                .id(IDUtils.genUrlStateId())
                .fromIp(request.getRemoteAddr())
                .fromHost(request.getRemoteHost())
                .start(System.currentTimeMillis())
                .duration(duration.toMillis());
        if (watchingURLInfos.containsKey(requestURI)) {
            // 包含键名requestURI，说明URL Map中有URLInfo，构建URLState来表示当前URLInfo的状态
            URLInfo urlInfo = watchingURLInfos.get(requestURI);
            urlState = builder
                    .urlId(urlInfo.getId())
                    .url(urlInfo.getUrl())
                    .currentConcurrency(urlInfo.getMaxConcurrency().get())
                    .build();
            // 已执行完业务逻辑，url的最大并发量减1
            urlInfo.getMaxConcurrency().decrementAndGet();
            // 需要网络传输到监控中心，所以用线程池异步处理
            pool.execute(() -> communicationProxy.recordUrlState(urlState));
        }

    }

    @Override
    public void destroy() {
        pool.shutdown();
    }
}
