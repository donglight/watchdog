package org.donglight.watchdog.client.web.filter;

import org.donglight.watchdog.client.web.proxy.CommunicationProxy;
import org.donglight.watchdog.common.bean.URLInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * 拦截、过滤URL，记录并推送到服务监控端
 * 服务于spring mvc
 *
 * @author donglight
 * @date 2019/4/22
 * @since 1.0.0
 */
@Order(1)
public class URLFilterForSpringMvc extends AbstractURLFilter {

    private AntPathMatcher matcher;

    public URLFilterForSpringMvc(CommunicationProxy communicationProxy) {
        super.communicationProxy = communicationProxy;
        matcher = new AntPathMatcher();
    }

    @Override
    protected int binaryMatcherPath(List<URLInfo> urlInfos, String requestURL) {
        int low = 0, high = urlInfos.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (matcher.match(urlInfos.get(mid).getUrl(), requestURL)) {
                return mid;
            } else if (requestURL.compareTo(urlInfos.get(mid).getUrl()) > 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }
}
