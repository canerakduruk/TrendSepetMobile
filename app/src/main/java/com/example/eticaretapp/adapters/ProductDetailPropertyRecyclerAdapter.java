package com.example.eticaretapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailPropertyRecyclerAdapter extends RecyclerView.Adapter<ProductDetailPropertyRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Map.Entry<String, String>> propertyListEntries;

    public ProductDetailPropertyRecyclerAdapter(Context context, HashMap<String, String> propertyList) {
        this.context = context;
        this.propertyListEntries = new ArrayList<>(propertyList.entrySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_productproperty, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // `propertyListEntries`'den anahtar ve değerleri al
        Map.Entry<String, String> entry = propertyListEntries.get(position);
        holder.txtTitle.setText(entry.getKey());   // Anahtar
        holder.txtContent.setText(entry.getValue()); // Değer
    }

    @Override
    public int getItemCount() {
        return propertyListEntries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtContent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.item_recycler_productproperty_producttitle);
            txtContent = itemView.findViewById(R.id.item_recycler_productproperty_productcontent);
        }
    }
}
