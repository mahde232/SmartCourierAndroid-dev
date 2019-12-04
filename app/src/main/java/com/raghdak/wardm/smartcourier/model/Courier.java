package com.raghdak.wardm.smartcourier.model;


import java.io.Serializable;

/**
 * Created by wardm on 22/11/2017.
 */
public class Courier implements Serializable {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;

    public Courier(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }
    public Courier(String id, String email, String password, /*String firstName, String lastName, String address,*/ String phone) {
        super();
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Courier [email=" + email + ", password=" + password + /*", firstName=" + firstName + ", LastName=" + lastName
                + ", address=" + address + ", */ " phone=" + phone + "]";
    }

}

