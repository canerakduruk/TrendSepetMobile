package com.example.eticaretapp.adapters.expandableproperty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.ProductActionsActivity;
import com.example.eticaretapp.R;
import com.example.eticaretapp.databinding.BottomsheetlayoutValueBinding;
import com.example.eticaretapp.datamodels.ValueModel;
import com.example.eticaretapp.eventbus.DataEvent;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ValueRecyclerAdapter extends RecyclerView.Adapter<ValueRecyclerAdapter.ViewHolder> {
    private List<ValueModel> valueList;
    private Context context;
    private BottomSheetDialog valueDialog;
    private String parentName;

    public ValueRecyclerAdapter(Context context,String parentName, BottomSheetDialog valueDialog, List<ValueModel> valueList) {
        this.context = context;
        this.valueList = valueList;
        this.valueDialog = valueDialog;
        this.parentName = parentName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_value, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvValueName.setText(valueList.get(position).getName());
        holder.itemView.setOnClickListener(view -> {

            ((ProductActionsActivity) context).property.put(valueList.get(position).getParentId(), valueList.get(position).getId());
            ((ProductActionsActivity) context).propertyName.put(parentName, valueList.get(position).getName());
            valueDialog.dismiss();

        });
    }

    @Override
    public int getItemCount() {
        return valueList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvValueName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvValueName = itemView.findViewById(R.id.item_recycler_value_txtvalue);
        }
    }

}

