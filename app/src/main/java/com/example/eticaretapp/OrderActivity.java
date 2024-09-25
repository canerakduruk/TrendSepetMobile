package com.example.eticaretapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eticaretapp.adapters.OrderRecyclerAdapter;
import com.example.eticaretapp.databinding.ActivityOrderBinding;
import com.example.eticaretapp.datamodels.OrderModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.viewmodels.OrderViewModel;

import java.util.List;

public class OrderActivity extends BaseActivity {

    private DBHelper dbHelper;
    private ActivityOrderBinding binding;
    private OrderViewModel orderViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = (ActivityOrderBinding) BindingHelper.attachBindingToContentFrame(this, ActivityOrderBinding.inflate(getLayoutInflater()));

        initializeClasses();
        setMenus();
        setRecyclerView();
        setListeners();


    }


    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        orderViewModel = new OrderViewModel();
    }

    private void setMenus() {
        navigationHelper.setActionBar("Siparişlerim");
        navigationHelper.createBackButtonOnActionBar();

    }
    private void setListeners() {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void setRecyclerView() {
        orderViewModel.getOrdersByCustomerId(dbHelper.getActiveUser(), new OrderViewModel.OrderViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<OrderModel> orderList = (List<OrderModel>) object;
                binding.rvOrderList.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                binding.rvOrderList.setAdapter(new OrderRecyclerAdapter(OrderActivity.this, orderList));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(OrderActivity.this, "Siparişler gelmedi", Toast.LENGTH_SHORT).show();
            }
        });

    }


}