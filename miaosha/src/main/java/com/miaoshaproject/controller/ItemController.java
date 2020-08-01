package com.miaoshaproject.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.miaoshaproject.controller.viewobject.ItemVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller("/item")
@RequestMapping("/item")
@CrossOrigin(origins = { "*" }, allowCredentials = "true")
public class ItemController extends BaseController {

    @Autowired
    private ItemService itemService;

    private ItemVO convertItemVoFromItemMedol(ItemModel itemModel){

        if(itemModel==null){
            return null;
        }

        ItemVO itemVO=new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);

        if(itemModel.getPromoModel()!= null){
            //有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            itemVO.setPromoStatus(0);
        }

        return itemVO;

    }
    // 创建商品的controller
    @RequestMapping(value = "/create", method = { RequestMethod.POST }, consumes = { CONTENT_TYPE_FORMED })
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
            @RequestParam(name = "description") String description, @RequestParam(name = "price") BigDecimal price,
            @RequestParam(name = "stock") Integer stock, @RequestParam(name = "imgUrl") String imgUrl)
            throws BusinessException {


            //封装service请求用来创建商品
            ItemModel itemModel =new ItemModel();

            itemModel.setTitle(title);
            itemModel.setDescription(description);
            itemModel.setImgUrl(imgUrl);
            itemModel.setPrice(price);
            itemModel.setStock(stock);

          ItemModel itemModelForReturn = itemService.createdItem(itemModel);

          ItemVO itemVO =convertItemVoFromItemMedol(itemModelForReturn);


        return CommonReturnType.create(itemVO);
    }

    // 商品详情页的浏览
    @RequestMapping(value = "/get", method = { RequestMethod.GET })
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id") Integer id) {
        ItemModel itemModel = itemService.getItemById(id);

        ItemVO itemVO = convertItemVoFromItemMedol(itemModel);

        return CommonReturnType.create(itemVO);
    }

    //商品列表页面浏览
    @RequestMapping(value = "/list", method = { RequestMethod.GET })
    @ResponseBody
    public CommonReturnType listItem(){

        List<ItemModel> itemModelList = itemService.listItem();
        //使用stream api 将list内的itemModel转化为ItemVO
       List<ItemVO> itemVOList =  itemModelList.stream().map(itemModel -> {
            ItemVO itemVO =this.convertItemVoFromItemMedol(itemModel);
            return itemVO;
        }).collect(Collectors.toList());

        return CommonReturnType.create(itemVOList);
    }
}

