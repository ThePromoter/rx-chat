package com.dpinciotti.app.rxchat.services;

import android.content.Context;

import com.dpinciotti.app.rxchat.R;
import com.dpinciotti.app.rxchat.models.Message;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;

import static java.util.Collections.singletonList;

public class PubNubManager {

    private static PubNubManager INSTANCE;
    private PubNub pubnub;

    private PubNubManager(Context context) {
        PNConfiguration pnConfiguration = new PNConfiguration()
            .setSubscribeKey(context.getString(R.string.pubnub_sub_key))
            .setPublishKey(context.getString(R.string.pubnub_pub_key));

        pubnub = new PubNub(pnConfiguration);
    }

    public static PubNubManager getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new PubNubManager(context);
        return INSTANCE;
    }

    public void listenToChannel(String channelName, SubscribeCallback callback) {
        pubnub.addListener(callback);
        pubnub.subscribe().channels(singletonList(channelName)).execute();
    }

    public void sendMessage(String channelName, Message message, PNCallback<PNPublishResult> callback) {
        pubnub.publish()
              .channel(channelName)
              .message(message)
              .async(callback);
    }

    public void disconnect(String channelName) {
        pubnub.unsubscribe()
              .channels(singletonList(channelName))
              .execute();
    }

    public void disconnect() {
        pubnub.unsubscribeAll();
    }
}
