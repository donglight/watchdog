package org.donglight.watchdog.client.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.donglight.watchdog.common.util.HttpClientUtil;
import org.donglight.watchdog.common.util.Response;

import java.io.IOException;

/**
 * 〈一句话功能简述〉<br>
 * 功能
 *
 * @author donglight
 * @date 2019/4/20
 * @since 1.0.0
 */
public class DefaultCommunicationProxy implements CommunicationProxy {

    private String remoteAddress;

    private ObjectMapper objectMapper = new ObjectMapper();

    public DefaultCommunicationProxy() {
        this.remoteAddress = "http://127.0.0.1:8080/api";
    }

    public DefaultCommunicationProxy(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public boolean remoteRegister(String appName) {
        String registerUrl = "/register";
        try {
            String json = HttpClientUtil.doPostJson(remoteAddress + registerUrl, "{\"appName\":\""+appName+"\"}");
            Response response = objectMapper.readValue(json, Response.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
