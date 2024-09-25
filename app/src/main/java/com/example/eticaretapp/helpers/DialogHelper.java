package com.example.eticaretapp.helpers;


import android.app.Activity;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.example.eticaretapp.R;
import com.example.eticaretapp.viewmodels.UserViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DialogHelper {

    public static void createFinishDialog(AppCompatActivity activity) {
        DBHelper dbHelper = new DBHelper(activity);
        UserViewModel userViewModel = new ViewModelProvider(activity).get(UserViewModel.class);
        new AlertDialog.Builder(activity)
                .setMessage("Oturumunuzu sonlandırmak istediğinizden emin misiniz ?")
                .setCancelable(false)
                .setNegativeButton("HAYIR", null)
                .setPositiveButton("EVET", (dialogInterface, i) -> {
                    userViewModel.signOut();
                    dbHelper.updateActiveUser("0");
                    userViewModel.getIsLogged().observe(activity, isLogged -> {
                        if (!isLogged) {
                            activity.recreate();
                        }
                    });
                })
                .create()
                .show();
    }

    public static BottomSheetDialog createBottomSheetDialog(Activity activity, ViewBinding binding) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.DialogStyle);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setContentView(binding.getRoot());
        return bottomSheetDialog;
    }
}
