package com.example.eticaretapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.adapters.AdressRecyclerAdapter;
import com.example.eticaretapp.adapters.ShoppingCartRecyclerAdapter;
import com.example.eticaretapp.databasemodels.AddressDbModel;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.databasemodels.ShoppingCartDbModel;
import com.example.eticaretapp.databinding.ActivityPaymentBinding;
import com.example.eticaretapp.databinding.BottomsheetlayoutAddressBinding;
import com.example.eticaretapp.datamodels.AddressModel;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.datamodels.ShoppingCartModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.DialogHelper;
import com.example.eticaretapp.viewmodels.AddressViewModel;
import com.example.eticaretapp.viewmodels.OrderViewModel;
import com.example.eticaretapp.viewmodels.ProductViewModel;
import com.example.eticaretapp.viewmodels.ShoppingCartViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PaymentActivity extends BaseActivity {
    private DBHelper dbHelper;
    public BottomSheetDialog addressSelectDialog;
    public ActivityPaymentBinding binding;
    private ProductViewModel productViewModel;
    private ShoppingCartViewModel shoppingCartViewModel;
    private List<ProductModel> productModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = (ActivityPaymentBinding) BindingHelper.attachBindingToContentFrame(this, ActivityPaymentBinding.inflate(getLayoutInflater()));

        initializeClasses();
        setListeners();

        int totalPrice = getIntent().getIntExtra("totalPrice", 0);
        binding.tvTotalPrice.setText(String.valueOf(totalPrice));
        getProductsFromCart();

        productViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                baseProgressIndicator.setVisibility(View.VISIBLE);
            } else {
                baseProgressIndicator.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();

        if (addressSelectDialog != null && addressSelectDialog.isShowing()) {
            addressSelectDialog.dismiss();
        }
    }


    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        productViewModel = new ProductViewModel();
        shoppingCartViewModel = new ShoppingCartViewModel();
    }

    private void setListeners() {

        binding.tvSelectAddress.setOnClickListener(view -> {
            showDialog();
        });
        binding.btnPay.setOnClickListener(view -> {
            setOrder();
        });
    }

    private void getProductsFromCart() {
        shoppingCartViewModel.getShoppingCartByCustomerId(dbHelper.getActiveUser(), new ShoppingCartViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<ShoppingCartModel> shoppingCartModelList = (List<ShoppingCartModel>) object;

                if (!shoppingCartModelList.isEmpty()) {
                    List<String> productIdList = new ArrayList<>();
                    for (ShoppingCartModel shoppingCartModel : shoppingCartModelList) {
                        productIdList.add(shoppingCartModel.getProductId());
                    }
                    productViewModel.getProductsById(productIdList, new ProductViewModel.ViewModelCallback() {
                        @Override
                        public void onSuccess(Object object) {
                            productModelList = (List<ProductModel>) object;
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(PaymentActivity.this, "Ürün bilgileri gelmedi", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(PaymentActivity.this, "Kart gelmedi", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setOrder() {

        OrderViewModel orderViewModel = new OrderViewModel();
        String address = binding.tvSelectAddress.getText().toString();
        String customerID = dbHelper.getActiveUser();
        String sellerID;
        Timestamp date = Timestamp.now();


        for (ProductModel productModel : productModelList) {

            HashMap<String, String> productInfo = new HashMap<>();
            productInfo.put(ProductDbModel.ID, productModel.getId());
            productInfo.put(ProductDbModel.NAME, productModel.getName());
            productInfo.put(ProductDbModel.DESCRIPTION, productModel.getDescription());
            productInfo.put(ProductDbModel.CATEGORY, productModel.getCategory());
            productInfo.put(ProductDbModel.PRICE, productModel.getPrice());
            productInfo.put(ProductDbModel.KDV_RATE, productModel.getKdvRate());
            productInfo.put(ProductDbModel.IMAGE, productModel.getImageUrl());

            sellerID = productModel.getOwnerId();

            orderViewModel.addOrder(productInfo, address, customerID, sellerID, date, "0", new OrderViewModel.OrderViewModelCallback() {
                @Override
                public void onSuccess(Object object) {
                    String productId = (String) object;
                    shoppingCartViewModel.clearShoppingCart(dbHelper.getActiveUser(), productId, new ShoppingCartViewModel.ViewModelCallback() {
                        @Override
                        public void onSuccess(Object object) {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                }

                @Override
                public void onError(Exception e) {

                }
            });

        }

    }


    private void showDialog() {
        BottomsheetlayoutAddressBinding addressBinding = BottomsheetlayoutAddressBinding.inflate(getLayoutInflater());
        addressSelectDialog = DialogHelper.createBottomSheetDialog(this, addressBinding);

        setRecyclerView(addressBinding.rvAddressList);
        addressBinding.tvAddAddress.setOnClickListener(view -> {
            Intent intent = new Intent(PaymentActivity.this, AddressActionsActivity.class);
            startActivity(intent);
            addressSelectDialog.dismiss();
        });


        addressSelectDialog.show();

    }

    private void setRecyclerView(RecyclerView recyclerView) {

        AddressViewModel addressViewModel = new AddressViewModel();
        addressViewModel.getMyAddresses(dbHelper.getActiveUser(), new AddressViewModel.AddressViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<AddressModel> addressList = (List<AddressModel>) object;
                System.out.println(addressList.size());
                recyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                recyclerView.setAdapter(new AdressRecyclerAdapter(PaymentActivity.this, addressList));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(PaymentActivity.this, "Adresler gelmedi", Toast.LENGTH_SHORT).show();
            }
        });


    }
}