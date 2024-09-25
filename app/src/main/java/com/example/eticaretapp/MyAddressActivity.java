package com.example.eticaretapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eticaretapp.adapters.AdressRecyclerAdapter;
import com.example.eticaretapp.databinding.ActivityMyAddressBinding;
import com.example.eticaretapp.datamodels.AddressModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.viewmodels.AddressViewModel;

import java.util.List;

public class MyAddressActivity extends BaseActivity {

    private DBHelper dbHelper;
    private ActivityMyAddressBinding binding;
    private AddressViewModel addressViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = (ActivityMyAddressBinding) BindingHelper.attachBindingToContentFrame(this, ActivityMyAddressBinding.inflate(getLayoutInflater()));

        initializeClasses();
        setListeners();
        setMenus();

    }



    @Override
    protected void onResume() {
        super.onResume();
        getAddresses();

    }

    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        addressViewModel = new AddressViewModel();
    }

    private void setListeners() {
        binding.tvAddAddress.setOnClickListener(view -> {
            Intent intent = new Intent(MyAddressActivity.this, AddressActionsActivity.class);
            startActivity(intent);
        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void setMenus() {
        navigationHelper.setActionBar("Adreslerim");
        navigationHelper.createBackButtonOnActionBar();
    }

    private void setRecyclerView(List<AddressModel> addressList) {

        binding.rvAddressList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAddressList.setAdapter(new AdressRecyclerAdapter(this, addressList));

    }
    private void getAddresses() {
        addressViewModel.getMyAddresses(dbHelper.getActiveUser(), new AddressViewModel.AddressViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<AddressModel> addressModelList = (List<AddressModel>) object;

                if (!addressModelList.isEmpty()) {
                    setRecyclerView(addressModelList);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MyAddressActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}