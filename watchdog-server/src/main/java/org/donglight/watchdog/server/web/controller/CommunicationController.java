package org.donglight.watchdog.server.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.donglight.watchdog.common.domain.URLInfo;
import org.donglight.watchdog.common.domain.URLState;
import org.donglight.watchdog.common.domain.WebApplication;
import org.donglight.watchdog.common.dto.RegistrationDTO;
import org.donglight.watchdog.common.util.Response;
import org.donglight.watchdog.common.util.ResponseEnum;
import org.donglight.watchdog.server.util.PollingState;
import org.donglight.watchdog.server.web.WebApplicationManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 〈一句话功能简述〉<br>
 * 与watchdog客户端通信的restful接口
 *
 * @author donglight
 * @date 2019/4/17
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class CommunicationController {

    private final WebApplicationManager webApplicationManager;

    @Autowired
    public CommunicationController(WebApplicationManager webApplicationManager) {
        this.webApplicationManager = webApplicationManager;
    }

    /**
     * 注册需要被监控的应用
     *
     * @param fromClient 应用信息
     * @return Response
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response<RegistrationDTO> registerApplication(@RequestBody Map<String, String> fromClient, HttpServletRequest request) {
        fromClient.put("addr", request.getRemoteAddr());
        Optional<WebApplication> webAppOptional = webApplicationManager.registerApplication(fromClient);
        RegistrationDTO registrationDTO = new RegistrationDTO();
        PollingState.setChangeApplications(true);
        return webAppOptional.map(webApp -> {
            BeanUtils.copyProperties(webApp, registrationDTO);
            return Response.ok("registration was successful", registrationDTO);
        }).orElseGet(() -> Response.exception(ResponseEnum.REGISTER_FAIL));
    }

    /**
     * 接收客户端的urlInfoList，设置到appId对应的application中
     *
     * @param appId
     * @param urlInfoList
     * @return
     */
    @PostMapping(value = "/application/{appId}/urls", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response setUrls(@PathVariable String appId, @RequestBody List<URLInfo> urlInfoList) {
        WebApplication application = webApplicationManager.getApplicationById(appId);
        application.setUrlInfoList(urlInfoList);
        log.info("successful set urls into application");
        return Response.ok("successful set urls into application", "");
    }

    /**
     * 接收urlInfo，添加到对相应的application中
     *
     * @param appId
     * @param urlInfo
     * @return
     */
    @PostMapping(value = "/application/{appId}/url", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response addNewURLInfo(@PathVariable String appId, @RequestBody URLInfo urlInfo) {
        WebApplication application = webApplicationManager.getApplicationById(appId);
        urlInfo.getRequestTimes().incrementAndGet();
        urlInfo.getMaxConcurrency().incrementAndGet();
        urlInfo.setSuccessRate(1);
        application.getUrlInfoList().add(urlInfo);
        PollingState.setChangeApplications(true);
        log.info("successful add url '{}' into url list", urlInfo.getUrl());
        return Response.ok("successful add url into url list", "");
    }

    /**
     * 接收URLState，添加到对应的URLInfo中
     *
     * @param appId
     * @param urlId
     * @param urlState
     * @return
     */
    @PostMapping(value = "/application/{appId}/url/{urlId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Response addURLState(@PathVariable String appId, @PathVariable Long urlId, @RequestBody URLState urlState) {
        WebApplication application = webApplicationManager.getApplicationById(appId);
        if (application == null) {
            return Response.exception(ResponseEnum.NOELEMENT);
        }
        URLInfo urlInfo = application.getUrlInfoList().stream().filter(url -> url.getId() == urlId).findAny().orElse(null);

        if (urlInfo == null) {
            return Response.exception(ResponseEnum.NOELEMENT);
        } else {
            urlInfo.getUrlStatList().add(urlState);
            AtomicInteger maxConcurrency = urlInfo.getMaxConcurrency();
            int oldConcurrency = maxConcurrency.get();
            int currentConcurrency = urlState.getCurrentConcurrency();
            if (oldConcurrency < currentConcurrency) {
                maxConcurrency.compareAndSet(oldConcurrency,urlState.getCurrentConcurrency());
            }
            long requestTimes = urlInfo.getRequestTimes().incrementAndGet();
            urlInfo.setSuccessRate((requestTimes - urlInfo.getFailTimes().get()) / requestTimes);
            PollingState.setChangeURLStates(true);
            log.info("successful add URLState {} into URLInfo list", urlState);
            return Response.ok("successful add URLState into URLInfo", "");
        }
    }
}
