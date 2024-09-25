package com.example.eticaretapp.helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.eticaretapp.ProductActionsActivity;
import com.example.eticaretapp.databinding.BottomsheetlayoutLoginBinding;
import com.example.eticaretapp.databinding.BottomsheetlayoutSignupBinding;
import com.example.eticaretapp.datamodels.UserModel;
import com.example.eticaretapp.viewmodels.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

public class AccountActionsHelper {
    public static void showLoginDialog(Activity activity, Intent intent) {
        BottomsheetlayoutLoginBinding binding = BottomsheetlayoutLoginBinding.inflate(activity.getLayoutInflater());
        BottomSheetDialog bottomSheetDialog = DialogHelper.createBottomSheetDialog(activity, binding);

        UserViewModel userViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(UserViewModel.class);
        DBHelper dbHelper = new DBHelper(activity);

        loadingController(userViewModel, (LifecycleOwner) activity, binding.linearProgressIndicator);

        binding.bottomsheetlayoutLoginTxtlogin.setOnClickListener(view -> {

            String username = binding.bottomsheetlayoutLoginEdtusername.getText().toString();
            String password = binding.bottomsheetlayoutLoginEdtpassword.getText().toString();


            if (TextHelper.isNullOrEmpty(username, password)) {
                Toast.makeText(activity, "Lütfen kullanıcı bilgilerinizi giriniz.", Toast.LENGTH_SHORT).show();
                return;
            }


            userViewModel.loginUser(username, password, new UserViewModel.ViewModelCallback() {
                @Override
                public void onSuccess(Object userId) {
                    dbHelper.updateActiveUser((String) userId);
                    Toast.makeText(activity, "Başarılı", Toast.LENGTH_SHORT).show();
                    startIntent(activity, intent);
                    bottomSheetDialog.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(activity, "Başarısız:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });


        binding.bottomsheetlayoutLoginTxtsignup.setOnClickListener(view -> {
            showSignUpDialog(activity, intent);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();

    }

    public static void showSignUpDialog(Activity activity, Intent intent) {
        BottomsheetlayoutSignupBinding binding = BottomsheetlayoutSignupBinding.inflate(activity.getLayoutInflater());
        BottomSheetDialog bottomSheetDialog = DialogHelper.createBottomSheetDialog(activity, binding);

        UserViewModel userViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(UserViewModel.class);
        DBHelper dbHelper = new DBHelper(activity);

        loadingController(userViewModel, (LifecycleOwner) activity, binding.linearProgressIndicator);

        binding.bottomsheetlayoutSignupTxtsignup.setOnClickListener(view -> {

            String password = binding.bottomsheetlayoutSignupEdtpassword.getText().toString();
            String email = binding.bottomsheetlayoutSignupEdtemail.getText().toString();
            String name = binding.bottomsheetlayoutSignupName.getText().toString();
            String surname = binding.bottomsheetlayoutSignupLastname.getText().toString();
            String phoneNumber = binding.bottomsheetlayoutSignupPhonenumber.getText().toString();
            RadioButton radSelectedGender = bottomSheetDialog.findViewById(binding.bottomsheetlayoutSignupRadiogroup.getCheckedRadioButtonId());
            String gender;


            if (radSelectedGender != null)
                gender = radSelectedGender.getText().toString();
            else {
                gender = null;
            }

            if (TextHelper.isNullOrEmpty(password, email, name, surname, gender, phoneNumber)) {
                Toast.makeText(activity, "Lütfen kullanıcı bilgilerinizi giriniz.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextHelper.containSpace(password, email, name, surname, gender, phoneNumber)) {
                Toast.makeText(activity, "Lütfen boşluk içermeyen kullanıcı bilgilerinizi giriniz.", Toast.LENGTH_SHORT).show();
                return;
            }

            userViewModel.registerUser(null, email, password, name, surname, gender, phoneNumber, new UserViewModel.ViewModelCallback() {
                @Override
                public void onSuccess(Object userId) {
                    Toast.makeText(activity, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                    userViewModel.loginUser(email, password, new UserViewModel.ViewModelCallback() {
                        @Override
                        public void onSuccess(Object userId) {
                            dbHelper.updateActiveUser((String) userId);
                            Toast.makeText(activity, "Başarılı", Toast.LENGTH_SHORT).show();
                            startIntent(activity, intent);
                            bottomSheetDialog.dismiss();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(activity, "Kayıt Hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        binding.bottomsheetlayoutSignupLogin.setOnClickListener(view -> {
            showLoginDialog(activity, intent);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();

    }

    private static void loadingController(UserViewModel userViewModel, LifecycleOwner activity, ProgressBar binding) {
        userViewModel.getIsLoading().observe(activity, isLoading -> {
            if (isLoading) {
                binding.setVisibility(View.VISIBLE);
            } else {
                binding.setVisibility(View.GONE);
            }
        });
    }

    private static void startIntent(Activity activity, Intent intent) {
        if (intent == null)
            activity.recreate();
        else {
            activity.recreate();
            activity.startActivity(intent);
        }
    }


}