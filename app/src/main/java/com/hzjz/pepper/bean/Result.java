package com.hzjz.pepper.bean;

import java.io.Serializable;

public class Result implements Serializable{
    private String headImg;
    private UserBean authUser;
    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public UserBean getAuthUser() {
        return authUser;
    }

    public void setAuthUser(UserBean authUser) {
        this.authUser = authUser;
    }
}
