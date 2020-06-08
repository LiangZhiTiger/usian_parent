package com.usian.feign;

import com.usian.pojo.SearchItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-search-service")
public interface SearchItemFeign {
    /**
     * 商品数据导入索引库
     * @return
     */
    @RequestMapping("/service/searchItem/importAll")
    public Boolean importAll();

    /**
     * 通过索引库搜索到商品
     * @param q
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping("/service/searchItem/selectByQ")
    List<SearchItem> selectByQ(@RequestParam String q,@RequestParam Long page,@RequestParam Integer pageSize);
}
