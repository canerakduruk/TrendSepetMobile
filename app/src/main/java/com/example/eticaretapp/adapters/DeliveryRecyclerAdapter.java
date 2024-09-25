package com.example.eticaretapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eticaretapp.R;
import com.example.eticaretapp.databasemodels.OrderDbModel;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.databasemodels.UserDbModel;
import com.example.eticaretapp.datamodels.OrderModel;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.ImageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeliveryRecyclerAdapter extends RecyclerView.Adapter<DeliveryRecyclerAdapter.ViewHolder> {
   Context context;
   List<OrderModel> deliveryList;
    public DeliveryRecyclerAdapter(Context context, List<OrderModel> deliveryList) {
        this.context =context;
        this.deliveryList = deliveryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sale_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String,String> productInfo = deliveryList.get(position).getProductInfo();

        holder.productName.setText(productInfo.get(ProductDbModel.NAME));

        holder.address.setText(deliveryList.get(position).getOrderAddress());

        Glide.with(context).load(productInfo.get(ProductDbModel.IMAGE)).into(holder.image);

        String status = deliveryList.get(position).getStatus();

        if (status.equals("0")) {
            holder.radioBtnDeliveryAccepted.setChecked(true);
        } else if (status.equals("1")) {
            holder.radioBtnDeliveryPending.setChecked(true);
        } else if (status.equals("2")) {
            holder.radioBtnDeliverySend.setChecked(true);
        }else if (status.equals("3")) {
            holder.radioBtnDeliverySent.setChecked(true);
        }

        holder.radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {


        });


    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, price, fullName, address, phone, buyer, count;
        ImageView image;
        RadioGroup radioGroup;
        RadioButton radioBtnDeliveryAccepted,radioBtnDeliveryPending,radioBtnDeliverySend,radioBtnDeliverySent;
        DBHelper dbHelper;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.sale_recycler_item_productname);
            price = itemView.findViewById(R.id.sale_recycler_item_price);
            fullName = itemView.findViewById(R.id.sale_recycler_item_fullname);
            address = itemView.findViewById(R.id.sale_recycler_item_address);
            phone = itemView.findViewById(R.id.sale_recycler_item_phone);
            buyer = itemView.findViewById(R.id.sale_recycler_item_buyer);
            count = itemView.findViewById(R.id.sale_recycler_item_count);
            image = itemView.findViewById(R.id.sale_recycler_item_image);
            radioGroup = itemView.findViewById(R.id.sale_recycler_radiogroup);
            radioBtnDeliveryAccepted = itemView.findViewById(R.id.sale_recycler_radiobtndeliveryaccepted);
            radioBtnDeliveryPending = itemView.findViewById(R.id.sale_recycler_radiobtndeliverypending);
            radioBtnDeliverySend = itemView.findViewById(R.id.sale_recycler_radiobtndeliverysend);
            radioBtnDeliverySent = itemView.findViewById(R.id.sale_recycler_radiobtndeliverysent);
            dbHelper = new DBHelper(itemView.getContext());


        }
    }
}
