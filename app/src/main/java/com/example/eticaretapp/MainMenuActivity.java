package com.example.eticaretapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eticaretapp.adapters.MainMenuProductRecyclerAdapter;
import com.example.eticaretapp.adapters.expandablemenu.ParentMenuReyclerAdapter;
import com.example.eticaretapp.adapters.filteradapter.PropertyFilterAdapter;
import com.example.eticaretapp.databinding.ActivityMainMenuBinding;
import com.example.eticaretapp.datamodels.ParentCategoryModel;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.datamodels.PropertyModel;
import com.example.eticaretapp.helpers.AnimationHelper;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.viewmodels.ParentCategoryViewModel;
import com.example.eticaretapp.viewmodels.ProductViewModel;
import com.example.eticaretapp.viewmodels.PropertyViewModel;

import java.util.HashMap;
import java.util.List;

public class MainMenuActivity extends BaseActivity {
    private ActivityMainMenuBinding binding;
    public View filterView;
    private List<ProductModel> productList;
    private ProductViewModel productViewModel;
    private PropertyViewModel propertyViewModel;
    public boolean inCategory;
    public String parentCategoryId;
    public HashMap<String, List<String>> selectedFilter;
    public String selectedCategoryId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = (ActivityMainMenuBinding) BindingHelper.attachBindingToContentFrame(this, ActivityMainMenuBinding.inflate(getLayoutInflater()));


        initializeClasses();
        initialSettings();
        setProductsRecyclerView();
        setExpandableCategoryMenu();
        setMenus();
        navigationHelper.setTitle("Tüm Ürünler");


        productViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                baseProgressIndicator.setVisibility(View.VISIBLE);
            } else {

                baseProgressIndicator.setVisibility(View.INVISIBLE);
                if (productList.isEmpty()) {
                    binding.llEmpty.setVisibility(View.VISIBLE);
                    binding.llProduct.setVisibility(View.GONE);
                } else {
                    binding.llEmpty.setVisibility(View.GONE);
                    binding.llProduct.setVisibility(View.VISIBLE);
                }

            }
        });

        tvAllProduct.setOnClickListener(view -> {
            selectedCategoryId = null;
            setProductsRecyclerView();
            navigationHelper.closeDrawer();
            navigationHelper.setTitle("Tüm Ürünler");
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    public void setProductsRecyclerView() {

        if (selectedCategoryId != null) {
            if (selectedFilter == null || selectedFilter.isEmpty()) {

                productViewModel.getProductByWhere(selectedCategoryId, new ProductViewModel.ViewModelCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        setFilterRecycler();
                        productList = (List<ProductModel>) object;
                        binding.rvProductList.setLayoutManager(new GridLayoutManager(MainMenuActivity.this, 2));
                        binding.rvProductList.setAdapter(new MainMenuProductRecyclerAdapter(MainMenuActivity.this, productList));
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(MainMenuActivity.this, "Ürünler yüklenmedi", Toast.LENGTH_SHORT).show();

                    }
                });
            } else {
                productViewModel.getProductQuery(selectedFilter, selectedCategoryId, new ProductViewModel.ViewModelCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        productList = (List<ProductModel>) object;
                        binding.rvProductList.setLayoutManager(new GridLayoutManager(MainMenuActivity.this, 2));
                        binding.rvProductList.setAdapter(new MainMenuProductRecyclerAdapter(MainMenuActivity.this, productList));
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(MainMenuActivity.this, "Ürünler yüklenmedi", Toast.LENGTH_SHORT).show();

                    }
                });
            }


        } else {
            productViewModel.getAllProducts(new ProductViewModel.ViewModelCallback() {
                @Override
                public void onSuccess(Object object) {
                    productList = (List<ProductModel>) object;
                    binding.rvProductList.setLayoutManager(new GridLayoutManager(MainMenuActivity.this, 2));
                    binding.rvProductList.setAdapter(new MainMenuProductRecyclerAdapter(MainMenuActivity.this, productList));
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(MainMenuActivity.this, "Ürünler yüklenmedi", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void setExpandableCategoryMenu() {
        ParentCategoryViewModel parentCategoryViewModel = new ParentCategoryViewModel();
        parentCategoryViewModel.getCategories(new ParentCategoryViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<ParentCategoryModel> parentCategoryList = (List<ParentCategoryModel>) object;
                expandableCategoryMenu.setLayoutManager(new LinearLayoutManager(MainMenuActivity.this));
                expandableCategoryMenu.setAdapter(new ParentMenuReyclerAdapter(MainMenuActivity.this, parentCategoryList));
            }

            @Override
            public void onError(Exception e) {

            }
        });


    }


    private void initializeClasses() {
        productViewModel = new ProductViewModel();
        propertyViewModel = new PropertyViewModel();

    }

    private void initialSettings() {
        AnimationHelper.setupLayoutTransition(binding.main);
    }

    private void setMenus() {

        navigationHelper.setActionBar("");
        navigationHelper.createDrawerMenu();
        navigationHelper.createBottomMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (inCategory) {
            createFilterButton(menu);
        }
        navigationHelper.createShoppingCartButton(menu);
        return true;
    }

    private void createFilterButton(Menu menu) {

        filterView = navigationHelper.createFilterButton(menu);
        filterView.setOnClickListener(view -> {
            if (binding.rvPropertyFilterList.getVisibility() == View.GONE) {
                binding.rvPropertyFilterList.setVisibility(View.VISIBLE);
            } else {
                binding.rvPropertyFilterList.setVisibility(View.GONE);
            }
        });


    }

    private void setFilterRecycler() {


        propertyViewModel.getProperties(parentCategoryId, new PropertyViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<PropertyModel> propertyList = (List<PropertyModel>) object;
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainMenuActivity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                binding.rvPropertyFilterList.setLayoutManager(linearLayoutManager);
                binding.rvPropertyFilterList.setAdapter(new PropertyFilterAdapter(MainMenuActivity.this, propertyList));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MainMenuActivity.this, "Filtre yüklenmedi", Toast.LENGTH_SHORT).show();
            }
        });


    }


}