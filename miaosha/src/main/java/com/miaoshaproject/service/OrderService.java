package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.OrderModel;

public interface OrderService {
    //1.通过前端URL上传过来秒杀活动ID，然后下单接口内校验对应ID是否属于属于对应商品活动已经开始
    //2.直接在下单接口内判断对应商品是否存在秒杀活动，若存在进行中的则以秒杀价格下单
    OrderModel creatOrder(Integer userId,Integer itemId,Integer promoId, Integer amount) throws BusinessException;

}