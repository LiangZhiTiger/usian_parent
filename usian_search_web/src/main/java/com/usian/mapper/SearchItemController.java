package com.usian.mapper;

import com.usian.feign.SearchItemFeign;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/searchItem")
public class SearchItemController {
    @Autowired
    private SearchItemFeign searchItemFeign;

    @RequestMapping("/importAll")
    public Result importAll(){
        Boolean bool = searchItemFeign.importAll();
        if (bool){
            return Result.ok(200);
        }
        return Result.error("导入失败");
    }

}
