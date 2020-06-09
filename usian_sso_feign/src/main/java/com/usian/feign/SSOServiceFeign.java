package com.usian.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("usian-sso-service")
public interface SSOServiceFeign {
    @RequestMapping("/service/sso/checkUserInfo/{checkValue}/{checkFlag}")
    public Boolean checkUserInfo(@PathVariable String checkValue,@PathVariable Integer checkFlag);
}
