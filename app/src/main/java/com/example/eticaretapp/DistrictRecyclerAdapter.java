package com.example.eticaretapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.datamodels.DistrictModel;

import java.util.List;

public class DistrictRecyclerAdapter extends RecyclerView.Adapter<DistrictRecyclerAdapter.ViewHolder> {
    Context context;
    List<DistrictModel> districtList;

    public DistrictRecyclerAdapter(Context context, List<DistrictModel> districtList) {
        this.context = context;
        this.districtList = districtList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cityanddistrict_reycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(districtList.get(position).getName());
        holder.itemView.setOnClickListener(view -> {
            ((AddressActionsActivity) context).binding.tvDistrict.setText(districtList.get(position).getName());
            ((AddressActionsActivity) context).selectedDistrictId = districtList.get(position).getId();
            ((AddressActionsActivity) context).addressSelectDialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return districtList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cityanddistrict_recycler_item_name);

        }
    }

}
