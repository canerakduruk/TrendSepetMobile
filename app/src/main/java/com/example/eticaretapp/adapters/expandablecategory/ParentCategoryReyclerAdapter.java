package com.example.eticaretapp.adapters.expandablecategory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.R;
import com.example.eticaretapp.databasemodels.ChildCategoryDbModel;
import com.example.eticaretapp.datamodels.ChildCategoryModel;
import com.example.eticaretapp.datamodels.ParentCategoryModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.example.eticaretapp.viewmodels.ChildCategoryViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ParentCategoryReyclerAdapter extends RecyclerView.Adapter<ParentCategoryReyclerAdapter.ViewHolder> {

    private Context context;
    private List<ParentCategoryModel> categoryList;
    private ChildCategoryViewModel childCategoryViewModel;

    public ParentCategoryReyclerAdapter(Context context, List<ParentCategoryModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        childCategoryViewModel = new ChildCategoryViewModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expandablerecycler_parentcategory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvParentCategory.setText(categoryList.get(position).getName());
        holder.tvParentCategory.setOnClickListener(view -> {
            holder.rvChildCategory.setVisibility(holder.rvChildCategory.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        childCategoryViewModel.getCategories(categoryList.get(position).getId(), new ChildCategoryViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<ChildCategoryModel> childCategoryList = (List<ChildCategoryModel>) object;
                holder.rvChildCategory.setLayoutManager(new LinearLayoutManager(context));
                ChildCategoryRecyclerAdapter childCategoryAdapter = new ChildCategoryRecyclerAdapter(context,childCategoryList);
                holder.rvChildCategory.setAdapter(childCategoryAdapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Alt kategoriler gelmedi", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvParentCategory;
        RecyclerView rvChildCategory;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvParentCategory = itemView.findViewById(R.id.item_expandable_recyclerview_txtparentcategory);
            rvChildCategory = itemView.findViewById(R.id.item_expandable_recyclerview_recviewchildcategory);
        }

    }


}
