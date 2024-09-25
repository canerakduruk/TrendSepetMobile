package com.example.eticaretapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eticaretapp.ChatActivity;
import com.example.eticaretapp.R;
import com.example.eticaretapp.RateProductActivity;
import com.example.eticaretapp.databasemodels.ChatDbModel;
import com.example.eticaretapp.databasemodels.OrderDbModel;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.datamodels.OrderModel;
import com.example.eticaretapp.datamodels.UserModel;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.example.eticaretapp.viewmodels.UserViewModel;

import java.util.HashMap;
import java.util.List;

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<OrderModel> orderList;
    private UserViewModel userViewModel;


    public OrderRecyclerAdapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
        userViewModel = new UserViewModel();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recyclerview_order, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HashMap<String, String> productInfo = orderList.get(position).getProductInfo();


        holder.orderName.setText(productInfo.get(ProductDbModel.NAME));

        holder.orderStatus.setText(productInfo.get(OrderDbModel.STATUS));

        Glide.with(context).load(productInfo.get(ProductDbModel.IMAGE)).into(holder.orderImage);

        int kdv = Integer.parseInt(productInfo.get(ProductDbModel.KDV_RATE));
        int price = Integer.parseInt(productInfo.get(ProductDbModel.PRICE));

        int totalPrice = (price * kdv) / 100 + price;
        holder.orderPrice.setText(totalPrice + "₺");

        if (holder.orderStatus.getText().equals("0")) {
            holder.orderStatus.setTextColor(holder.orange);
        } else if (holder.orderStatus.getText().equals("1")) {
            holder.orderStatus.setTextColor(holder.yellow);
        } else if (holder.orderStatus.getText().equals("2")) {
            holder.orderStatus.setTextColor(holder.blue);
        } else if (holder.orderStatus.getText().equals("3")) {
            holder.orderStatus.setTextColor(holder.green);
        }

        userViewModel.getUser(orderList.get(position).getSellerId(), new UserViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                UserModel user = (UserModel) object;
                holder.orderSeller.setText(user.getName() + " " + user.getSurname());
                holder.message.setOnClickListener(view -> {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(ChatDbModel.RECEIVER_ID, user.getId());
                    context.startActivity(intent);
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });

        holder.txtCommentRate.setOnClickListener(view -> {
           Intent intent = new Intent(context, RateProductActivity.class);
           intent.putExtra(ProductDbModel.ID,productInfo.get(ProductDbModel.ID));
           context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderName, orderStatus, orderPrice, orderSeller, orderCount, orderComment, message;
        ImageView commentImage;
        RatingBar orderRatingBar;
        TextView txtCommentRate;
        ImageView orderImage;
        Resources resources;
        LinearLayout commentLayout;
        int orange, green, red, yellow, blue;


        DBHelper dbHelper;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dbHelper = new DBHelper(itemView.getContext());
            orderName = itemView.findViewById(R.id.order_recycler_image_productname);
            orderPrice = itemView.findViewById(R.id.order_recycler_price);
            orderImage = itemView.findViewById(R.id.order_recycler_image);
            orderSeller = itemView.findViewById(R.id.order_recycler_seller);
            orderCount = itemView.findViewById(R.id.order_recycler_count);
            orderStatus = itemView.findViewById(R.id.order_recycler_status);
            orderRatingBar = itemView.findViewById(R.id.item_order_recycler_ratingbar);
            orderComment = itemView.findViewById(R.id.item_order_recycler_comment);
            txtCommentRate = itemView.findViewById(R.id.item_order_recycler_rate);
            commentImage = itemView.findViewById(R.id.item_order_recycler_commentimage);
            commentLayout = itemView.findViewById(R.id.item_order_recycler_commentlayout);
            message = itemView.findViewById(R.id.item_order_recycler_message);

            resources = itemView.getResources();

            dbHelper = new DBHelper(itemView.getContext());
            orange = resources.getColor(R.color.orange_220, itemView.getContext().getTheme());
            green = resources.getColor(R.color.green, itemView.getContext().getTheme());
            red = resources.getColor(R.color.red, itemView.getContext().getTheme());
            yellow = resources.getColor(R.color.yellow, itemView.getContext().getTheme());
            blue = resources.getColor(R.color.blue, itemView.getContext().getTheme());

        }


    }
}
