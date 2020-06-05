package com.usian.mapper;

import com.usian.feign.SearchItemFeign;
import com.usian.pojo.SearchItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @RequestMapping("/list")
    public Result selectByQ(String q,@RequestParam(defaultValue = "1")Long page,@RequestParam(defaultValue = "20")Integer pageSize){
        List<SearchItem> searchItemList = searchItemFeign.selectByQ(q,page,pageSize);
        if (searchItemList!=null&&searchItemList.size()>0){
            return Result.ok(searchItemList);
        }
        return Result.error("查无结果");
    }

}
