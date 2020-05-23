package com.usian.controller;

import com.usian.utils.AdNode;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/frontend/content")
public class ContentController {
    @Autowired
    private ContentServiceFeign contentServiceFeign;

    /**
     * 大广告展示查询
     * @return
     */
    @RequestMapping("/selectFrontendContentByAD")
    public Result selectFrontendContentByAD(){
        List<AdNode> adNodes = contentServiceFeign.selectFrontendContentByAD();
        if (adNodes!=null && adNodes.size()>0){
            return Result.ok(adNodes);
        }
        return Result.error("查无结果");
    }
}
