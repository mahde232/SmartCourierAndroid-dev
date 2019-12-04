package com.raghdak.wardm.smartcourier.protocol.response;

import com.raghdak.wardm.smartcourier.model.Courier;



/**
 * Created by wardm on 22/11/2017.
 */

public class LoginResponse {
    private String text;
    private Courier courier;
    public LoginResponse(String text) {
        super();
        this.text = text;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public Courier getCourier() {
        return courier;
    }
    public void setCourier(Courier user) {
        this.courier = user;
    }
    public static LoginResponse OK() {
        return new LoginResponse("Login Successfull!");
    }

    public static LoginResponse NO_User() {
        return new LoginResponse("User not found!");
    }

    public static LoginResponse ERROR() {
        return new LoginResponse("ERROR!");
    }

    public static LoginResponse WRONG_PASSWORD() {
        return new LoginResponse("Wrong Password!");
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "text='" + text + '\'' +
                ", courier=" + courier.toString() +
                '}';
    }
}

