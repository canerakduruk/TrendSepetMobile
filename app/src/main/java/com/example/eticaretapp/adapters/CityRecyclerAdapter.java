package com.example.eticaretapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.AddressActionsActivity;
import com.example.eticaretapp.R;
import com.example.eticaretapp.datamodels.CityModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityRecyclerAdapter extends RecyclerView.Adapter<CityRecyclerAdapter.ViewHolder> {

    Context context;
    List<CityModel> cityList;

    public CityRecyclerAdapter(Context context, List<CityModel> cityList) {
        this.context = context;
        this.cityList = cityList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cityanddistrict_reycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(cityList.get(position).getName());

        holder.itemView.setOnClickListener(view -> {
            ((AddressActionsActivity) context).binding.tvCity.setText(cityList.get(position).getName());
            ((AddressActionsActivity) context).binding.tvDistrict.setText(null);
            ((AddressActionsActivity) context).selectedCityId = cityList.get(position).getId();

            ((AddressActionsActivity) context).addressSelectDialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cityanddistrict_recycler_item_name);

        }
    }
}
