package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;

import java.util.Map;

public interface ItemService {

    TbItem selectItemInfo(Long itemId);

    PageResult selectTbItemAllByPage(Integer page, Integer rows);

    Integer insertTbItem(TbItem tbItem, String desc, String itemParams);

    Map<String,Object> preUpdateItem(Long id);

    Integer deleteItemById(Long itemId);
}

