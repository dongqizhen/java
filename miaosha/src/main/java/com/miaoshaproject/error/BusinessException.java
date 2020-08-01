package com.miaoshaproject.error;


//包装器业余异常实现
public class BusinessException extends Exception implements CommonError {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private CommonError commonError;

    public BusinessException(CommonError commonError){
        super();
        this.commonError=commonError;
    }

    //接收自定义errMsg的方式构造业务异常
    public BusinessException (CommonError commonError,String errMsg){
        super();
        this.commonError=commonError;
        this.commonError.setErrMsg(errMsg);
    }


    @Override
    public int getErrCode() {
        // TODO Auto-generated method stub
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        // TODO Auto-generated method stub
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        // TODO Auto-generated method stub
        this.commonError.setErrMsg(errMsg);
        return this;
    }

}