package org.donglight.watchdog.common.dto;

import lombok.Data;

/**
 * 〈一句话功能简述〉<br>
 * 注册后返回到客户端的信息
 *
 * @author donglight
 * @date 2019/4/21
 * @since 1.0.0
 */
@Data
public class RegistrationDTO {

    private String token;

    private String appId;

}
