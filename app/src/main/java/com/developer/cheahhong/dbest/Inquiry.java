package com.developer.cheahhong.dbest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CheahHong on 4/3/2017.
 */

public class Inquiry {

    private List<String>         inquiryPeoples          =null;
    private ArrayList<Item>      items                   = new ArrayList<>();
    private ArrayList<Quotation> quotations              = new ArrayList<>();
    private String               inquiryID               =null;
    private String               inquiryName             =null;
    private String               inquiryOwner            =null;
    private String               status                  =null;
    private String      userStatus              =null;
    private long        inquiryTime             =0;
    private ChatMessage lastMessage             =null;
    private int         msgUnreadCountForMobile =0;

    public Inquiry(List<String> inquiryPeoples, ArrayList<Item> items, ArrayList<Quotation> quotations, String inquiryID, String inquiryName, String inquiryOwner, ChatMessage lastMessage, long time, int msgUnreadCountForMobile, String status) {
        this.inquiryID = inquiryID;
        this.inquiryName = inquiryName;
        this.inquiryOwner = inquiryOwner;
        this.inquiryPeoples = inquiryPeoples;
        this.items = items;
        this.quotations = quotations;
        this.lastMessage = lastMessage;
        this.msgUnreadCountForMobile=msgUnreadCountForMobile;
        this.inquiryTime = time;
        this.status = status;
    }

    public Inquiry(){
    }

    public String getInquiryName() {
        return inquiryName;
    }

    public void setInquiryName(String inquiryName) {
        this.inquiryName = inquiryName;
    }

    public String getInquiryID() {
        return inquiryID;
    }

    public void setInquiryID(String inquiryID) {
        this.inquiryID = inquiryID;
    }

    public String getInquiryOwner() {
        return inquiryOwner;
    }

    public void setInquiryOwner(String inquiryOwner) {
        this.inquiryOwner = inquiryOwner;
    }

    public List<String> getInquiryPeoples() {
        return inquiryPeoples;
    }

    public void setInquiryPeoples(List<String> inquiryPeoples) {
        this.inquiryPeoples = inquiryPeoples;
    }

    public ChatMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getMsgUnreadCountForMobile() {
        return msgUnreadCountForMobile;
    }

    public void setMsgUnreadCountForMobile(int msgUnreadCountForMobile) {
        this.msgUnreadCountForMobile = msgUnreadCountForMobile;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public ArrayList<Quotation> getQuotations() {
        return quotations;
    }

    public void setQuotations(ArrayList<Quotation> quotations) {
        this.quotations = quotations;
    }

    public long getInquiryTime() {
        return inquiryTime;
    }

    public void setInquiryTime(long inquiryTime) {
        this.inquiryTime = inquiryTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
