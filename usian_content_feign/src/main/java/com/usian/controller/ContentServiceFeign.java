package com.usian.controller;

import com.usian.pojo.TbContentCategory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-content-service")
public interface ContentServiceFeign {

    @RequestMapping("/service/contentCategory/selectContentCategoryByParentId")
    public List<TbContentCategory> selectContentCategoryByParentId(@RequestParam Long id);
}
