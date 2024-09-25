package com.example.eticaretapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eticaretapp.ProductDetailActivity;
import com.example.eticaretapp.R;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.databasemodels.WishListDbModel;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.datamodels.UserModel;
import com.example.eticaretapp.datamodels.WishListModel;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.ImageHelper;
import com.example.eticaretapp.viewmodels.WishListViewModel;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainMenuProductRecyclerAdapter extends RecyclerView.Adapter<MainMenuProductRecyclerAdapter.ViewHolder> {

    Context context;
    List<ProductModel> productList;
    DBHelper dbHelper;

    public MainMenuProductRecyclerAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = productList;
        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WishListViewModel wishListViewModel = new WishListViewModel();
        String productId = productList.get(position).getId();
        String customerId = dbHelper.getActiveUser();

        holder.txtProductName.setText(productList.get(position).getName());
        Glide.with(context).load(productList.get(position).getImageUrl()).into(holder.imgProduct);

        int price = Integer.parseInt(productList.get(position).getPrice());
        int kdv = Integer.parseInt(productList.get(position).getKdvRate());
        String priceWithKdv = String.valueOf(price + (price * kdv / 100));
        holder.txtProductPrice.setText(priceWithKdv);



        wishListViewModel.getProductByWishList(productId, customerId, new WishListViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                WishListModel wishListModel = (WishListModel) object;
                if (wishListModel != null) {
                    holder.favoriteBox.setChecked(true);
                } else {
                    holder.favoriteBox.setChecked(false);
                }
                holder.favoriteBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    if (isChecked) {
                        wishListViewModel.addToWishList(productId, customerId, new WishListViewModel.ViewModelCallback() {
                            @Override
                            public void onSuccess(Object object) {
                                holder.favoriteBox.setChecked(true);
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.favoriteBox.setChecked(false);  // Hata durumunda geri al
                            }
                        });
                    } else {
                        wishListViewModel.removeFromWishList(productId, customerId, new WishListViewModel.ViewModelCallback() {
                            @Override
                            public void onSuccess(Object object) {
                                holder.favoriteBox.setChecked(false);
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.favoriteBox.setChecked(true);  // Hata durumunda geri al
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                holder.favoriteBox.setChecked(false);
                holder.favoriteBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    if (isChecked) {
                        wishListViewModel.addToWishList(productId, customerId, new WishListViewModel.ViewModelCallback() {
                            @Override
                            public void onSuccess(Object object) {
                                holder.favoriteBox.setChecked(true);
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.favoriteBox.setChecked(false);  // Hata durumunda geri al
                            }
                        });
                    } else {
                        wishListViewModel.removeFromWishList(productId, customerId, new WishListViewModel.ViewModelCallback() {
                            @Override
                            public void onSuccess(Object object) {
                                holder.favoriteBox.setChecked(false);
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.favoriteBox.setChecked(true);  // Hata durumunda geri al
                            }
                        });
                    }
                });

            }
        });

        // Sonra yeni dinleyiciyi ayarla


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra(ProductDbModel.ID, productId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductPrice;
        ImageView imgProduct;
        CheckBox favoriteBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.myproduct_recycler_productname);
            txtProductPrice = itemView.findViewById(R.id.myproduct_recycler_price);
            imgProduct = itemView.findViewById(R.id.myproduct_recycler_image);
            favoriteBox = itemView.findViewById(R.id.myproduct_recycler_favoritebutton);
        }
    }
}
