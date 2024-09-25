package com.example.eticaretapp.adapters.expandablecategory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.ProductActionsActivity;
import com.example.eticaretapp.R;
import com.example.eticaretapp.datamodels.ChildCategoryModel;

import java.util.List;

public class ChildCategoryRecyclerAdapter extends RecyclerView.Adapter<ChildCategoryRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<ChildCategoryModel> categoryList;


    ChildCategoryRecyclerAdapter(Context context, List<ChildCategoryModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expandablerecycler_childcategory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvCategoryName.setText(categoryList.get(position).getName());

        holder.itemView.setOnClickListener(view -> {
            ((ProductActionsActivity)context).binding.tvProductCategory.setText(categoryList.get(position).getName());
            ((ProductActionsActivity)context).selectedParentCategoryId = categoryList.get(position).getParentId();
            ((ProductActionsActivity)context).selectedChildCategoryId = categoryList.get(position).getId();
            ((ProductActionsActivity)context).categorySelectDialog.dismiss();
            ((ProductActionsActivity)context).propertySelectDialog = null;
            ((ProductActionsActivity)context).binding.tvProductProperty.setText(null);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.spinner_item_txtViewCategoryName);
            itemView.setAlpha(0f);  // Başlangıçta tamamen şeffaf yapın
            itemView.setTranslationY(100f);  // Yüksek bir pozisyona koyun
            itemView.animate()
                    .alpha(1f)  // Tamamen opak yapın
                    .translationY(0f)  // Son konumuna kaydırın
                    .setDuration(300)  // Animasyon süresi
                    .start();
        }


    }

}
