package com.example.cheahhong.dbest;

/**
 * Created by lesgo on 5/6/2017.
 */

public class User {

    String email;
    String name;
    String contact;
    String address;
    String type;
    int    memberPoint;

    public User(String email, String name,String contact,String address,String type,int memberPoint) {
        this.email = email;
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.type = type;
        this.memberPoint = memberPoint;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMemberPoint() {
        return memberPoint;
    }

    public void setMemberPoint(int memberPoint) {
        this.memberPoint = memberPoint;
    }
}
