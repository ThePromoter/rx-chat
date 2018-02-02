package com.dpinciotti.app.rxchat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.dpinciotti.app.rxchat.models.Conversation;
import com.dpinciotti.app.rxchat.models.Message;
import com.dpinciotti.app.rxchat.services.ConversationCache;
import com.dpinciotti.app.rxchat.utils.OnTextChangedListener;

import java.util.Calendar;

public class ConversationActivity
    extends AppCompatActivity{

    public static final String CHANNEL_ID = "channel_id";

    private ConversationAdapter adapter;
    private ConversationCache conversationCache;

    private String channelId;
    private EditText messageText;
    private FloatingActionButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        channelId = getIntent().getStringExtra(CHANNEL_ID);
        conversationCache = ConversationCache.getInstance(this);
        Conversation conversation = conversationCache.getConversation(channelId);

        setTitle(conversation.getName());

        adapter = new ConversationAdapter(this, conversation);
        adapter.setMessages(conversationCache.getMessages(channelId));

        RecyclerView conversationRecycler = findViewById(R.id.message_recycler);
        conversationRecycler.setAdapter(adapter);
        conversationRecycler.setLayoutManager(new LinearLayoutManager(this));

        messageText = findViewById(R.id.send_message_text);
        messageText.addTextChangedListener((OnTextChangedListener) (s, start, before, count) -> toggleSendButton(s.length() > 0));

        sendButton = findViewById(R.id.send_message_button);
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(this::sendMessage);
    }

    public void toggleSendButton(boolean enabled) {
        if (enabled && !sendButton.isEnabled()) {
            sendButton.setEnabled(true);
            sendButton.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override public void onHidden(FloatingActionButton fab) {
                    sendButton.show();
                    sendButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));
                }
            });
        } else if (!enabled && sendButton.isEnabled()) {
            sendButton.setEnabled(false);
            sendButton.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override public void onHidden(FloatingActionButton fab) {
                    sendButton.show();
                    sendButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray_light)));
                }
            });
        }
    }

    public void sendMessage(View view) {
        Message newMessage = new Message(messageText.getText().toString(), Calendar.getInstance().getTime());
        newMessage.setMe(true);
        adapter.addMessage(newMessage);
        conversationCache.saveMessage(channelId, newMessage);
        messageText.setText("");
    }
}
