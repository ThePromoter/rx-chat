package com.dpinciotti.app.rxchat.services;

import android.content.Context;
import android.util.Log;

import com.dpinciotti.app.rxchat.R;
import com.dpinciotti.app.rxchat.models.Message;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static java.util.Collections.singletonList;

public class PubNubManager {

    public static final String LOG_TAG = PubNubManager.class.getSimpleName();
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

    public Flowable<PNMessageResult> messageReceivedFlowable(String channelName) {
        return Flowable.create(emitter -> {
            pubnub.addListener(new SubscribeCallback() {
                @Override public void status(PubNub pubnub, PNStatus status) {
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

                @Override public void message(PubNub pubnub, PNMessageResult message) {
                    if (emitter.isCancelled()) return;
                    emitter.onNext(message);
                }

                @Override public void presence(PubNub pubnub, PNPresenceEventResult presence) {

                }
            });
            pubnub.subscribe().channels(singletonList(channelName)).execute();
        }, BackpressureStrategy.BUFFER);
    }

    public Single<Message> sendMessage(String channelName, Message message) {
        return Single.create(emitter -> pubnub.publish()
                                              .channel(channelName)
                                              .message(message)
                                              .async(new PNCallback<PNPublishResult>() {
                                                  @Override
                                                  public void onResponse(PNPublishResult result, PNStatus status) {
                                                      if (emitter.isDisposed()) return;

                                                      if (!status.isError()) {
                                                          emitter.onSuccess(message);
                                                      } else {
                                                          emitter.onError(status.getErrorData().getThrowable());
                                                      }
                                                  }
                                              }));
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
