package com.example.eticaretapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eticaretapp.ShoppingCartActivity;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.ProductDetailActivity;
import com.example.eticaretapp.R;
import com.example.eticaretapp.datamodels.ShoppingCartModel;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.viewmodels.ShoppingCartViewModel;

import java.util.List;

public class ShoppingCartRecyclerAdapter extends RecyclerView.Adapter<ShoppingCartRecyclerAdapter.ViewHolder> {
    private final List<ProductModel> productList;
    private final Context context;
    private final DBHelper dbHelper;
    private final ShoppingCartViewModel shoppingCartViewModel;


    public ShoppingCartRecyclerAdapter(Context context, List<ProductModel> productList) {
        this.productList = productList;
        this.context = context;
        dbHelper = new DBHelper(context);
        shoppingCartViewModel = new ShoppingCartViewModel();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchasable_product_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        isLoading(holder);

        getProduct(holder, position);

        getCount(holder, position);

        setListeners(holder, position);


    }

    private void setListeners(@NonNull ViewHolder holder, int position) {
        holder.btnIncrease.setOnClickListener(view -> {
            shoppingCartViewModel.addShoppingCart(dbHelper.getActiveUser(), productList.get(position).getId(), new ShoppingCartViewModel.ViewModelCallback() {
                @Override
                public void onSuccess(Object object) {
                    holder.productCount++;
                    int price = Integer.parseInt(productList.get(position).getPrice());
                    int kdvRate = Integer.parseInt(productList.get(position).getKdvRate());
                    holder.productPrice.setText(String.valueOf(holder.productCount*(price + (price * kdvRate / 100))));
                    holder.productQuantity.setText(String.valueOf(holder.productCount));
                    ((ShoppingCartActivity) context).totalPrice += price + (price * kdvRate / 100);
                    ((ShoppingCartActivity) context).binding.txtTotalPrice.setText(String.valueOf(((ShoppingCartActivity) context).totalPrice));

                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(context, "Hata", Toast.LENGTH_SHORT).show();
                }
            });
        });
        holder.btnDecrease.setOnClickListener(view -> {
            shoppingCartViewModel.deleteShoppingCart(dbHelper.getActiveUser(), productList.get(position).getId(), new ShoppingCartViewModel.ViewModelCallback() {

                @Override
                public void onSuccess(Object object) {
                    holder.productCount--;
                    int price = Integer.parseInt(productList.get(position).getPrice());
                    int kdvRate = Integer.parseInt(productList.get(position).getKdvRate());
                    holder.productPrice.setText(String.valueOf(holder.productCount*(price + (price * kdvRate / 100))));

                    ((ShoppingCartActivity) context).totalPrice -= price + (price * kdvRate / 100);
                    ((ShoppingCartActivity) context).binding.txtTotalPrice.setText(String.valueOf(((ShoppingCartActivity) context).totalPrice));



                    if (holder.productCount < 1){
                        removeProduct(position);



                    }
                    holder.productQuantity.setText(String.valueOf(holder.productCount));

                }

                @Override
                public void onError(Exception e) {

                }
            });
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra(ProductDbModel.ID, productList.get(position).getId());
            context.startActivity(intent);
        });
    }

    private void getCount(@NonNull ViewHolder holder, int position) {
        shoppingCartViewModel.getProductAtCart(dbHelper.getActiveUser(), productList.get(position).getId(), new ShoppingCartViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                holder.productCount = (int) object;
                holder.productQuantity.setText(String.valueOf(holder.productCount));

                int price = Integer.parseInt(productList.get(position).getPrice());
                int kdvRate = Integer.parseInt(productList.get(position).getKdvRate());

                holder.productPrice.setText(String.valueOf(holder.productCount*(price + (price * kdvRate / 100))));
                ((ShoppingCartActivity) context).totalPrice += holder.productCount*(price + (price * kdvRate / 100));
                ((ShoppingCartActivity) context).binding.txtTotalPrice.setText(String.valueOf(((ShoppingCartActivity) context).totalPrice));


            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Hata", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProduct(@NonNull ViewHolder holder, int position) {
        holder.productName.setText(productList.get(position).getName());
        Glide.with(context).load(productList.get(position).getImageUrl()).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void isLoading(@NonNull ViewHolder holder) {
        shoppingCartViewModel.getIsLoading().observe((LifecycleOwner) context, isLoading -> {
            if (isLoading) {
                holder.btnIncrease.setEnabled(false);
                holder.btnDecrease.setEnabled(false);
            } else {
                holder.btnIncrease.setEnabled(true);
                holder.btnDecrease.setEnabled(true);
            }
        });
    }


    public void removeProduct(int position) {

        productList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, productList.size());
        ((ShoppingCartActivity) context).shoppingCardEmptyController(productList.size());



    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        ImageView productImage;
        Button btnIncrease, btnDecrease;
        int productCount = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeViews(itemView);

        }

        private void initializeViews(@NonNull View itemView) {
            productName = itemView.findViewById(R.id.recycler_productname);
            productPrice = itemView.findViewById(R.id.recycler_price);
            productImage = itemView.findViewById(R.id.recycler_image);
            btnIncrease = itemView.findViewById(R.id.recycler_buttonincrease);
            btnDecrease = itemView.findViewById(R.id.recycler_buttondecrease);
            productQuantity = itemView.findViewById(R.id.recycler_quantity_product);
        }

    }


}
