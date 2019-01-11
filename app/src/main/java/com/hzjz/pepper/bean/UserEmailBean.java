package com.hzjz.pepper.bean;

public class UserEmailBean {
    private String email;
    private String allEdit;
    private String allDelete;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAllEdit() {
        return allEdit;
    }

    public void setAllEdit(String allEdit) {
        this.allEdit = allEdit;
    }

    public String getAllDelete() {
        return allDelete;
    }

    public void setAllDelete(String allDelete) {
        this.allDelete = allDelete;
    }

    @Override
    public String toString() {
        allEdit = allEdit.equals("false")?"0":"1";
        allDelete = allDelete.equals("false")?"0":"1";
        return email+"::"+allEdit+"::"+allDelete;
    }
}
