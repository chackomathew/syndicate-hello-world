package com.task10.entity;

import java.util.Map;

public class SignUpRequestModel {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public SignUpRequestModel(Map<String, String> body) {
        setDataFromRequestBody(body);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setDataFromRequestBody(Map<String, String> body) {
        this.firstName = body.get("firstName");
        this.lastName = body.get("lastName");
        this.email = body.get("email");
        this.password = body.get("password");
    }
}
