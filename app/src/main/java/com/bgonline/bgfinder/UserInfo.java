package com.bgonline.bgfinder;

import com.google.gson.Gson;

import java.io.Serializable;

public class UserInfo implements Serializable{
    private String userId;
    private String email;
    private String description;
    private String firstName;
    private String lastName;
    private String bggAccount;
    private String country;
    private String city;
    private String birthDate;
    private String userName;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getBggAccount() {
        return bggAccount;
    }

    public void setBggAccount(String bggAccount) {
        this.bggAccount = bggAccount;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static UserInfo fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, UserInfo.class);
    }
}
