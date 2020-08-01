package com.miaoshaproject.controller;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.viewobject.UserVo;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户登录接口
    @RequestMapping(value = "/login", method = { RequestMethod.POST }, consumes = { CONTENT_TYPE_FORMED })
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone") String telphone,@RequestParam(name="password") String password) throws BusinessException,
            NoSuchAlgorithmException, UnsupportedEncodingException {
        //入参校验
        if(org.apache.commons.lang3.StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password) ){
            throw new BusinessException(EmBusinessError.PARAPARAMETER_VALIDATION_ERROR);
        }
        //用户登录服务，用来校验用户登录是否合法
        UserModel userModel =  userService.login(telphone, this.EncodeByMd5(password));

        //将登录凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);
        return CommonReturnType.create(null);
    }


    // 用户注册接口
    @RequestMapping(value = "/register", method = { RequestMethod.POST }, consumes = { CONTENT_TYPE_FORMED })
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
            @RequestParam(name = "optCode") String optCode, @RequestParam(name = "name") String name,
            @RequestParam(name = "gender") Integer gender, @RequestParam(name = "age") Integer age,
            @RequestParam(name = "password") String password) throws BusinessException, NoSuchAlgorithmException,
            UnsupportedEncodingException {
        // 验证对应的手机号和optcode相符合
        String inSessionOptCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        if (!StringUtils.equals(optCode, inSessionOptCode)) {
            throw new BusinessException(EmBusinessError.PARAPARAMETER_VALIDATION_ERROR, "短信验证码不符合");
        }

        // 用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(Byte.parseByte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));

        userService.register(userModel);

        return CommonReturnType.create(null);
    }

    public String EncodeByMd5(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Encoder encoder = Base64.getEncoder();

        // 加密字符串
        String newStr = encoder.encodeToString(md5.digest(password.getBytes("utf-8")));
        return newStr;
    }

    //用户获取opt短信接口
    @RequestMapping(value = "/getopt",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOpt(@RequestParam(name = "telphone") String telphone){
        //需要按照一定的规则生成OTP验证码

        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;

        String optCode = String.valueOf(randomInt);

        //将OPT验证码同对应手机号关联

        httpServletRequest.getSession().setAttribute(telphone, optCode);
        //将OPT验证码通过短信通道发送给用户
        System.out.println(optCode);

        return CommonReturnType.create(null);
    }



    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException {
        //调用service服务获取对应ID的用户对象并返回到前端
        UserModel userModel= userService.getUserById(id);

        //若获取的用户信息不存在
        if(userModel==null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        UserVo userVo = convertFromModel(userModel);
        //返回通用对象
        return CommonReturnType.create(userVo);
    }

    private UserVo convertFromModel(UserModel userModel){
        if(userModel==null){
            return null;
        }else{
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(userModel, userVo);
            return userVo;
        }
    }


}