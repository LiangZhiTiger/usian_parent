package com.usian.controller;

import com.usian.feign.SSOServiceFeign;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/sso")
public class SSOController {
    @Autowired
    private SSOServiceFeign ssoServiceFeign;

    @RequestMapping("/checkUserInfo/{checkValue}/{checkFlag}")
    public Result checkUserInfo(@PathVariable String checkValue,@PathVariable Integer checkFlag){
        Boolean bool = ssoServiceFeign.checkUserInfo(checkValue,checkFlag);
        if (bool){
            return Result.ok();
        }
        return Result.error("校验失败");
    }
}
