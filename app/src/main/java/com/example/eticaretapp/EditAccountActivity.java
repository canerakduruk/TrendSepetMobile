package com.example.eticaretapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.example.eticaretapp.databasemodels.UserDbModel;
import com.example.eticaretapp.databinding.ActivityEditAccountBinding;
import com.example.eticaretapp.databinding.BottomsheetlayoutPasswordchangeBinding;
import com.example.eticaretapp.datamodels.UserModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.DialogHelper;
import com.example.eticaretapp.helpers.TextHelper;
import com.example.eticaretapp.viewmodels.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EditAccountActivity extends BaseActivity {

    private ActivityEditAccountBinding binding;
    private DBHelper dbHelper;
    private UserViewModel userViewModel;
    private ActivityResultLauncher<Intent> resultLauncher;
    private String currentProfileUri;
    private String newProfileUri;
    private HashMap<String, Object> currentUserInfo;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = (ActivityEditAccountBinding) BindingHelper.attachBindingToContentFrame(this, ActivityEditAccountBinding.inflate(getLayoutInflater()));

        setClasses();
        setListeners();
        setMenus();
        getInfos();
        registerResult();

        userViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                baseProgressIndicator.setVisibility(View.VISIBLE);
            } else {
                baseProgressIndicator.setVisibility(View.GONE);
            }
        });
    }


    private void setClasses() {
        dbHelper = new DBHelper(this);
        userViewModel = new UserViewModel();
    }

    private void setListeners() {
        binding.tvSave.setOnClickListener(view -> {
            updateInfos();
        });
        binding.tvChangePassword.setOnClickListener(view -> {
            createPasswordChangeDialog();
        });
        binding.ivProfileImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            resultLauncher.launch(intent);
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }


    private void setMenus() {
        navigationHelper.setActionBar("Hesap Bilgilerim");
        navigationHelper.createBackButtonOnActionBar();
    }


    private void getInfos() {

        userViewModel.getUser(dbHelper.getActiveUser(), new UserViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object map) {
                UserModel userInfo = (UserModel) map;
                binding.etName.setText(userInfo.getName());
                binding.etSurname.setText(userInfo.getSurname());
                if (userInfo.getGender().equals("Erkek"))
                    binding.rbMale.setChecked(true);
                else binding.rbFemale.setChecked(true);
                binding.etEmail.setText(userInfo.getEmail());
                binding.etPhoneNumber.setText(userInfo.getPhoneNumber());
                currentProfileUri = userInfo.getProfileImageUrl();
                currentUserInfo = userInfo.toMap();
                if (currentProfileUri != null)
                    Glide.with(EditAccountActivity.this).load(currentProfileUri).into(binding.ivProfileImage);

            }

            @Override
            public void onError(Exception e) {

            }
        });


    }

    private void updateInfos() {

        String name = binding.etName.getText().toString();
        String surname = binding.etSurname.getText().toString();
        String email = binding.etEmail.getText().toString();
        String phoneNumber = binding.etPhoneNumber.getText().toString();
        String profileImageUrl = currentProfileUri;
        String gender = null;


        RadioButton radioBtnSelectedGender = findViewById(binding.rgGender.getCheckedRadioButtonId());
        if (radioBtnSelectedGender != null) {
            gender = radioBtnSelectedGender.getText().toString();
        }
        if (TextHelper.containSpace(name, surname, email, phoneNumber)) {
            Toast.makeText(this, "Lütfen boşluk içermeyen alanları doldurunuz.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextHelper.isNullOrEmpty(name, surname, email, phoneNumber, gender)){
            Toast.makeText(this, "Lütfen bütün alanları doldurunuz.", Toast.LENGTH_SHORT).show();
            return;
        }


        HashMap<String, Object> newUserInfo = new HashMap<>();
        newUserInfo.put(UserDbModel.NAME, name);
        newUserInfo.put(UserDbModel.SURNAME, surname);
        newUserInfo.put(UserDbModel.EMAIL, email);
        newUserInfo.put(UserDbModel.PHONE_NUMBER, phoneNumber);
        newUserInfo.put(UserDbModel.IMAGE, profileImageUrl);
        newUserInfo.put(UserDbModel.GENDER, gender);

        Log.d("TAG", "updateInfos: " + currentUserInfo + " " + newUserInfo);
        for (String key : currentUserInfo.keySet()) {
            if (newUserInfo.containsKey(key)) {
                if (newUserInfo.get(key) == null) {
                    newUserInfo.remove(key);
                } else if (Objects.equals(currentUserInfo.get(key), newUserInfo.get(key))) {
                    newUserInfo.remove(key);
                }
            }
        }


        Log.d("TAG", "updateInfos: " + newUserInfo);

        if (!newUserInfo.isEmpty()) {
            userViewModel.updateUserData(dbHelper.getActiveUser(), newUserInfo, new UserViewModel.ViewModelCallback() {
                @Override
                public void onSuccess(Object object) {
                    currentUserInfo = newUserInfo;
                    Toast.makeText(EditAccountActivity.this, "Bilgileriniz başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(EditAccountActivity.this, "Bilgileriniz güncellenemedi lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Değişiklik yok", Toast.LENGTH_SHORT).show();
        }


    }

    private void createPasswordChangeDialog() {
        BottomsheetlayoutPasswordchangeBinding passwordChangeBinding = BottomsheetlayoutPasswordchangeBinding.inflate(this.getLayoutInflater());
        BottomSheetDialog passwordChangeDialog = DialogHelper.createBottomSheetDialog(this, passwordChangeBinding);


        passwordChangeBinding.tvAccept.setOnClickListener(view -> {
            String currentPassword = passwordChangeBinding.etOldPassword.getText().toString();
            String newPassword = passwordChangeBinding.etNewPassword.getText().toString();

            if (TextHelper.isNullOrEmpty(currentPassword, newPassword)) {
                Toast.makeText(this, "Lütfen tüm alanları doldurunuz.", Toast.LENGTH_SHORT).show();
            } else {
                userViewModel.changePassword(currentPassword, newPassword, new UserViewModel.ViewModelCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        Toast.makeText(EditAccountActivity.this, "Şifreniz başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                        passwordChangeDialog.dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(EditAccountActivity.this, "Şifreniz güncellenemedi lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        passwordChangeBinding.tvForgotPassword.setOnClickListener(view -> {
            userViewModel.resetPassword(new UserViewModel.ViewModelCallback() {
                @Override
                public void onSuccess(Object object) {
                    Toast.makeText(EditAccountActivity.this, "Şifre yenileme linki emailinize gönderildi.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(EditAccountActivity.this, "Şifre yenileme linki gönderilemedi.", Toast.LENGTH_SHORT).show();
                }
            });
        });


        passwordChangeDialog.show();
    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        Uri imageUri = result.getData().getData();
                        binding.ivProfileImage.setImageURI(imageUri);
                        currentProfileUri = imageUri.toString();
                    } catch (Exception e) {
                        Toast.makeText(EditAccountActivity.this, "Fotoğraf Seçilmedi", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}