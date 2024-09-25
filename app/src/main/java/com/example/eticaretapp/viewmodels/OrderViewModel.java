package com.example.eticaretapp.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.eticaretapp.databasemodels.OrderDbModel;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.datamodels.OrderModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;

    public OrderViewModel() {
        firebaseHelper = new FirebaseHelper();
    }

    public void addOrder(HashMap<String, String> productInfo, String orderAddress, String customerId, String sellerId, Timestamp date, String status, OrderViewModelCallback callback){
        OrderModel orderModel = new OrderModel();
        orderModel.setProductInfo(productInfo);
        orderModel.setOrderAddress(orderAddress);
        orderModel.setCustomerId(customerId);
        orderModel.setSellerId(sellerId);
        orderModel.setDate(date);
        orderModel.setStatus(status);

        firebaseHelper.saveToDatabase(orderModel, OrderDbModel.TABLE,null).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               callback.onSuccess(productInfo.get(ProductDbModel.ID));
           }else{
               callback.onError(task.getException());
           }
        });
    }

    public void getOrdersByCustomerId (String customerId, OrderViewModelCallback callback){
        firebaseHelper.getDataByWhere(OrderDbModel.TABLE, OrderDbModel.CUSTOMER_ID, customerId).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                List<OrderModel> orderList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()){
                    OrderModel orderModel = document.toObject(OrderModel.class);
                    orderModel.setId(document.getId());
                    orderList.add(orderModel);
                }
                callback.onSuccess(orderList);
            }else{
                callback.onError(task.getException());
            }
        });
    }

    public void getOrdersBySellerId (String sellerId, OrderViewModelCallback callback){
        firebaseHelper.getDataByWhere(OrderDbModel.TABLE, OrderDbModel.SELLER_ID, sellerId).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                List<OrderModel> orderList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()){
                    OrderModel orderModel = document.toObject(OrderModel.class);
                    orderModel.setId(document.getId());
                    orderList.add(orderModel);
                }
                callback.onSuccess(orderList);
            }else{
                callback.onError(task.getException());
            }
        });

    }

    public interface OrderViewModelCallback{
        void onSuccess(Object object);
        void onError(Exception e);
    }
}
