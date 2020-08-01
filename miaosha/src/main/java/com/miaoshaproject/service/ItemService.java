package com.miaoshaproject.service;

import java.util.List;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.ItemModel;

public interface ItemService {
    //创建商品
    ItemModel createdItem(ItemModel itemModel) throws BusinessException;
    //商品列表浏览
    List<ItemModel> listItem();
    //商品详情浏览
    ItemModel getItemById(Integer id);

    //库存减扣
    boolean decreaseStock(Integer itemId,Integer amount) throws BusinessException;

    //商品销量增加
    void increaseSales(Integer itemId,Integer amount) throws BusinessException;
}