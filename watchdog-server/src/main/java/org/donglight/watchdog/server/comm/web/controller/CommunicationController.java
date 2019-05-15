package org.donglight.watchdog.server.comm.web.controller;

import org.donglight.watchdog.common.bean.Metrics;
import org.donglight.watchdog.common.bean.URLInfo;
import org.donglight.watchdog.common.bean.URLState;
import org.donglight.watchdog.common.bean.WatchDogClientConfigBean;
import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.server.comm.AbstractWatchDogCommunicationService;
import org.donglight.watchdog.server.comm.WebApplicationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 与watchdog客户端通信的restful接口
 *
 * @author donglight
 * @date 2019/4/17
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api")
public class CommunicationController extends AbstractWatchDogCommunicationService {


    @Autowired
    public CommunicationController(WebApplicationManager webApplicationManager) {
        super(webApplicationManager);
    }

    @Override
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response registerApplication(@RequestBody WatchDogClientConfigBean watchDogClientConfigBean) {
        return super.registerApplication(watchDogClientConfigBean);
    }

    @Override
    @PostMapping(value = "/{appName}/{instanceId}/renew", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String renewApplication(@PathVariable("appName") String appName,
                                   @PathVariable("instanceId") String instanceId,
                                   @RequestBody String ping) {
        return super.renewApplication(appName, instanceId, ping);
    }

    @Override
    @PostMapping(value = "/{appName}/{instanceId}/urls", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response setUrls(@PathVariable("appName") String appName,
                            @PathVariable("instanceId") String instanceId,
                            @RequestBody List<URLInfo> urlInfoList) {
        return super.setUrls(appName, instanceId, urlInfoList);
    }


    @Override
    @PostMapping(value = "/{appName}/{instanceId}/url", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response addNewURLInfo(@PathVariable("appName") String appName,
                                  @PathVariable("instanceId") String instanceId,
                                  @RequestBody URLInfo urlInfo) {
        return super.addNewURLInfo(appName, instanceId, urlInfo);
    }


    @Override
    @PostMapping(value = "/{appName}/{instanceId}/urlState", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response addURLState(@PathVariable("appName") String appName,
                                @PathVariable("instanceId") String instanceId,
                                @RequestBody URLState urlState) {
        return super.addURLState(appName, instanceId, urlState);
    }


    @Override
    @PostMapping(value = "/{appName}/{instanceId}/metrics", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response setMetrics(@PathVariable("appName") String appName,
                               @PathVariable("instanceId") String instanceId,
                               @RequestBody Metrics metrics) {
        return super.setMetrics(appName, instanceId, metrics);
    }
}
