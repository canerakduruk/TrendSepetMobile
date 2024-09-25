package com.example.eticaretapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eticaretapp.adapters.DeliveryRecyclerAdapter;
import com.example.eticaretapp.databinding.ActivityDeliveryBinding;
import com.example.eticaretapp.datamodels.OrderModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.viewmodels.OrderViewModel;

import java.util.List;

public class DeliveryActivity extends BaseActivity {
    private ActivityDeliveryBinding binding;
    private DBHelper dbHelper;
    private OrderViewModel orderViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = (ActivityDeliveryBinding) BindingHelper.attachBindingToContentFrame(this,ActivityDeliveryBinding.inflate(getLayoutInflater()));


        initializeClasses();
        setListeners();
        setDeliveryRecView();
        setMenus();


    }

    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        orderViewModel = new OrderViewModel();
    }

    private void setListeners(){
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void setMenus() {

        navigationHelper.setActionBar("Gelen Siparişler");
        navigationHelper.createBackButtonOnActionBar();

    }


    private void setDeliveryRecView() {
        orderViewModel.getOrdersBySellerId(dbHelper.getActiveUser(), new OrderViewModel.OrderViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<OrderModel> orderList = (List<OrderModel>) object;
                binding.rvDeliveryList.setLayoutManager(new LinearLayoutManager(DeliveryActivity.this));
                binding.rvDeliveryList.setAdapter(new DeliveryRecyclerAdapter(DeliveryActivity.this, orderList));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(DeliveryActivity.this, "Siparişler gelmedi", Toast.LENGTH_SHORT).show();
            }
        });
    }


}