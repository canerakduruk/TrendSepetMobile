package com.example.eticaretapp;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eticaretapp.adapters.ChatUserListAdapter;
import com.example.eticaretapp.databinding.ActivityChatUserListBinding;
import com.example.eticaretapp.datamodels.UserModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.viewmodels.ChatViewModel;
import com.example.eticaretapp.viewmodels.UserViewModel;

import java.util.List;
import java.util.Set;

public class ChatListActivity extends BaseActivity {

    private ActivityChatUserListBinding binding;
    private UserViewModel userViewModel;
    private ChatViewModel chatViewModel;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = (ActivityChatUserListBinding) BindingHelper.attachBindingToContentFrame(this,ActivityChatUserListBinding.inflate(getLayoutInflater()));

        initializeClasses();
        setChatListRecView();
        setMenus();

    }

    private void setMenus() {
        navigationHelper.setActionBar("Mesajlarım");
        navigationHelper.createBackButtonOnActionBar();
    }

    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        userViewModel = new UserViewModel();
        chatViewModel = new ChatViewModel();
    }

    private void setChatListRecView() {

        chatViewModel.getChatList(dbHelper.getActiveUser(), new ChatViewModel.ChatViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                userViewModel.getUsers((Set<String>) object, new UserViewModel.ViewModelCallback() {

                    @Override
                    public void onSuccess(Object object) {
                        List<UserModel> userList = (List<UserModel>) object;
                        binding.rvChatUserList.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));
                        binding.rvChatUserList.setAdapter(new ChatUserListAdapter(ChatListActivity.this, userList));
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });


    }
}