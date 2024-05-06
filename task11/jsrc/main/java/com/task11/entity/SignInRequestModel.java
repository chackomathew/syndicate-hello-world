package com.task11.entity;

import java.util.Map;

public class SignInRequestModel {

    private String email;
    private String password;

    public SignInRequestModel(Map<String, String> body) {
        setDataFromRequestBody(body);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setDataFromRequestBody(Map<String, String> body) {
        this.email = body.get("email");
        this.password = body.get("password");
    }
}
