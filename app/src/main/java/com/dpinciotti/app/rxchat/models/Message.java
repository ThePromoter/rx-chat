package com.dpinciotti.app.rxchat.models;

import java.util.Date;

public class Message {

    String text;
    Date sentDate;
    transient boolean isMe;

    public Message(String text, Date sentDate) {
        this.text = text;
        this.sentDate = sentDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}
