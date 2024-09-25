package com.example.eticaretapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eticaretapp.adapters.ChatRecyclerAdapter;
import com.example.eticaretapp.datamodels.ChatModel;
import com.example.eticaretapp.databasemodels.ChatDbModel;
import com.example.eticaretapp.databinding.ActivityChatBinding;
import com.example.eticaretapp.datamodels.UserModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.example.eticaretapp.viewmodels.ChatViewModel;
import com.example.eticaretapp.viewmodels.UserViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private UserViewModel userViewModel;
    private ChatViewModel chatViewModel;
    private List<ChatModel> chatModelList;
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private DBHelper dbHelper;
    private String senderId, receiverId;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = (ActivityChatBinding) BindingHelper.attachBindingToContentFrame(this, ActivityChatBinding.inflate(getLayoutInflater()));
        initializeClasses();

        receiverId = getIntent().getStringExtra(ChatDbModel.RECEIVER_ID);
        senderId = dbHelper.getActiveUser();

        chatModelList = new ArrayList<>();
        chatRecyclerAdapter = new ChatRecyclerAdapter(chatModelList, dbHelper.getActiveUser());

        setChatRecView();
        setListeners();
        setMenus();
        listenMessages();

    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatModelList.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatModel chatModel = new ChatModel();
                    chatModel.setSenderId(documentChange.getDocument().getString(ChatDbModel.SENDER_ID));
                    chatModel.setReceiverId(documentChange.getDocument().getString(ChatDbModel.RECEIVER_ID));
                    chatModel.setMessage(documentChange.getDocument().getString(ChatDbModel.MESSAGE));
                    chatModel.setDateTime(documentChange.getDocument().getTimestamp(ChatDbModel.DATE));
                    chatModelList.add(chatModel);
                }

            }
            Collections.sort(chatModelList, Comparator.comparing(ChatModel::getDateTime));
            if (count == 0) {
                chatRecyclerAdapter.notifyDataSetChanged();
            } else {
                chatRecyclerAdapter.notifyItemRangeChanged(chatModelList.size(), chatModelList.size());
                binding.rvChat.scrollToPosition(chatModelList.size() - 1);
            }
        }
    };

    private void listenMessages() {
        firebaseHelper.getCollection(ChatDbModel.TABLE)
                .whereEqualTo(ChatDbModel.SENDER_ID, senderId)
                .whereEqualTo(ChatDbModel.RECEIVER_ID, receiverId).addSnapshotListener(eventListener);

        firebaseHelper.getCollection(ChatDbModel.TABLE)
                .whereEqualTo(ChatDbModel.SENDER_ID, receiverId)
                .whereEqualTo(ChatDbModel.RECEIVER_ID, senderId).addSnapshotListener(eventListener);
    }


    private void setListeners() {
        binding.tvSend.setOnClickListener(view -> sendMessage());
    }

    private void setMenus() {
        navigationHelper.setActionBar("receiverName");
        navigationHelper.createBackButtonOnActionBar();
    }

    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        userViewModel = new UserViewModel();
        chatViewModel = new ChatViewModel();
        firebaseHelper = new FirebaseHelper();
    }

    private void setChatRecView() {
        userViewModel.getUser(receiverId, new UserViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                UserModel userModel = (UserModel) object;
                binding.rvChat.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                binding.rvChat.setAdapter(chatRecyclerAdapter);
                binding.rvChat.scrollToPosition(chatModelList.size() - 1);
                navigationHelper.setActionBar(userModel.getName() +" "+ userModel.getSurname());
                navigationHelper.createBackButtonOnActionBar();


            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    private void sendMessage() {

        Timestamp timestamp = Timestamp.now();
        String message = binding.tvMessageInput.getText().toString();

        chatViewModel.sendMessage(message, senderId, receiverId, timestamp, new ChatViewModel.ChatViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(ChatActivity.this, "Gönderildi", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ChatActivity.this, "Gönderilemedi", Toast.LENGTH_SHORT).show();

            }
        });
        binding.tvMessageInput.setText(null);


    }



}