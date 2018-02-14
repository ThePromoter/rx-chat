package com.dpinciotti.app.rxchat.services;

import android.content.Context;

import com.dpinciotti.app.rxchat.R;
import com.dpinciotti.app.rxchat.models.Conversation;
import com.dpinciotti.app.rxchat.models.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConversationCache {

    private static ConversationCache INSTANCE;
    private Map<String, Conversation> conversationMap;
    private Map<String, List<Message>> messageMap;
    private Context context;

    private ConversationCache(Context context) {
        this.context = context;
        conversationMap = new LinkedHashMap<>();
        messageMap = new HashMap<>();
        buildDefaultData();
    }

    private void buildDefaultData() {
        GregorianCalendar calendar = new GregorianCalendar();

        calendar.add(Calendar.MINUTE, -1);
        Message newestMessage = new Message(context.getString(R.string.conversation_message_1), calendar.getTime());
        saveConversation(new Conversation(context.getString(R.string.conversation_channel_1), context.getString(R.string.conversation_name_1), newestMessage, R.drawable.ic_profile_charlie));
        saveMessage(context.getString(R.string.conversation_channel_1), newestMessage);

        calendar.add(Calendar.MINUTE, -67);
        newestMessage = new Message(context.getString(R.string.conversation_message_2), calendar.getTime());
        saveConversation(new Conversation(context.getString(R.string.conversation_channel_2), context.getString(R.string.conversation_name_2), new Message(context.getString(R.string.conversation_message_2), calendar.getTime()), R.drawable.ic_profile_dennis));
        saveMessage(context.getString(R.string.conversation_channel_2), newestMessage);

        calendar.add(Calendar.HOUR, -5);
        newestMessage = new Message(context.getString(R.string.conversation_message_3), calendar.getTime());
        saveConversation(new Conversation(context.getString(R.string.conversation_channel_3), context.getString(R.string.conversation_name_3), new Message(context.getString(R.string.conversation_message_3), calendar.getTime()), R.drawable.ic_profile_frank));
        saveMessage(context.getString(R.string.conversation_channel_3), newestMessage);

        calendar.add(Calendar.DATE, -4);
        newestMessage = new Message(context.getString(R.string.conversation_message_4), calendar.getTime());
        saveConversation(new Conversation(context.getString(R.string.conversation_channel_4), context.getString(R.string.conversation_name_4), new Message(context.getString(R.string.conversation_message_4), calendar.getTime()), R.drawable.ic_profile_dee));
        saveMessage(context.getString(R.string.conversation_channel_4), newestMessage);

        calendar.add(Calendar.DATE, -17);
        newestMessage = new Message(context.getString(R.string.conversation_message_5), calendar.getTime());
        saveConversation(new Conversation(context.getString(R.string.conversation_channel_5), context.getString(R.string.conversation_name_5), new Message(context.getString(R.string.conversation_message_5), calendar.getTime()), R.drawable.ic_profile_mac));
        saveMessage(context.getString(R.string.conversation_channel_5), newestMessage);
    }

    public static ConversationCache getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new ConversationCache(context);
        return INSTANCE;
    }

    public Conversation getConversation(String channelId) {
        return conversationMap.get(channelId);
    }

    public void saveConversation(Conversation conversation) {
        conversationMap.put(conversation.getChannelId(), conversation);
    }

    public List<Conversation> getAllConversations() {
        return new ArrayList<>(conversationMap.values());
    }

    public void saveMessage(String channelId, Message message) {
        List<Message> existingMessages = messageMap.get(channelId);
        if (existingMessages == null) existingMessages = new LinkedList<>();
        existingMessages.add(message);
        messageMap.put(channelId, existingMessages);
    }

    public List<Message> getMessages(String channelId) {
        return new ArrayList<>(messageMap.get(channelId));
    }
}
