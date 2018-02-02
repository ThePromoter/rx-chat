package com.dpinciotti.app.rxchat;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dpinciotti.app.rxchat.models.Conversation;
import com.dpinciotti.app.rxchat.models.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConversationListAdapter
    extends RecyclerView.Adapter<ConversationListAdapter.ConversationViewHolder> {

    public class ConversationViewHolder extends RecyclerView.ViewHolder {

        View container;
        ImageView personImage;
        TextView conversationName;
        TextView lastMessageText;
        TextView lastMessageDate;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.item_container);
            personImage = itemView.findViewById(R.id.person_icon);
            conversationName = itemView.findViewById(R.id.conversation_name);
            lastMessageText = itemView.findViewById(R.id.last_message);
            lastMessageDate = itemView.findViewById(R.id.last_message_date);
        }
    }

    public interface OnConversationClickedListener {

        void conversationClicked(Conversation conversation);
    }

    private Context context;
    private List<Conversation> conversations;
    private OnConversationClickedListener listener;

    public ConversationListAdapter(Context context, OnConversationClickedListener listener) {
        super();
        this.context = context;
        this.listener = listener;
        this.conversations = new ArrayList<>();
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyItemRangeInserted(0, conversations.size() - 1);
    }

    public void addConversation(Conversation conversation) {
        conversations.add(conversation);
        notifyItemInserted(conversations.size());
    }

    @Override public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View conversationItemView = inflater.inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(conversationItemView);
    }

    @Override public void onBindViewHolder(ConversationViewHolder vh, int position) {
        final Conversation conversation = getItem(position);

        vh.conversationName.setText(conversation.getName());

        Message lastMessage = conversation.getLastMessage();
        if (lastMessage != null) {
            vh.lastMessageText.setText(lastMessage.getText());
            vh.lastMessageDate.setText(DateUtils.getRelativeTimeSpanString(lastMessage.getSentDate().getTime(),
                                                                           Calendar.getInstance().getTimeInMillis(),
                                                                           DateUtils.MINUTE_IN_MILLIS));
        }

        if (conversation.getIconRes() != 0) {
            vh.personImage.setImageResource(conversation.getIconRes());
        } else {
            vh.personImage.setImageResource(R.drawable.ic_profile_default);
            vh.personImage.setColorFilter(ContextCompat.getColor(context, R.color.primary_dark),
                                          android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        vh.container.setOnClickListener(v -> listener.conversationClicked(conversation));
    }

    public Conversation getItem(int position) {
        return conversations.get(position);
    }

    @Override public int getItemCount() {
        return conversations.size();
    }
}
