package com.dpinciotti.app.rxchat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dpinciotti.app.rxchat.models.Conversation;
import com.dpinciotti.app.rxchat.models.Message;
import com.dpinciotti.app.rxchat.services.ConversationCache;
import com.dpinciotti.app.rxchat.services.PubNubManager;
import com.dpinciotti.app.rxchat.utils.OnTextChangedListener;
import com.google.gson.Gson;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Calendar;
import java.util.Objects;

import io.reactivex.annotations.Nullable;

public class ConversationActivity
    extends AppCompatActivity {

    public static final String LOG_TAG = ConversationActivity.class.getSimpleName();
    public static final String CHANNEL_ID = "channel_id";

    private ConversationAdapter adapter;
    private ConversationCache conversationCache;
    private Gson gson;

    private String channelId;
    private EditText messageText;
    private FloatingActionButton sendButton;
    @Nullable private Message messageBeingSent;

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

        gson = new Gson();
        listenForMessages();
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
        // Cut off the milliseconds to avoid rounding errors
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);

        Message newMessage = new Message(messageText.getText().toString(), now.getTime());
        newMessage.setMe(true);
        PubNubManager.getInstance(this)
                     .sendMessage(channelId, newMessage, new PNCallback<PNPublishResult>() {
                         @Override public void onResponse(PNPublishResult result, PNStatus status) {
                             if (status.isError()) {
                                 Log.e(LOG_TAG, "Error sending message");
                             } else {
                                 Log.d(LOG_TAG, "Message sent successfully");
                             }
                         }
                     });
        messageText.getText().clear();
        messageBeingSent = newMessage;
    }

    private void listenForMessages() {
        PubNubManager.getInstance(this)
                     .listenToChannel(channelId, new SubscribeCallback() {
                         @Override
                         public void status(PubNub pubnub, PNStatus status) {
                             switch (status.getCategory()) {
                                 case PNUnexpectedDisconnectCategory:
                                     // This event happens when radio / connectivity is lost
                                     Log.e(LOG_TAG, "Connectivity lost");
                                     break;
                                 case PNConnectedCategory:
                                     // This event happens when we're successfully connected
                                     Log.d(LOG_TAG, "Successfully connected to channel");
                                     break;
                                 case PNReconnectedCategory:
                                     // This event happens when radio / connectivity is lost, then regained
                                     Log.d(LOG_TAG, "Successfully reconnected to channel");
                                     break;
                             }
                         }

                         @Override
                         public void message(PubNub pubnub, PNMessageResult message) {
                             if (Objects.equals(message.getChannel(), channelId)) {
                                 Log.d(LOG_TAG, "Message received: " + message.getMessage());
                                 Message chatMessage = gson.fromJson(message.getMessage(), Message.class);
                                 // If the message being sent is equal to this one, it's from me
                                 if (chatMessage.equals(messageBeingSent)) chatMessage.setMe(true);
                                 conversationCache.saveMessage(channelId, chatMessage);

                                 // Need to remember to run this on the UI thread!
                                 runOnUiThread(() -> adapter.addMessage(chatMessage));
                             }
                         }

                         @Override
                         public void presence(PubNub pubnub, PNPresenceEventResult presence) {

                         }
                     });
    }
}
