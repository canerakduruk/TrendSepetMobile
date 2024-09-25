package com.example.eticaretapp.helpers;

import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.example.eticaretapp.R;

public class BindingHelper {

    public static ViewBinding attachBindingToContentFrame(AppCompatActivity activity,ViewBinding binding) {
        FrameLayout contentFrame = activity.findViewById(R.id.flContent);
        contentFrame.removeAllViews();
        contentFrame.addView(binding.getRoot());
        return binding;
    }
}
