package com.example.eticaretapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.eticaretapp.adapters.MyProductRecyclerAdapter;
import com.example.eticaretapp.databinding.ActivityMyProductBinding;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.viewmodels.ProductViewModel;

import java.util.List;

public class MyProductActivity extends BaseActivity {
    private DBHelper dbHelper;
    private ActivityMyProductBinding binding;
    private ProductViewModel productViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = (ActivityMyProductBinding) BindingHelper.attachBindingToContentFrame(this, ActivityMyProductBinding.inflate(getLayoutInflater()));

        initializeClasses();
        setListeners();
        setProductRecView();
        setMenus();

    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }


    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        productViewModel = new ProductViewModel();
    }

    private void setListeners() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }


    private void setMenus() {

        navigationHelper.setActionBar("Ürünlerim");
        navigationHelper.createBackButtonOnActionBar();

    }


    private void setProductRecView() {

        productViewModel.getMyProducts(dbHelper.getActiveUser(), new ProductViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<ProductModel> productList = (List<ProductModel>) object;
                System.out.println(productList.size());
                binding.rvMyProduct.setLayoutManager(new GridLayoutManager(MyProductActivity.this, 2));
                binding.rvMyProduct.setAdapter(new MyProductRecyclerAdapter(MyProductActivity.this, productList));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MyProductActivity.this, "Ürünler gelmedi", Toast.LENGTH_SHORT).show();
            }
        });


    }


}