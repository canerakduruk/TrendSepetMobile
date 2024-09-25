package com.example.eticaretapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eticaretapp.adapters.CommentRecyclerAdapter;
import com.example.eticaretapp.adapters.ProductDetailPropertyRecyclerAdapter;
import com.example.eticaretapp.adapters.SimilarProductRecyclerAdapter;
import com.example.eticaretapp.adapters.expandableproperty.PropertyRecyclerAdapter;
import com.example.eticaretapp.databasemodels.CommentDbModel;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.databinding.ActivityProductDetailBinding;
import com.example.eticaretapp.datamodels.ChildCategoryModel;
import com.example.eticaretapp.datamodels.CommentModel;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.datamodels.PropertyModel;
import com.example.eticaretapp.datamodels.ShoppingCartModel;
import com.example.eticaretapp.datamodels.ValueModel;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.viewmodels.ChildCategoryViewModel;
import com.example.eticaretapp.viewmodels.CommentViewModel;
import com.example.eticaretapp.viewmodels.ProductViewModel;
import com.example.eticaretapp.viewmodels.PropertyViewModel;
import com.example.eticaretapp.viewmodels.ShoppingCartViewModel;
import com.example.eticaretapp.viewmodels.ValueViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetailActivity extends BaseActivity {
    private ActivityProductDetailBinding binding;
    private ProductViewModel productViewModel;
    private ShoppingCartViewModel shoppingCartViewModel;
    private ChildCategoryViewModel childCategoryViewModel;

    private DBHelper dbHelper;
    private int offset = 100;
    private final int limit = 100;


    private String productID;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = (ActivityProductDetailBinding) BindingHelper.attachBindingToContentFrame(this, ActivityProductDetailBinding.inflate(getLayoutInflater()));
        productID = getIntent().getStringExtra(ProductDbModel.ID);

        initializeClasses();
        getProduct();
        setListeners();
        setMenus();
        isLoading();


    }

    private void isLoading() {
        shoppingCartViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                baseProgressIndicator.setVisibility(View.VISIBLE);
                binding.btnIncrease.setClickable(false);
                binding.btnDecrease.setClickable(false);
                binding.btnAddCart.setClickable(false);
            } else {
                baseProgressIndicator.setVisibility(View.INVISIBLE);
                binding.btnIncrease.setClickable(true);
                binding.btnDecrease.setClickable(true);
                binding.btnAddCart.setClickable(true);

            }
        });
    }

    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        productViewModel = new ProductViewModel();
        shoppingCartViewModel = new ShoppingCartViewModel();
        childCategoryViewModel = new ChildCategoryViewModel();
    }

    private void setMenus() {
        navigationHelper.setActionBar("");
        navigationHelper.createBackButtonOnActionBar();
    }

    private void setListeners() {

        binding.btnAddCart.setOnClickListener(view -> {
            addCart();
        });

        binding.btnIncrease.setOnClickListener(view -> {
            addCart();
        });

        binding.btnDecrease.setOnClickListener(view -> {
            removeCart();
            visibilityController(count);

        });


    }

    private void addCart() {
        shoppingCartViewModel.addShoppingCart(dbHelper.getActiveUser(), productID, new ShoppingCartViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                count++;
                binding.tvProductCount.setText(String.valueOf(count));
                visibilityController(count);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProductDetailActivity.this, "Eklenemedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeCart() {
        shoppingCartViewModel.deleteShoppingCart(dbHelper.getActiveUser(), productID, new ShoppingCartViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                count--;
                binding.tvProductCount.setText(String.valueOf(count));
                visibilityController(count);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProductDetailActivity.this, "Eksiltilmedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProduct() {


        productViewModel.getProductById(productID, new ProductViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                ProductModel productModel = (ProductModel) object;
                binding.tvProductName.setText(productModel.getName());
                binding.tvProductDescription.setText(productModel.getDescription());

                int price = Integer.parseInt(productModel.getPrice());
                int kdvRate = Integer.parseInt(productModel.getKdvRate());
                binding.price.setText(String.valueOf(price + (price * kdvRate / 100)));

                Glide.with(ProductDetailActivity.this).load(productModel.getImageUrl()).into(binding.ivProductImage);

                shoppingCartViewModel.getProductAtCart(dbHelper.getActiveUser(), productID, new ShoppingCartViewModel.ViewModelCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        count = (int) object;
                        System.out.println(count);
                        binding.tvProductCount.setText(String.valueOf(count));
                        visibilityController(count);

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(ProductDetailActivity.this, "Ürün sayısı gelmedi", Toast.LENGTH_SHORT).show();
                    }
                });

                childCategoryViewModel.getCategoryById(productModel.getCategory(), new ChildCategoryViewModel.ViewModelCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        ChildCategoryModel childCategoryModel = (ChildCategoryModel) object;
                        binding.tvCategory.setText(childCategoryModel.getName());
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(ProductDetailActivity.this, "Kategori", Toast.LENGTH_SHORT).show();
                    }
                });

                setRecViewProperty(productModel);
                setRecViewComment(productModel);
                setRecViewSimilarProduct(productModel);

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProductDetailActivity.this, "Ürünler gelmedi", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void visibilityController(int count) {
        if (count > 0) {
            binding.btnAddCart.setVisibility(View.GONE);
            binding.cvButtons.setVisibility(View.VISIBLE);
        } else {
            binding.btnAddCart.setVisibility(View.VISIBLE);
            binding.cvButtons.setVisibility(View.GONE);
        }
    }

    private void setRecViewProperty(ProductModel productModel) {
        PropertyViewModel propertyViewModel = new PropertyViewModel();

        propertyViewModel.getPropertyByProduct(productModel.getProperty(), new PropertyViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                HashMap<String, String> propertyMap = (HashMap<String, String>) object;
                binding.rvPropertyList.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                binding.rvPropertyList.setAdapter(new ProductDetailPropertyRecyclerAdapter(ProductDetailActivity.this, propertyMap));
            }

            @Override
            public void onError(Exception e) {

            }
        });


    }

    private void setRecViewComment(ProductModel productModel) {

        CommentViewModel commentViewModel = new CommentViewModel();
        commentViewModel.getCommentByProductId(productModel.getId(), new CommentViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<CommentModel> commentList = (List<CommentModel>) object;
                binding.rvCommentList.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                binding.rvCommentList.setAdapter(new CommentRecyclerAdapter(ProductDetailActivity.this, commentList));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProductDetailActivity.this, "Yorumlar gelmedi", Toast.LENGTH_SHORT).show();
            }
        });


//        binding.rvCommentList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == commentList.size() - 1) {
//
//                    ArrayList<HashMap<String, String>> newComments = dbHelper.getArrayList(query);
//
//                    if (newComments != null && !newComments.isEmpty()) {
//                        commentList.addAll(newComments);
//                        commentRecyclerAdapter.notifyDataSetChanged();
//                        offset += limit;
//                    }
//                }
//
//            }
//        });
    }

    private void setRecViewSimilarProduct(ProductModel productModel) {
        productViewModel.getSimilarProduct(productModel.getCategory(),productID, new ProductViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<ProductModel> similarList = (List<ProductModel>) object;
                binding.rvSimilarProductList.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                binding.rvSimilarProductList.setAdapter(new SimilarProductRecyclerAdapter(ProductDetailActivity.this, similarList));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ProductDetailActivity.this, "Benzer ürünler gelmedi", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
