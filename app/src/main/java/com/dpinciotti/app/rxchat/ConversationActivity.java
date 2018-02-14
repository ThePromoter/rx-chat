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

import java.util.Calendar;
import java.util.Objects;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.IoScheduler;

public class ConversationActivity
    extends AppCompatActivity {

    public static final String LOG_TAG = ConversationActivity.class.getSimpleName();
    public static final String CHANNEL_ID = "channel_id";

    private ConversationAdapter adapter;
    private ConversationCache conversationCache;
    private PubNubManager pubNubManager;

    private String channelId;
    private EditText messageText;
    private FloatingActionButton sendButton;

    @Nullable private Message messageBeingSent;
    @Nullable private CompositeDisposable subscriptions = new CompositeDisposable();
    private Gson gson = new Gson();
    private Scheduler ioThread = new IoScheduler();
    private Scheduler uiThread = AndroidSchedulers.mainThread();

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

        sendButton = findViewById(R.id.send_message_button);
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(this::sendMessage);

        messageText = findViewById(R.id.send_message_text);

        pubNubManager = PubNubManager.getInstance(this);

        // Listen for text changes, and toggle the send button depending on if there's text or not
        Disposable textChangedSubscription = textChangedObservable(messageText)
            .map(text -> text.length() > 0)
            .subscribe(this::toggleSendButton);

        // Begin listening for new messages
        Disposable listenerSubscription = listenForMessages();

        subscriptions.addAll(textChangedSubscription, listenerSubscription);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        pubNubManager.disconnect();
        subscriptions.dispose();
    }

    private Flowable<String> textChangedObservable(EditText editText) {
        return Flowable.create(emitter -> editText.addTextChangedListener((OnTextChangedListener) (s, start, before, count) -> emitter.onNext(s.toString())), BackpressureStrategy.LATEST);
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

    private Disposable listenForMessages() {
        return pubNubManager.messageReceivedFlowable(channelId)
                            .subscribeOn(ioThread)
                            .observeOn(uiThread)
                            .filter(pubNubMessage -> Objects.equals(pubNubMessage.getChannel(), channelId))
                            .map(pubNubMessage -> gson.fromJson(pubNubMessage.getMessage(), Message.class))
                            .doOnNext(message -> Log.d(LOG_TAG, "Message received: " + message))
                            .filter(message -> !message.equals(messageBeingSent))
                            .doOnNext(message -> conversationCache.saveMessage(channelId, message))
                            .subscribe(adapter::addMessage);
    }

    public void sendMessage(View view) {
        Disposable messageSendSubscription =
            Single.just(messageText.getText().toString())
                  .subscribeOn(ioThread)
                  .observeOn(uiThread)
                  .zipWith(Single.just(Calendar.getInstance())
                                 // Cut off the milliseconds to avoid rounding errors
                                 .doOnSuccess(now -> now.set(Calendar.MILLISECOND, 0)),
                           (text, now) -> new Message(text, now.getTime()))
                  .doOnSuccess(newMessage -> messageBeingSent = newMessage)
                  .flatMap(newMessage -> pubNubManager.sendMessage(channelId, newMessage))
                  .doOnError(throwable -> Log.e(LOG_TAG, "Error sending message", throwable))
                  .doOnSuccess(result -> Log.d(LOG_TAG, "Message sent successfully"))
                  .doOnSuccess(newMessage -> newMessage.setMe(true))
                  .doOnSuccess(newMessage -> conversationCache.saveMessage(channelId, newMessage))
                  .subscribe(message -> adapter.addMessage(message));

        subscriptions.add(messageSendSubscription);
        messageText.getText().clear();
    }
}
