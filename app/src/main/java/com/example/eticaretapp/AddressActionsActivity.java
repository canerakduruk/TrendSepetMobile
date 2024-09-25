package com.example.eticaretapp;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.adapters.CityRecyclerAdapter;
import com.example.eticaretapp.databasemodels.AddressDbModel;
import com.example.eticaretapp.databasemodels.DistrictDbModel;
import com.example.eticaretapp.databasemodels.UserDbModel;
import com.example.eticaretapp.databinding.ActivityAddressActionsBinding;
import com.example.eticaretapp.databinding.BottomsheetlayoutCityanddistrictBinding;
import com.example.eticaretapp.datamodels.AddressModel;
import com.example.eticaretapp.datamodels.CityModel;
import com.example.eticaretapp.datamodels.DistrictModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.DialogHelper;
import com.example.eticaretapp.helpers.TextHelper;
import com.example.eticaretapp.viewmodels.AddressViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.List;

public class AddressActionsActivity extends BaseActivity {
    public ActivityAddressActionsBinding binding;
    private AddressViewModel addressViewModel;
    private DBHelper dbHelper;
    public BottomSheetDialog addressSelectDialog;
    public String selectedCityId, selectedDistrictId;
    private String addressId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = (ActivityAddressActionsBinding) BindingHelper.attachBindingToContentFrame(this, ActivityAddressActionsBinding.inflate(getLayoutInflater()));

        initializeClasses();
        setListeners();
        setMenus();

        addressId = getIntent().getStringExtra(AddressDbModel.ID);

        if (addressId != null)
            getAddress();

    }


    private void getAddress() {

        addressViewModel.getMyAddress(addressId, new AddressViewModel.AddressViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                AddressModel addressModel = (AddressModel) object;
                binding.etTitle.setText(addressModel.getTitle());
                binding.tvCity.setText(addressModel.getCity());
                binding.tvDistrict.setText(addressModel.getDistrict());
                binding.etAddressDetail.setText(addressModel.getDetail());
                selectedCityId = addressModel.getCityId();
                selectedDistrictId = addressModel.getDistrictId();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AddressActionsActivity.this, "Adresiniz getirilirken hata oluştu lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        addressViewModel = new AddressViewModel();
    }

    private void setListeners() {
        binding.tvCity.setOnClickListener(view -> {
            showCityAndDistrictDialog(true);
        });

        binding.tvDistrict.setOnClickListener(view -> {
            if (!binding.tvCity.getText().toString().isEmpty())
                showCityAndDistrictDialog(false);
            else
                Toast.makeText(this, "Lütfen il seçiniz.", Toast.LENGTH_SHORT).show();
        });

        binding.tvSave.setOnClickListener(view -> {
            saveAddress();
        });


    }

    private void setMenus() {
        navigationHelper.setActionBar("Adresi Düzenle");
        navigationHelper.createBackButtonOnActionBar();
    }

    private void saveAddress() {
        AddressModel addressModel = new AddressModel();
        addressModel.setTitle(binding.etTitle.getText().toString());
        addressModel.setCityId(selectedCityId);
        addressModel.setDistrictId(selectedDistrictId);
        addressModel.setDetail(binding.etAddressDetail.getText().toString());
        addressModel.setOwnerId(dbHelper.getActiveUser());

        if(TextHelper.containSpace(binding.etTitle.getText().toString(),binding.etAddressDetail.getText().toString())){
            Toast.makeText(this, "Lütfen boşluk kullanmayınız", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextHelper.isNullOrEmpty(binding.etTitle.getText().toString(),binding.tvCity.getText().toString(),binding.tvDistrict.getText().toString(),binding.etAddressDetail.getText().toString())){
            Toast.makeText(this, "Lütfen boş alan bırakmayınız", Toast.LENGTH_SHORT).show();
            return;
        }

        if (addressId == null) {
            addressViewModel.saveAddress(addressModel, new AddressViewModel.AddressViewModelCallback() {
                @Override
                public void onSuccess(Object object) {
                    Toast.makeText(AddressActionsActivity.this,"Adres başarıyla eklendi." , Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(AddressActionsActivity.this, "Adres eklerken hata oluştu.", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            HashMap<String,Object> map = addressModel.toMap();
            addressViewModel.updateAddress(map,addressId, new AddressViewModel.AddressViewModelCallback() {
                @Override
                public void onSuccess(Object object) {
                    Toast.makeText(AddressActionsActivity.this, "Adres başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                    finish();

                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(AddressActionsActivity.this, "Adres güncellenirken hata oluştu.", Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    private void showCityAndDistrictDialog(boolean isCity) {
        BottomsheetlayoutCityanddistrictBinding binding = BottomsheetlayoutCityanddistrictBinding.inflate(getLayoutInflater());
        addressSelectDialog = DialogHelper.createBottomSheetDialog(this, binding);

        RecyclerView rvCityAndDistrict = binding.rvCityAndDistrict;
        setRecyclerCityAndDistrict(rvCityAndDistrict, isCity);
        addressSelectDialog.show();

    }

    private void setRecyclerCityAndDistrict(RecyclerView recyclerView, boolean isCity) {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (isCity)
            addressViewModel.getCities(new AddressViewModel.AddressViewModelCallback() {
                @Override
                public void onSuccess(Object object) {
                    List<CityModel> cityList = (List<CityModel>) object;
                    recyclerView.setAdapter(new CityRecyclerAdapter(AddressActionsActivity.this, cityList));
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(AddressActionsActivity.this, "Şehirler yüklenemedi.", Toast.LENGTH_SHORT).show();
                }
            });
        else {
            if (selectedCityId != null || !selectedCityId.isEmpty())
                addressViewModel.getDistricts(selectedCityId, new AddressViewModel.AddressViewModelCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        List<DistrictModel> districtList = (List<DistrictModel>) object;
                        recyclerView.setAdapter(new DistrictRecyclerAdapter(AddressActionsActivity.this, districtList));
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(AddressActionsActivity.this, "İlçeler yüklenemedi.", Toast.LENGTH_SHORT).show();
                    }
                });
        }

    }


}