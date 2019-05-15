package org.donglight.watchdog.client.web.filter;

import org.donglight.watchdog.common.bean.URLInfo;
import org.donglight.watchdog.common.util.CommonUtil;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * 针对原生servlet应用的URLFilter
 *
 * @author donglight
 * @date 2019/5/11
 * @since 1.0.0
 */
@Order(1)
public class URLFilterForServlet extends AbstractURLFilter {

    @Override
    protected int binaryMatcherPath(List<URLInfo> urlInfos, String requestURL) {
        int low = 0, high = urlInfos.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (CommonUtil.match(urlInfos.get(mid).getUrl(), requestURL)) {
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
