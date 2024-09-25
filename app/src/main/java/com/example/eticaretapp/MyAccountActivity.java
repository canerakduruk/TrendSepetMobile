package com.example.eticaretapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;

import com.bumptech.glide.Glide;
import com.example.eticaretapp.databinding.ActivityMyAccountBinding;
import com.example.eticaretapp.datamodels.UserModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.AccountActionsHelper;
import com.example.eticaretapp.helpers.DialogHelper;
import com.example.eticaretapp.viewmodels.UserViewModel;

public class MyAccountActivity extends BaseActivity {
    private ActivityMyAccountBinding binding;
    private DBHelper dbHelper;
    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = (ActivityMyAccountBinding) BindingHelper.attachBindingToContentFrame(this, ActivityMyAccountBinding.inflate(getLayoutInflater()));

        initializeClasses();
        setListeners();
        setMenus();
        layoutVisibilityController();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAccount();
    }

    private void getAccount() {
        if (!dbHelper.getActiveUser().equals("0")) {
            userViewModel.getIsLoading().observe(this, isLoading -> {
                if (isLoading) {
                    baseProgressIndicator.setVisibility(View.VISIBLE);
                    binding.llMain.setVisibility(View.INVISIBLE);
                } else {
                    baseProgressIndicator.setVisibility(View.INVISIBLE);
                    binding.llMain.setVisibility(View.VISIBLE);
                }
            });
            userViewModel.getUser(dbHelper.getActiveUser(), new UserViewModel.ViewModelCallback() {
                @Override
                public void onSuccess(Object user) {
                    UserModel userInfo = (UserModel) user;
                    binding.tvEmail.setText(userInfo.getEmail());
                    binding.tvFullName.setText(userInfo.getName() + " " + userInfo.getSurname());
                    if (userInfo.getProfileImageUrl() != null)
                        Glide.with(MyAccountActivity.this).load(Uri.parse(userInfo.getProfileImageUrl())).into(binding.ivProfile);
                }

                @Override
                public void onError(Exception e) {
                    Log.d("TAG", "OLMADI: " + e.getMessage());
                }
            });
        }
    }


    private void layoutVisibilityController() {

        if (!dbHelper.getActiveUser().equals("0")) {
            binding.llUserInfo.setVisibility(View.VISIBLE);
            binding.llAccountActions.setVisibility(View.GONE);
            binding.tvExitAccount.setVisibility(View.VISIBLE);
        } else {
            binding.llUserInfo.setVisibility(View.GONE);
            binding.llAccountActions.setVisibility(View.VISIBLE);
            binding.tvExitAccount.setVisibility(View.GONE);
        }
    }


    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        userViewModel = new UserViewModel();
    }

    private void setListeners() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        binding.tvMyAddresses.setOnClickListener(view -> {
            getTargetActivity(MyAddressActivity.class);
        });
        binding.tvOrder.setOnClickListener(view -> {
            getTargetActivity(OrderActivity.class);
        });
        binding.tvLogin.setOnClickListener(view -> {
            AccountActionsHelper.showLoginDialog(this, null);
        });
        binding.tvSignUp.setOnClickListener(view -> {
            AccountActionsHelper.showSignUpDialog(this, null);
        });
        binding.tvAccountInfo.setOnClickListener(view -> {
            getTargetActivity(EditAccountActivity.class);
        });
        binding.tvDelivery.setOnClickListener(view -> {
            getTargetActivity(DeliveryActivity.class);
        });
        binding.tvMyProduct.setOnClickListener(view -> {
            getTargetActivity(MyProductActivity.class);
        });
        binding.tvSetProduct.setOnClickListener(view -> {
            getTargetActivity(ProductActionsActivity.class);
        });
        binding.tvChat.setOnClickListener(view -> {
            getTargetActivity(ChatListActivity.class);
        });
        binding.tvExitAccount.setOnClickListener(view -> {
            DialogHelper.createFinishDialog(this);
        });

    }

    private void getTargetActivity(Class<?> targetActivity) {
        Intent intent = new Intent(MyAccountActivity.this, targetActivity);
        if (isUserLoggedIn()) {
            startActivity(intent);
        } else AccountActionsHelper.showLoginDialog(this, intent);

    }

    private void setMenus() {
        navigationHelper.createBottomMenu();
    }


}