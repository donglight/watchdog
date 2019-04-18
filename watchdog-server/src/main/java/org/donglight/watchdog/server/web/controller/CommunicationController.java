package org.donglight.watchdog.server.web.controller;

import org.donglight.watchdog.common.domain.WebApplication;
import org.donglight.watchdog.server.web.WebApplicationManagerFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 〈一句话功能简述〉<br>
 * 与watchdog客户端通信的restful接口
 *
 * @author donglight
 * @date 2019/4/17
 * @since 1.0.0
 */
@RestController
public class CommunicationController {

    private final WebApplicationManagerFacade managerFacade;

    @Autowired
    public CommunicationController(WebApplicationManagerFacade managerFacade) {
        this.managerFacade = managerFacade;
    }

    @RequestMapping("/registry")
    public ResponseEntity<String> registry(WebApplication application){
        boolean isRegister = managerFacade.registerApplication(application);
        if(isRegister){
            return ResponseEntity.ok("login was successful");
        }else{
            return ResponseEntity.ok("login was fail");
        }
    }
}
