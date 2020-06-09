package com.usian.controller;

import com.usian.service.SSOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/sso")
public class SSOController {
    @Autowired
    private SSOService ssoService;

    @RequestMapping("/checkUserInfo/{checkValue}/{checkFlag}")
    public Boolean checkUserInfo(@PathVariable String checkValue,@PathVariable Integer checkFlag){
        return ssoService.checkUserInfo(checkValue,checkFlag);
    }
}
