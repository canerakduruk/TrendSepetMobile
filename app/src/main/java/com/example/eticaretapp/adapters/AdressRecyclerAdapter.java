package com.example.eticaretapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.AddressActionsActivity;
import com.example.eticaretapp.PaymentActivity;
import com.example.eticaretapp.R;
import com.example.eticaretapp.databasemodels.AddressDbModel;
import com.example.eticaretapp.datamodels.AddressModel;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.viewmodels.AddressViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdressRecyclerAdapter extends RecyclerView.Adapter<AdressRecyclerAdapter.ViewHolder> {
    private final Context context;
    private final List<AddressModel> addressList;

    public AdressRecyclerAdapter(Context context, List<AddressModel> addressList) {
        this.addressList = addressList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdressRecyclerAdapter.ViewHolder holder, int position) {


        holder.txtTitle.setText(addressList.get(position).getTitle());

        holder.txtContent.setText(addressList.get(position).getCity()
                + "/" + addressList.get(position).getDistrict()
                + "/" + addressList.get(position).getDetail());

        PaymentActivity paymentActivity = context instanceof PaymentActivity ? ((PaymentActivity) context) : null;
        if (paymentActivity != null) {
            holder.itemView.setOnClickListener(view -> {

                String address = addressList.get(position).getTitle() + " (" + addressList.get(position).getCity() + "/" + addressList.get(position).getDistrict() + "/" + addressList.get(position).getDetail() + ")";

                SpannableString spannableString = new SpannableString(address);
                int start = address.indexOf(addressList.get(position).getTitle());
                int end = start + addressList.get(position).getTitle().length();
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                paymentActivity.binding.tvSelectAddress.setText(spannableString);
                paymentActivity.addressSelectDialog.dismiss();
            });
        }
        holder.txtDelete.setOnClickListener(view -> {

            AddressViewModel addressViewModel = new AddressViewModel();
            addressViewModel.deleteAddress(addressList.get(position).getId(), new AddressViewModel.AddressViewModelCallback() {
                @Override
                public void onSuccess(Object object) {
                    addressList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, addressList.size());
                }


                @Override
                public void onError(Exception e) {
                    Toast.makeText(context, "Adres silinemedi", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.txtEdit.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddressActionsActivity.class);
            intent.putExtra(AddressDbModel.ID, addressList.get(position).getId());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtContent, txtEdit, txtDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.address_recycler_item_txttitle);
            txtContent = itemView.findViewById(R.id.address_recycler_item_txtcontent);
            txtDelete = itemView.findViewById(R.id.address_recycler_item_txtdelete);
            txtEdit = itemView.findViewById(R.id.address_recycler_item_txtedit);

        }
    }
}
