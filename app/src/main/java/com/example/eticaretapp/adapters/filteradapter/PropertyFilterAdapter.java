package com.example.eticaretapp.adapters.filteradapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.MainMenuActivity;
import com.example.eticaretapp.R;
import com.example.eticaretapp.databinding.BottomsheetlayoutFiltervalueBinding;
import com.example.eticaretapp.datamodels.PropertyModel;
import com.example.eticaretapp.datamodels.ValueModel;
import com.example.eticaretapp.helpers.DialogHelper;
import com.example.eticaretapp.viewmodels.ValueViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.List;

public class PropertyFilterAdapter extends RecyclerView.Adapter<PropertyFilterAdapter.ViewHolder> {
    private Context context;
    private List<PropertyModel> propertyList;
    private ValueViewModel valueViewModel;

    public PropertyFilterAdapter(Context context, List<PropertyModel> propertyList) {
        this.context = context;
        this.propertyList = propertyList;
        valueViewModel = new ValueViewModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_propertyfilter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String propertyName = propertyList.get(position).getName();
        String propertyId = propertyList.get(position).getId();
        holder.txtPropertyName.setText(propertyName);
        holder.txtPropertyName.setOnClickListener(view -> {
            BottomsheetlayoutFiltervalueBinding valueBinding = BottomsheetlayoutFiltervalueBinding.inflate(LayoutInflater.from(context));
            BottomSheetDialog valueDialog = DialogHelper.createBottomSheetDialog((Activity) context, valueBinding);

            valueViewModel.getValueByPropId(propertyId, new ValueViewModel.ViewModelCallback() {
                @Override
                public void onSuccess(Object object) {
                    List<ValueModel> valueList = (List<ValueModel>) object;
                    if (((MainMenuActivity) context).selectedFilter == null) {
                        ((MainMenuActivity) context).selectedFilter = new HashMap<>();
                    }
                    valueBinding.rvValue.setLayoutManager(new LinearLayoutManager(context));
                    valueBinding.rvValue.setAdapter(new ValueFilterAdapter(context, valueList));
                }

                @Override
                public void onError(Exception e) {

                }
            });

            valueDialog.setOnDismissListener(dialogInterface -> {
                ((MainMenuActivity) context).setProductsRecyclerView();

            });

            valueDialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPropertyName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPropertyName = itemView.findViewById(R.id.item_recycler_propertyfilter_name);
        }
    }
}
