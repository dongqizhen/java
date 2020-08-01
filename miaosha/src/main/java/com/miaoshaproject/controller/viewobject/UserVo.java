package com.miaoshaproject.controller.viewobject;

/**
 *  通过ID获取用户信息返回的用户对象
 */
public class UserVo {
    private Integer id;
    private String name;
    private Integer gender;
    private Integer age;
    private String telphone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getGender() {
        return gender;
    }

    public Integer getAge() {
        return age;
    }

}