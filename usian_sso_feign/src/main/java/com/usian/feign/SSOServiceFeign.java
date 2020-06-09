package com.usian.feign;

import com.usian.pojo.TbUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("usian-sso-service")
public interface SSOServiceFeign {
    /**
     * 注册校验
     * @param checkValue
     * @param checkFlag
     * @return
     */
    @RequestMapping("/service/sso/checkUserInfo/{checkValue}/{checkFlag}")
    public Boolean checkUserInfo(@PathVariable String checkValue,@PathVariable Integer checkFlag);

    /**
     * 注册账号
     * @param tbUser
     * @return
     */
    @RequestMapping("/service/sso/userRegister")
    Integer userRegister(@RequestBody TbUser tbUser);

    /**
     * 登录账号
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/service/sso/userLogin")
    Map userLogin(@RequestParam String username,@RequestParam String password);

    /**
     * 通过token查询用户信息
     * @param token
     * @return
     */
    @RequestMapping("/service/sso/getUserByToken/{token}")
    TbUser getUserByToken(@PathVariable String token);

    /**
     * 退出登录
     * @param token
     * @return
     */
    @RequestMapping("/service/sso/logOut")
    Boolean logOut(@RequestParam String token);
}
