package com.dpinciotti.app.rxchat;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dpinciotti.app.rxchat.models.Conversation;
import com.dpinciotti.app.rxchat.models.Message;
import com.dpinciotti.app.rxchat.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter
    extends RecyclerView.Adapter<ConversationAdapter.MessageViewHolder> {

    private static final int TYPE_ME = 1;
    private static final int TYPE_THEM = 2;

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        ImageView personImage;
        TextView message;
        TextView messageDate;

        public MessageViewHolder(View itemView) {
            super(itemView);
            personImage = itemView.findViewById(R.id.person_icon);
            message = itemView.findViewById(R.id.message_text);
            messageDate = itemView.findViewById(R.id.message_date);
        }
    }

    private Context context;
    private List<Message> messages;
    private Conversation conversation;

    public ConversationAdapter(Context context, Conversation conversation) {
        super();
        this.context = context;
        this.conversation = conversation;
        this.messages = new ArrayList<>();
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        messages.add(message);
        // Call this instead of notifyItemInserted so we can update timestamps
        notifyDataSetChanged();
    }

    @Override public int getItemViewType(int position) {
        final Message message = getItem(position);
        return message.isMe() ? TYPE_ME : TYPE_THEM;
    }

    @Override public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View conversationItemView;
        if (viewType == TYPE_ME) {
            conversationItemView = inflater.inflate(R.layout.item_message_me, parent, false);
        } else {
            conversationItemView = inflater.inflate(R.layout.item_message_them, parent, false);
        }
        return new MessageViewHolder(conversationItemView);
    }

    @Override public void onBindViewHolder(MessageViewHolder vh, int position) {
        final Message message = getItem(position);

        vh.message.setText(message.getText());
        if (message.isMe()) {
            vh.message.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
        } else {
            vh.message.setBackgroundColor(ContextCompat.getColor(context, R.color.accent_light));
        }

        if (message.isMe() || conversation.getIconRes() == 0) {
            vh.personImage.setImageResource(R.drawable.ic_profile_default);
            vh.personImage.setColorFilter(ContextCompat.getColor(context, R.color.primary_dark));
        } else {
            vh.personImage.setImageResource(conversation.getIconRes());
        }

        vh.messageDate.setText(TimeUtils.getTimeSpanString(context, message.getSentDate().getTime(), true));
    }

    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override public int getItemCount() {
        return messages.size();
    }
}
