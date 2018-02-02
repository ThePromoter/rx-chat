package com.dpinciotti.app.rxchat.models;

import android.support.annotation.DrawableRes;

public class Conversation {

    String channelId;
    String name;
    Message lastMessage;
    @DrawableRes int iconRes;

    public Conversation(String channelId, String name, Message lastMessage, @DrawableRes int iconRes) {
        this.channelId = channelId;
        this.name = name;
        this.lastMessage = lastMessage;
        this.iconRes = iconRes;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @DrawableRes public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(@DrawableRes int iconRes) {
        this.iconRes = iconRes;
    }
}
