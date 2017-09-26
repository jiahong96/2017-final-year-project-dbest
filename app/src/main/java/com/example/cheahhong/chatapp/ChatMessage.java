package com.example.cheahhong.chatapp;

import java.util.Date;

/**
 * Created by CheahHong on 3/24/2017.
 */

public class ChatMessage {
    private String messageID="";
    private String messageText="";
    private String messageUser="";
    private String link="";
    private Boolean messageRead=false;
    private long messageTime=0;
    private String inquiryOwner="";
    private String messageType="";

    public ChatMessage(String messageID,String messageText, String messageUser,long messageTime, String inqOw,String type,String link) {
        this.messageID = messageID;
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageRead = false;
        this.messageTime = messageTime;
        this.inquiryOwner = inqOw;
        this.messageType = type;
        this.link = link;
    }

    public ChatMessage(){

    }

    public String getInquiryOwner() {
        return inquiryOwner;
    }

    public void setInquiryOwner(String inquiryOwner) {
        this.inquiryOwner = inquiryOwner;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public Boolean getMessageRead() {
        return messageRead;
    }

    public void setMessageRead(Boolean messageRead) {
        this.messageRead = messageRead;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
