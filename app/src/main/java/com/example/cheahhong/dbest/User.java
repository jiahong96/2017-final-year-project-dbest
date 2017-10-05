package com.example.cheahhong.dbest;

/**
 * Created by lesgo on 5/6/2017.
 */

public class User {
    String email;
    String name;
    String  type;
    int memberPoint;

    public User(String email, String name,String type,int memberPoint) {
        this.email = email;
        this.name = name;
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

    public Integer getMemberPoint() {
        return memberPoint;
    }

    public void setMemberPoint(Integer memberPoint) {
        this.memberPoint = memberPoint;
    }
}
