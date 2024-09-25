package com.example.eticaretapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.eticaretapp.adapters.ShoppingCartRecyclerAdapter;
import com.example.eticaretapp.databinding.ActivityShoppingCardBinding;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.datamodels.ShoppingCartModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.AccountActionsHelper;
import com.example.eticaretapp.viewmodels.ProductViewModel;
import com.example.eticaretapp.viewmodels.ShoppingCartViewModel;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends BaseActivity {
    public ActivityShoppingCardBinding binding;
    private ShoppingCartViewModel shoppingCartViewModel;
    private ProductViewModel productViewModel;
    private DBHelper dbHelper;

    public int totalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = (ActivityShoppingCardBinding) BindingHelper.attachBindingToContentFrame(this, ActivityShoppingCardBinding.inflate(getLayoutInflater()));


        initializeClasses();
        setListeners();
        setMenus();


    }
    @Override
    protected void onResume() {
        super.onResume();
        totalPrice = 0;
        setCartRecView();

    }

    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        shoppingCartViewModel = new ShoppingCartViewModel();
        productViewModel = new ProductViewModel();
    }


    private void setListeners() {
        binding.btnCompleteShopping.setOnClickListener(view -> {
            if (isUserLoggedIn()) {
                Intent intent = new Intent(ShoppingCartActivity.this, PaymentActivity.class);
                intent.putExtra("totalPrice",totalPrice);
                startActivity(intent);
                return;
            }
            new AlertDialog.Builder(ShoppingCartActivity.this)
                    .setMessage("Lütfen oturum açınız")
                    .setCancelable(false)
                    .setPositiveButton("KAYIT OL", (dialogInterface, i) -> {
                        AccountActionsHelper.showSignUpDialog(this, null);
                    })
                    .setNegativeButton("GİRİŞ", (dialogInterface, i) -> {
                        AccountActionsHelper.showLoginDialog(this, null);
                    })
                    .create()
                    .show();

        });
    }

    private void setMenus() {
        navigationHelper.setActionBar("");
        navigationHelper.createBackButtonOnActionBar();
    }


    private void setCartRecView() {
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
                            List<ProductModel> productModelList = (List<ProductModel>) object;
                            binding.rvCartList.setLayoutManager(new GridLayoutManager(ShoppingCartActivity.this, 2));
                            binding.rvCartList.setAdapter(new ShoppingCartRecyclerAdapter(ShoppingCartActivity.this, productModelList));
                            shoppingCardEmptyController(productModelList.size());

                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(ShoppingCartActivity.this, "Ürün bilgileri gelmedi", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ShoppingCartActivity.this, "Kart gelmedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void shoppingCardEmptyController(int size) {

        if (size != 0) {
            binding.rvCartList.setVisibility(View.VISIBLE);
            binding.llEmpty.setVisibility(View.GONE);
            binding.bottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            binding.rvCartList.setVisibility(View.GONE);
            binding.llEmpty.setVisibility(View.VISIBLE);
            binding.bottomNavigationView.setVisibility(View.GONE);
        }


    }


}