package com.example.emsismartpresence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageText.setText(message.getText());
        holder.timestamp.setText(message.getFormattedTimestamp());

        // Configurer l'apparence selon le type de message
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.messageContainer.getLayoutParams();
        if (message.getType() == Message.TYPE_USER) {
            // Message utilisateur : gris, aligné à droite
            holder.messageContainer.setBackgroundResource(R.drawable.user_message_background);
            params.startToStart = ConstraintLayout.LayoutParams.UNSET;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            holder.messageText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        } else {
            // Message assistant : vert, aligné à gauche
            holder.messageContainer.setBackgroundResource(R.drawable.assistant_message_background);
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            holder.messageText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }
        holder.messageContainer.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestamp;
        LinearLayout messageContainer;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            timestamp = itemView.findViewById(R.id.message_timestamp);
            messageContainer = itemView.findViewById(R.id.message_container);
        }
    }
}