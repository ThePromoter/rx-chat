package com.dpinciotti.app.rxchat.models;

import java.util.Date;
import java.util.Objects;

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
        if (sentDate == null) sentDate = new Date();
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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(text, message.text) &&
               Objects.equals(sentDate, message.sentDate);
    }

    @Override public int hashCode() {

        return Objects.hash(text, sentDate);
    }
}
