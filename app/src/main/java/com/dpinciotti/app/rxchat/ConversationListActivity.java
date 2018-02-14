package com.dpinciotti.app.rxchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dpinciotti.app.rxchat.models.Conversation;
import com.dpinciotti.app.rxchat.services.ConversationCache;

public class ConversationListActivity
    extends AppCompatActivity{

    private ConversationListAdapter adapter;
    private ConversationCache conversationCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        conversationCache = ConversationCache.getInstance(this);

        adapter = new ConversationListAdapter(this, this::conversationClicked);
        adapter.setConversations(conversationCache.getAllConversations());

        RecyclerView conversationRecycler = findViewById(R.id.conversation_recycler);
        conversationRecycler.setAdapter(adapter);
        conversationRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    public void conversationClicked(Conversation conversation) {
        Intent newIntent = new Intent(this, ConversationActivity.class);
        newIntent.putExtra(ConversationActivity.CHANNEL_ID, conversation.getChannelId());
        startActivity(newIntent);
    }
}
