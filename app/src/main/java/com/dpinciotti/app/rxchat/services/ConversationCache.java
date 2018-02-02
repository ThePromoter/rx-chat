package com.dpinciotti.app.rxchat.services;

import android.content.Context;

import com.dpinciotti.app.rxchat.R;
import com.dpinciotti.app.rxchat.models.Conversation;
import com.dpinciotti.app.rxchat.models.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConversationCache {

    private static ConversationCache INSTANCE;
    private Map<String, Conversation> conversationMap;
    private Context context;

    private ConversationCache(Context context) {
        this.context = context;
        conversationMap = new LinkedHashMap<>();
        buildDefaultData();
    }

    private void buildDefaultData() {
        GregorianCalendar calendar = new GregorianCalendar();

        calendar.add(Calendar.MINUTE, -1);
        saveConversation(new Conversation(context.getString(R.string.conversation_channel_1), context.getString(R.string.conversation_name_1), new Message(context.getString(R.string.conversation_message_1), calendar.getTime()), R.drawable.ic_profile_charlie));
        calendar.add(Calendar.MINUTE, -13);
        saveConversation(new Conversation(context.getString(R.string.conversation_channel_2), context.getString(R.string.conversation_name_2), new Message(context.getString(R.string.conversation_message_2), calendar.getTime()), R.drawable.ic_profile_dennis));
        calendar.add(Calendar.MINUTE, -20);
        saveConversation(new Conversation(context.getString(R.string.conversation_channel_3), context.getString(R.string.conversation_name_3), new Message(context.getString(R.string.conversation_message_3), calendar.getTime()), R.drawable.ic_profile_frank));
        calendar.add(Calendar.HOUR, -4);
        saveConversation(new Conversation(context.getString(R.string.conversation_channel_4), context.getString(R.string.conversation_name_4), new Message(context.getString(R.string.conversation_message_4), calendar.getTime()), R.drawable.ic_profile_dee));
        calendar.add(Calendar.DATE, -2);
        saveConversation(new Conversation(context.getString(R.string.conversation_channel_5), context.getString(R.string.conversation_name_5), new Message(context.getString(R.string.conversation_message_5), calendar.getTime()), R.drawable.ic_profile_mac));
    }

    public static ConversationCache getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new ConversationCache(context);
        return INSTANCE;
    }

    public Conversation getConversation(String name) {
        return conversationMap.get(name);
    }

    public void saveConversation(Conversation conversation) {
        conversationMap.put(conversation.getName(), conversation);
    }

    public List<Conversation> getAllConversations() {
        return new ArrayList<>(conversationMap.values());
    }
}
