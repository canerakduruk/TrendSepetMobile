package com.example.eticaretapp.adapters.expandableproperty;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.ProductActionsActivity;
import com.example.eticaretapp.R;
import com.example.eticaretapp.databinding.BottomsheetlayoutValueBinding;
import com.example.eticaretapp.datamodels.PropertyModel;
import com.example.eticaretapp.datamodels.ValueModel;
import com.example.eticaretapp.helpers.DialogHelper;
import com.example.eticaretapp.viewmodels.ValueViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;


public class PropertyRecyclerAdapter extends RecyclerView.Adapter<PropertyRecyclerAdapter.ViewHolder> {
    private List<PropertyModel> propertyList;
    private Context context;
    private ValueViewModel valueViewModel;


    public PropertyRecyclerAdapter(Context context, List<PropertyModel> propertyList) {
        this.context = context;
        this.propertyList = propertyList;
        valueViewModel = new ValueViewModel();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expandablerecycler_property, parent, false);
        return new ViewHolder(view);

    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvPropertyName.setText(propertyList.get(position).getName());

        valueViewModel.getValueByPropId(propertyList.get(position).getId(), new ValueViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<ValueModel> valueList = (List<ValueModel>) object;

                holder.tvValue.setOnClickListener(view -> {
                    BottomsheetlayoutValueBinding valueBinding = BottomsheetlayoutValueBinding.inflate(LayoutInflater.from(context));
                    BottomSheetDialog valueDialog = DialogHelper.createBottomSheetDialog((Activity) context, valueBinding);
                    valueBinding.rvValue.setAdapter(new ValueRecyclerAdapter(context,propertyList.get(holder.getBindingAdapterPosition()).getName(), valueDialog, valueList));
                    valueBinding.rvValue.setLayoutManager(new LinearLayoutManager(context));
                    valueBinding.tvCancel.setOnClickListener(view1 -> {
                        ((ProductActionsActivity) context).property.remove(propertyList.get(holder.getBindingAdapterPosition()).getId());
                        ((ProductActionsActivity) context).propertyName.remove(propertyList.get(holder.getBindingAdapterPosition()).getName());
                        valueDialog.cancel();
                    });



                    valueDialog.setOnDismissListener(dialogInterface -> {
                       holder.tvValue.setText(((ProductActionsActivity) context).propertyName.get(propertyList.get(holder.getBindingAdapterPosition()).getName()));
                    });
                    valueDialog.setOnCancelListener(dialogInterface -> {
                        holder.tvValue.setText(null);
                    });
                    valueDialog.show();

                });

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Değerler yüklenmedi", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPropertyName, tvValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPropertyName = itemView.findViewById(R.id.item_expandablerecycler_property_txtpropertyname);
            tvValue = itemView.findViewById(R.id.item_expandablerecycler_property_txtvalue);
        }


    }


}
