package com.miaoshaproject.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.miaoshaproject.dao.ItemDOMapper;
import com.miaoshaproject.dao.ItemStockDOMapper;
import com.miaoshaproject.dataobject.ItemDO;
import com.miaoshaproject.dataobject.ItemStockDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.PromoModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.core.joran.util.beans.BeanUtil;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validatator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private PromoService promoService;

    private ItemDO convertItemDoFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }

        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    private ItemStockDO convertItemStockDoFromItemModel(ItemModel itemModel){
        if (itemModel == null) {
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();

        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());

        return itemStockDO;
    }

    @Override
    @Transactional
    public ItemModel createdItem(ItemModel itemModel) throws BusinessException {

        // 校验入参
        ValidationResult result = validatator.validate(itemModel);
       if(result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAPARAMETER_VALIDATION_ERROR,result.getErrMsg());
       }
        //转化itemModel->dataObject
        ItemDO itemDO = convertItemDoFromItemModel(itemModel);

        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO = convertItemStockDoFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        //返回创建完成的对象
        return getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDoList = itemDOMapper.listItem();
        List<ItemModel> itemModelList = itemDoList.stream().map(itemDO -> {
            //ItemStockDO itemStockDO=itemStockDOMapper.selectByItemId(itemDo.getId());
            ItemModel itemModel=this.convertItmeModelFromDataObject(itemDO, itemStockDOMapper.selectByItemId(itemDO.getId()));
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        //通过ID重数据库获取itemDO对象
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if(itemDO == null){
            return null;
        }

        //操作获得库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

        //将dataObject->model
        ItemModel itemModel = convertItmeModelFromDataObject(itemDO, itemStockDO);

        //获取活动商品信息
      PromoModel promoModel =  promoService.getPromoByItemId(itemModel.getId());
        if(promoModel !=null && promoModel.getStatus() != 3){
            itemModel.setPromoModel(promoModel);;
        }
        return itemModel;
    }

    private ItemModel convertItmeModelFromDataObject (ItemDO itemDO,ItemStockDO itemStockDO){
       ItemModel itemModel = new ItemModel();
       BeanUtils.copyProperties(itemDO, itemModel);

       itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
       itemModel.setStock(itemStockDO.getStock());

       return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
       int affectedRow = itemStockDOMapper.decreaseStock(itemId, amount);
       if(affectedRow > 0){
           //更新库存成功
           return true;
       }else{
           //更新库存失败
           return false;
       }

    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemDOMapper.increaseSales(itemId, amount);
    }

}