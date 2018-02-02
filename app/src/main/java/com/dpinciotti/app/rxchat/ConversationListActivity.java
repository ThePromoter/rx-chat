package com.dpinciotti.app.rxchat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
        conversationRecycler.setLayoutManager(new LinearLayoutManager(this));
        conversationRecycler.setAdapter(adapter);

        FloatingActionButton newConversationButton = findViewById(R.id.fab_new_conversation);
        newConversationButton.setOnClickListener(this::startNewConversation);
    }

    public void conversationClicked(Conversation conversation) {

    }

    public void startNewConversation(View view) {

    }
}
