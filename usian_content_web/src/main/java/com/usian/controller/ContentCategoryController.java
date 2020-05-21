package com.usian.controller;

import com.usian.pojo.TbContentCategory;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backend/content")
public class ContentCategoryController {

    @Autowired
    private ContentServiceFeign contentServiceFeign;

    @RequestMapping("/selectContentCategoryByParentId")
    public Result selectContentCategoryByParentId(@RequestParam(defaultValue = "0") Long id){
        List<TbContentCategory> tbContentCategoryList = contentServiceFeign.selectContentCategoryByParentId(id);
        if (tbContentCategoryList.size()>0 && tbContentCategoryList!=null){
            return Result.ok(tbContentCategoryList);
        }
        return Result.error("查无结果");
    }

    @RequestMapping("/insertContentCategory")
    public Result insertContentCategory(TbContentCategory tbContentCategory){
        Integer num = contentServiceFeign.insertContentCategory(tbContentCategory);
        if (num==1){
            return Result.ok();
        }
        return Result.error("添加失败");
    }
}
