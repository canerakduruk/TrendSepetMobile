package com.example.eticaretapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eticaretapp.ProductActionsActivity;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.R;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.helpers.ImageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyProductRecyclerAdapter extends RecyclerView.Adapter<MyProductRecyclerAdapter.ViewHolder> {

    List<ProductModel> productList;
    Context context;

    public MyProductRecyclerAdapter(Context context,List<ProductModel> productList ) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_product, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.productName.setText(productList.get(position).getName());
        viewHolder.productPrice.setText(productList.get(position).getPrice());

        Glide.with(context).load(productList.get(position).getImageUrl()).into(viewHolder.productImage);


        viewHolder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductActionsActivity.class);
            intent.putExtra(ProductDbModel.ID, productList.get(position).getId());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productPrice;
        ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(view -> {

            });

            productName = itemView.findViewById(R.id.myproduct_recycler_productname);
            productPrice = itemView.findViewById(R.id.myproduct_recycler_price);
            productImage = itemView.findViewById(R.id.myproduct_recycler_image);


        }
    }
}
