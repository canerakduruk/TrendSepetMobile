package com.example.eticaretapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eticaretapp.R;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.helpers.ImageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimilarProductRecyclerAdapter extends RecyclerView.Adapter<SimilarProductRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<ProductModel> similarProductList;

    public SimilarProductRecyclerAdapter(Context context, List<ProductModel> similarProductList) {

        this.context = context;
        this.similarProductList = similarProductList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_similarproduct, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtProductName.setText(similarProductList.get(position).getName());
        Glide.with(context).load(similarProductList.get(position).getImageUrl()).into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return similarProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductBrand;
        ImageView imgProduct;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductBrand = itemView.findViewById(R.id.item_recycler_similarproduct_brand);
            txtProductName = itemView.findViewById(R.id.item_recycler_similarproduct_name);
            imgProduct = itemView.findViewById(R.id.item_recycler_similarproduct_image);


        }
    }
}
