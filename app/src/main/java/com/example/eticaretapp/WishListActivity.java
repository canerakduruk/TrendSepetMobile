package com.example.eticaretapp;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.adapters.WishListRecyclerAdapter;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.databasemodels.WishListDbModel;
import com.example.eticaretapp.databinding.ActivityWishListBinding;
import com.example.eticaretapp.datamodels.WishListModel;
import com.example.eticaretapp.helpers.AccountActionsHelper;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.viewmodels.WishListViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WishListActivity extends BaseActivity {
    private ActivityWishListBinding binding;

    private DBHelper dbHelper;

    private WishListViewModel wishListViewModel;
    private List<WishListModel> wishList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = (ActivityWishListBinding) BindingHelper.attachBindingToContentFrame(this, ActivityWishListBinding.inflate(getLayoutInflater()));

        initializeClasses();
        wishListViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading){
                baseProgressIndicator.setVisibility(View.VISIBLE);
                binding.main.setVisibility(View.INVISIBLE);
            }else {
                baseProgressIndicator.setVisibility(View.INVISIBLE);
                binding.main.setVisibility(View.VISIBLE);
            }
        });
        setMenus();
        setWishListRecView();



    }
    private void initializeClasses() {
        wishListViewModel = new WishListViewModel();
        dbHelper = new DBHelper(this);
    }

    private void setMenus() {
        navigationHelper.createBottomMenu();
    }

    private void setWishListRecView() {

        wishListViewModel.getWishList(dbHelper.getActiveUser(), new WishListViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                wishList = (List<WishListModel>) object;
                binding.rvWishList.setLayoutManager(new LinearLayoutManager(WishListActivity.this));
                binding.rvWishList.setAdapter(new WishListRecyclerAdapter(WishListActivity.this,wishList));
                visibilityController();
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    public void visibilityController() {
        if (!dbHelper.getActiveUser().equals("0")) {
            binding.txtLogin.setVisibility(View.GONE);
            if (wishList.isEmpty()) {
                binding.llEmpty.setVisibility(View.VISIBLE);
                binding.rvWishList.setVisibility(View.GONE);

            } else {
                binding.rvWishList.setVisibility(View.VISIBLE);
                binding.llEmpty.setVisibility(View.GONE);
            }

        } else {
            binding.txtLogin.setVisibility(View.VISIBLE);
            binding.rvWishList.setVisibility(View.GONE);
            binding.llEmpty.setVisibility(View.VISIBLE);
            binding.txtLogin.setOnClickListener(view -> AccountActionsHelper.showLoginDialog(this, null));
        }


    }
}