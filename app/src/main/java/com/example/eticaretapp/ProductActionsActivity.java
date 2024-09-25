package com.example.eticaretapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eticaretapp.adapters.expandablecategory.ParentCategoryReyclerAdapter;
import com.example.eticaretapp.adapters.expandableproperty.PropertyRecyclerAdapter;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.databinding.ActivityProductActionsBinding;
import com.example.eticaretapp.databinding.BottomsheetlayoutCategoryBinding;
import com.example.eticaretapp.databinding.BottomsheetlayoutPropertyBinding;
import com.example.eticaretapp.datamodels.ChildCategoryModel;
import com.example.eticaretapp.datamodels.ParentCategoryModel;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.datamodels.PropertyModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.DialogHelper;
import com.example.eticaretapp.viewmodels.ChildCategoryViewModel;
import com.example.eticaretapp.viewmodels.ParentCategoryViewModel;
import com.example.eticaretapp.viewmodels.ProductViewModel;
import com.example.eticaretapp.viewmodels.PropertyViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductActionsActivity extends BaseActivity {
    public ActivityProductActionsBinding binding;

    private ParentCategoryViewModel parentCategoryViewModel;
    private ChildCategoryViewModel childCategoryViewModel;
    private ProductViewModel productViewModel;
    private PropertyViewModel propertyViewModel;

    private DBHelper dbHelper;
    private ActivityResultLauncher<Intent> resultLauncher;
    public BottomSheetDialog propertySelectDialog, categorySelectDialog;
    private RecyclerView propertyRecyclerView;
    public String selectedParentCategoryId;
    public String selectedChildCategoryId;

    public HashMap<String, String> property = new HashMap<>();
    public HashMap<String, String> propertyName = new HashMap<>();
    private String imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = (ActivityProductActionsBinding) BindingHelper.attachBindingToContentFrame(this, ActivityProductActionsBinding.inflate(getLayoutInflater()));


        initializeClasses();
        registerResult();
        setListeners();
        setMenus();

        Intent intent = getIntent();
        if (intent != null) {
            String productId = intent.getStringExtra(ProductDbModel.ID);
            if (productId != null) {
                binding.tvSetProduct.setText("Güncelle");

                productViewModel.getProductById(productId, new ProductViewModel.ViewModelCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        ProductModel product = (ProductModel) object;
                        binding.etProductName.setText(product.getName());
                        binding.etProductPrice.setText(product.getPrice());
                        binding.etProductContent.setText(product.getDescription());
                        binding.etProductKdvRate.setText(product.getKdvRate());
                        //binding.tvProductProperty.setText(product.getProperty());

                        selectedChildCategoryId = product.getCategory();
                        property = product.getProperty();

                        childCategoryViewModel.getCategoryById(product.getCategory(), new ChildCategoryViewModel.ViewModelCallback() {
                            @Override
                            public void onSuccess(Object object) {
                                ChildCategoryModel childCategory = (ChildCategoryModel) object;
                                selectedParentCategoryId = childCategory.getParentId();
                                binding.tvProductCategory.setText(childCategory.getName());
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(ProductActionsActivity.this, "Kategori gelmedi", Toast.LENGTH_SHORT).show();

                            }
                        });
                        Glide.with(ProductActionsActivity.this).load(product.getImageUrl()).into(binding.ivProductImage);

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        }

    }


    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        productViewModel = new ProductViewModel();
        parentCategoryViewModel = new ParentCategoryViewModel();
        propertyViewModel = new PropertyViewModel();
        childCategoryViewModel = new ChildCategoryViewModel();

    }

    private void setListeners() {
        binding.ivProductImage.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            intent.setType("image/*");
            resultLauncher.launch(intent);
        });

        binding.tvSetProduct.setOnClickListener(view -> setProduct());

        binding.tvProductCategory.setOnClickListener(view -> {
            showCategoryDialog();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();

            }
        });
        binding.tvProductProperty.setOnClickListener(view -> {
            if (!binding.tvProductCategory.getText().toString().trim().isEmpty()) {
                showSelectPropertyDialog();
                return;
            }
            Toast.makeText(this, "Lütfen Kategori Seçiniz", Toast.LENGTH_SHORT).show();

        });
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

    }

    private void setProduct() {
        String productName = binding.etProductName.getText().toString();
        String productPrice = binding.etProductPrice.getText().toString();
        String productContent = binding.etProductContent.getText().toString();
        String productKdvRate = binding.etProductKdvRate.getText().toString();
        String productCategory = binding.tvProductCategory.getText().toString();
        String productProperty = binding.tvProductProperty.getText().toString();


        if (productName.trim().isEmpty() || productPrice.trim().isEmpty() || productContent.trim().isEmpty() || productKdvRate.trim().isEmpty() || productCategory.trim().isEmpty() || productProperty.trim().isEmpty()) {
            Toast.makeText(ProductActionsActivity.this, "Lütfen boş alanları doldurunuz.", Toast.LENGTH_SHORT).show();
        } else {
            baseProgressIndicator.setVisibility(View.VISIBLE);

            List<String> keysToRemove = new ArrayList<>();
            for (Map.Entry<String, String> entry : property.entrySet()) {
                if (entry.getValue().equals("")) {
                    keysToRemove.add(entry.getKey());
                }
            }
            for (String key : keysToRemove) {
                property.remove(key);
            }
            productViewModel.addProduct(productName, productContent, selectedChildCategoryId, property, productPrice, productKdvRate, imageUri, dbHelper.getActiveUser(), new ProductViewModel.ViewModelCallback() {
                @Override
                public void onSuccess(Object object) {
                    baseProgressIndicator.setVisibility(View.INVISIBLE);
                    binding.etProductName.setText("");
                    binding.etProductPrice.setText("");
                    binding.etProductContent.setText("");
                    binding.tvProductCategory.setText("");
                    binding.tvProductProperty.setText("");
                    binding.ivProductImage.setImageResource(R.drawable.choose_image);
                    Toast.makeText(ProductActionsActivity.this, "Ürün başarıyla eklendi.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(ProductActionsActivity.this, "Ürünü eklerken hata oluştu lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private void setMenus() {
        navigationHelper.setActionBar("Ürün Ekle");
        navigationHelper.createBackButtonOnActionBar();
    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            try {
                Uri imageUri = result.getData().getData();
                binding.ivProductImage.setImageURI(imageUri);
                this.imageUri = String.valueOf(imageUri);
            } catch (Exception e) {
                Toast.makeText(ProductActionsActivity.this, "Fotoğraf Seçilmedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCategoryDialog() {
        BottomsheetlayoutCategoryBinding categoryBinding = BottomsheetlayoutCategoryBinding.inflate(getLayoutInflater());
        categorySelectDialog = DialogHelper.createBottomSheetDialog(this, categoryBinding);
        RecyclerView categoryRecyclerView = categoryBinding.rvCategory;
        setCategoryRecyclerView(categoryRecyclerView);
        categorySelectDialog.show();
    }

    private void showSelectPropertyDialog() {
        if (propertySelectDialog == null) {
            BottomsheetlayoutPropertyBinding propertyBinding = BottomsheetlayoutPropertyBinding.inflate(getLayoutInflater());
            propertySelectDialog = DialogHelper.createBottomSheetDialog(this, propertyBinding);
            propertyRecyclerView = propertyBinding.rvProperty;
            setPropertyRecyclerView(propertyRecyclerView);

            propertyBinding.tvAccept.setOnClickListener(view -> {
                StringBuilder property = new StringBuilder();
                String key = "";
                String value = "";
                for (Map.Entry<String, String> entry : propertyName.entrySet()) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if (!value.equals("-")) {
                        property.append(key).append(" : ").append(value).append("\n");
                    }
                }
                propertySelectDialog.dismiss();
                binding.tvProductProperty.setText(property);

            });

        }

        propertySelectDialog.show();

    }

    private void setCategoryRecyclerView(RecyclerView recyclerView) {

        parentCategoryViewModel.getCategories(new ParentCategoryViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<ParentCategoryModel> parentCategoryList = (List<ParentCategoryModel>) object;
                recyclerView.setLayoutManager(new LinearLayoutManager(ProductActionsActivity.this));
                recyclerView.setAdapter(new ParentCategoryReyclerAdapter(ProductActionsActivity.this, parentCategoryList));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProductActionsActivity.this, "Kategoriler gelirken hata oluştu.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setPropertyRecyclerView(RecyclerView recyclerView) {

        propertyViewModel.getProperties(selectedParentCategoryId, new PropertyViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                property.clear();
                propertyName.clear();
                List<PropertyModel> propertyList = (List<PropertyModel>) object;
                for (PropertyModel propertyModel : propertyList) {
                    property.put(propertyModel.getId(), "");
                    propertyName.put(propertyModel.getName(), "-");
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(ProductActionsActivity.this));
                recyclerView.setAdapter(new PropertyRecyclerAdapter(ProductActionsActivity.this, propertyList));
            }

            @Override
            public void onError(Exception e) {

            }
        });


    }


}