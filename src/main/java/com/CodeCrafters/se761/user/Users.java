package com.CodeCrafters.se761.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Users {
    @Id
    @Column(name = "upi")
    private String upi;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "user_profile")
    private String userProfile;
    @Column(name = "phone")
    private String phone;

    public Users(String upi,
                String name,
                String email,
                String userProfile,
                String phone) {
        this.upi = upi;
        this.name = name;
        this.email = email;
        this.userProfile = userProfile;
        this.phone = phone;
    }

    public Users() {

    }

    public String getUpi() {
        return upi;
    }

    public void setUpi(String upi) {
        this.upi = upi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "upi='" + upi + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userProfile='" + userProfile + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

}