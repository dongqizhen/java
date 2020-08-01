package com.miaoshaproject.error;

public enum EmBusinessError implements CommonError {
    //通用错误类型10001
    PARAPARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    //
    UNKNOWN_ERROR(10002,"未知错误"),
    //2000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001,"用户不存在"),
    USER_LOGIN_FAIL(20002, "用户手机号或密码不正确"),
    USER_NOT_LOGIN(20003, "用户未登录"),
    //3000开头为交易信息错误定义
    STOCK_NOT_ENOUGH(30001,"库存不足")
    ;

    private int errCode;
    private String errMsg;


    private EmBusinessError(int errCode,String errMsg){
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        // TODO Auto-generated method stub
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        // TODO Auto-generated method stub
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        // TODO Auto-generated method stub
       this.errMsg = errMsg;
       return this;
    }

	// public void setErrCode(int errCode) {
	// 	this.errCode = errCode;
	// }

}