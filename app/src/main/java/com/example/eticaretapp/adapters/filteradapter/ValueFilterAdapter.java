package com.example.eticaretapp.adapters.filteradapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.MainMenuActivity;
import com.example.eticaretapp.R;
import com.example.eticaretapp.databasemodels.PropertyDbModel;
import com.example.eticaretapp.databasemodels.ValueDbModel;
import com.example.eticaretapp.datamodels.ValueModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ValueFilterAdapter extends RecyclerView.Adapter<ValueFilterAdapter.ViewHolder> {
    private Context context;
    private List<ValueModel> valueList;


    public ValueFilterAdapter(Context context, List<ValueModel> valueList) {
        this.context = context;
        this.valueList = valueList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_valuefilter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String valueName = valueList.get(position).getName();
        holder.checkBoxValue.setText(valueName);
        if (((MainMenuActivity) context).selectedFilter != null) {
            if (((MainMenuActivity) context).selectedFilter.containsKey(valueList.get(position).getParentId()) && ((MainMenuActivity) context).selectedFilter.get(valueList.get(position).getParentId()).contains(valueList.get(position).getId())) {
                holder.checkBoxValue.setChecked(true);
            } else {
                holder.checkBoxValue.setChecked(false);
            }
        }
        holder.checkBoxValue.setOnClickListener(view -> {
            if (holder.checkBoxValue.isChecked()) {
                if (((MainMenuActivity) context).selectedFilter == null) {
                    ((MainMenuActivity) context).selectedFilter = new HashMap<>();
                }
                List<String> list = ((MainMenuActivity) context).selectedFilter.get(valueList.get(position).getParentId());
                if (list == null) {
                    list = new ArrayList<>();
                    list.add(valueList.get(position).getId());
                    ((MainMenuActivity) context).selectedFilter.put(valueList.get(position).getParentId(), list);
                } else {
                    list.add(valueList.get(position).getId());
                    ((MainMenuActivity) context).selectedFilter.put(valueList.get(position).getParentId(), list);
                }
            } else {

                List<String> list = ((MainMenuActivity) context).selectedFilter.get(valueList.get(position).getParentId());
                list.remove(valueList.get(position).getId());
                if (list.isEmpty()) {
                    ((MainMenuActivity) context).selectedFilter.remove(valueList.get(position).getParentId());
                }else {
                    ((MainMenuActivity) context).selectedFilter.put(valueList.get(position).getParentId(), list);
                }

            }
            Log.d("TAG", "onBindViewHolder: " + ((MainMenuActivity) context).selectedFilter.toString());
        });



    }

    @Override
    public int getItemCount() {
        return valueList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxValue = itemView.findViewById(R.id.item_recycler_valuefilter_checkbox);
        }
    }
}
