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
import com.example.eticaretapp.WishListActivity;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.databasemodels.WishListDbModel;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.datamodels.WishListModel;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.ImageHelper;
import com.example.eticaretapp.viewmodels.ProductViewModel;
import com.example.eticaretapp.viewmodels.WishListViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WishListRecyclerAdapter extends RecyclerView.Adapter<WishListRecyclerAdapter.ViewHolder> {
    Context context;
    List<WishListModel> wishList;
    ProductViewModel productViewModel;
    WishListViewModel wishListViewModel;

    public WishListRecyclerAdapter(Context context, List<WishListModel> wishList) {
        this.context = context;
        this.wishList = wishList;
        productViewModel = new ProductViewModel();
        wishListViewModel = new WishListViewModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        productViewModel.getProductById(wishList.get(position).getProductId(), new ProductViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                String customerId = wishList.get(holder.getBindingAdapterPosition()).getCustomerId();
                String productId = wishList.get(holder.getBindingAdapterPosition()).getProductId();

                ProductModel productModel = (ProductModel) object;
                holder.productName.setText(productModel.getName());
                Glide.with(context).load(productModel.getImageUrl()).into(holder.imageView);
                int price = Integer.parseInt(productModel.getPrice());
                int kdvRate = Integer.parseInt(productModel.getKdvRate());
                holder.price.setText(String.valueOf(price + (price * kdvRate / 100)));
                holder.favoriteButton.setChecked(true);

                holder.favoriteButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    if (!isChecked) {
                        wishListViewModel.removeFromWishList(productId, customerId, new WishListViewModel.ViewModelCallback() {
                            @Override
                            public void onSuccess(Object object) {
                                wishList.remove(holder.getBindingAdapterPosition());
                                notifyItemRemoved(holder.getBindingAdapterPosition());
                                notifyItemRangeChanged(holder.getBindingAdapterPosition(), wishList.size());
                                ((WishListActivity) context).visibilityController();
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(context, "Ürün kaldırılamadı", Toast.LENGTH_SHORT).show();
                                holder.favoriteButton.setChecked(true);
                            }
                        });

                    }
                });

                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra(ProductDbModel.ID, wishList.get(holder.getBindingAdapterPosition()).getProductId());
                    context.startActivity(intent);
                });

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Ürün gelmedi", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, price;
        ImageView imageView;
        CheckBox favoriteButton;
        DBHelper dbHelper;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.myproduct_recycler_productname);
            price = itemView.findViewById(R.id.myproduct_recycler_price);
            imageView = itemView.findViewById(R.id.myproduct_recycler_image);
            favoriteButton = itemView.findViewById(R.id.myproduct_recycler_favoritebutton);
            dbHelper = new DBHelper(context);
        }
    }
}
