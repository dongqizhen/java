package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dao.UserPasswordDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.dataobject.UserPasswordDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class UserServiceImpl implements UserService{

   @Autowired
   private UserDOMapper userDOMapper;

   @Autowired
   private UserPasswordDOMapper userPasswordDOMapper;

   @Autowired
    private ValidatorImpl validator;

   @Override
   public UserModel getUserById(Integer id) {
        UserDO userDO =  userDOMapper.selectByPrimaryKey(id);
        if(userDO==null){
            return null;
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

        return convertFromDataObiect(userDO, userPasswordDO);
   }

   public UserModel convertFromDataObiect(UserDO userDO,UserPasswordDO userPasswordDO){
       if(userDO==null){
            return null;
       }else{
           UserModel userModel = new UserModel();
           BeanUtils.copyProperties(userDO, userModel);
           if(userPasswordDO!=null){
               userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
           }


           return userModel;
       }

   }

   @Override
   @Transactional
   public void register(UserModel userModel) throws BusinessException {
       // TODO Auto-generated method stub
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAPARAMETER_VALIDATION_ERROR);
        }
    //    if(StringUtils.isEmpty(userModel.getName()) || userModel.getGender()==null || userModel.getAge()==null
    //    || StringUtils.isEmpty(userModel.getTelphone())) {
    //         throw new BusinessException(EmBusinessError.PARAPARAMETER_VALIDATION_ERROR);
    //     }

       ValidationResult validationResult = validator.validate(userModel);

       if(validationResult.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAPARAMETER_VALIDATION_ERROR,validationResult.getErrMsg());
       }


        //实现model转成dataobject方法
        UserDO userDO = convertFromModel(userModel);
        try{
           userDOMapper.insertSelective(userDO);
        }catch(DuplicateKeyException exception){
            throw new BusinessException(EmBusinessError.PARAPARAMETER_VALIDATION_ERROR,"手机号已存在");
        }



        userModel.setId(userDO.getId());

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
   }

   private UserPasswordDO convertPasswordFromModel(UserModel userModel){
    if(userModel == null){
        return null;
    }else{
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setId(userModel.getId());
        return userPasswordDO;
    }
   }

   private UserDO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }else{
            UserDO userDO = new UserDO();
            BeanUtils.copyProperties(userModel, userDO);
            return userDO;
        }
   }

   @Override
   public UserModel login(String telphone, String encrptPassword) throws BusinessException {
       // TODO Auto-generated method stub
        //通过用户的手机获取用户的信息
      UserDO userDO= userDOMapper.selectByTelphone(telphone);
      if(userDO==null){
          throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
      }
      UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
      UserModel userModel = convertFromDataObiect(userDO,userPasswordDO);
        //比对用户信息加密的密码是否和传人的密码匹配

        if(!StringUtils.equals(encrptPassword, userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }

        return userModel;
   }


}