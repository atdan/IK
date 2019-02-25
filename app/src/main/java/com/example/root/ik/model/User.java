package com.example.root.ik.model;

public class User {
    private String name;
    private String Password;
    private String email, IsStaff;
    private String secureCode;
    private String HomeAddress;
    private String Phone, imageUrl, status;


    public User(){

    }


    public User(String name, String password, String email, String isStaff, String secureCode, String homeAddress, String phone, String imageUrl, String status) {
        this.name = name;
        Password = password;
        this.email = email;
        IsStaff = isStaff;
        this.secureCode = secureCode;
        HomeAddress = homeAddress;
        Phone = phone;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getHomeAddress() {
        return HomeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        HomeAddress = homeAddress;
    }
}
