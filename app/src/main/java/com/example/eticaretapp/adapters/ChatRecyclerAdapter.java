package com.example.eticaretapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.datamodels.ChatModel;
import com.example.eticaretapp.databinding.ItemRecyclerMessagereceivedBinding;
import com.example.eticaretapp.databinding.ItemRecyclerMessagesentBinding;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final List<ChatModel> chatModels;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatRecyclerAdapter(List<ChatModel> chatModels, String senderId) {
        this.chatModels = chatModels;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemRecyclerMessagesentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new ReceivedMessageViewHolder(
                    ItemRecyclerMessagereceivedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatModels.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatModels.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    public static String getReadableDateTime(Timestamp date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return dateFormat.format(date.toDate());

    }

    @Override
    public int getItemViewType(int position) {
        if (chatModels.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemRecyclerMessagesentBinding binding;

        public SentMessageViewHolder(ItemRecyclerMessagesentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ChatModel chatModel) {
            binding.itemRecyclerMessagesentContent.setText(chatModel.getMessage());
            binding.itemRecyclerMessagesentDate.setText(getReadableDateTime(chatModel.getDateTime()));
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemRecyclerMessagereceivedBinding binding;

        public ReceivedMessageViewHolder(ItemRecyclerMessagereceivedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ChatModel chatModel) {
            binding.itemRecyclerMessagereceivedContent.setText(chatModel.getMessage());
            binding.itemRecyclerMessagereceivedDate.setText(getReadableDateTime(chatModel.getDateTime()));


        }
    }
}
