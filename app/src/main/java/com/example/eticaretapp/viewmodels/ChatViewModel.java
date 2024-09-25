package com.example.eticaretapp.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.eticaretapp.databasemodels.ChatDbModel;
import com.example.eticaretapp.databasemodels.OrderDbModel;
import com.example.eticaretapp.datamodels.ChatModel;
import com.example.eticaretapp.datamodels.OrderModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;

    public ChatViewModel() {
        firebaseHelper = new FirebaseHelper();
    }

    public void getChatList(String customerId, ChatViewModelCallback callback) {
        Query query = firebaseHelper.getCollection(OrderDbModel.TABLE)
                .whereEqualTo(OrderDbModel.CUSTOMER_ID, customerId);

        firebaseHelper.getDataByQuery(query).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Set<String> uniqueSellers = new HashSet<>();
                List<OrderModel> orderList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    OrderModel orderModel = document.toObject(OrderModel.class);
                    orderList.add(orderModel);
                }
                for (OrderModel order : orderList) {
                    String getSellerId = order.getSellerId();
                    uniqueSellers.add(getSellerId);
                }

                callback.onSuccess(uniqueSellers);
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public void getChat(String senderId, String receiverId, EventListener<QuerySnapshot> eventListener) {
//        firebaseHelper.getCollection(ChatDbModel.TABLE)
//                .whereEqualTo(ChatDbModel.SENDER_ID, senderId)
//                .whereEqualTo(ChatDbModel.RECEIVER_ID, receiverId).addSnapshotListener(eventListener);
//        firebaseHelper.getCollection(ChatDbModel.TABLE)
//                .whereEqualTo(ChatDbModel.SENDER_ID, receiverId)
//                .whereEqualTo(ChatDbModel.RECEIVER_ID, senderId).addSnapshotListener(eventListener);


//        List<Task<QuerySnapshot>> tasks = new ArrayList<>();
//        tasks.add(querySender.get());
//        tasks.add(queryReceiver.get());
//
//        // Tüm sorguları paralel olarak çalıştırın
//        Tasks.whenAllSuccess(tasks).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                List<ChatModel> chatModelList = new ArrayList<>();
//                // Her bir sorgunun sonucunu işleyin
//                for (Object result : task.getResult()) {
//                    if (result instanceof QuerySnapshot) {
//                        QuerySnapshot snapshot = (QuerySnapshot) result;
//                        for (DocumentSnapshot document : snapshot.getDocuments()) {
//                            if (document.exists()) {
//                                // Document verilerini `ChatModel`'e dönüştürün
//                                ChatModel chatModel = document.toObject(ChatModel.class);
//                                chatModelList.add(chatModel);
//                            }
//                        }
//                    }
//                }
//                // Sonuçları callback aracılığıyla döndürün
//                if (callback != null) {
//                    callback.onSuccess(chatModelList);
//                }
//            } else {
//                // Hata işleme
//                System.err.println("Error getting documents: " + task.getException());
//                if (callback != null) {
//                    callback.onError(task.getException());
//                }
//            }
//        });

    }

    public void sendMessage(String message, String senderId, String receiverId, Timestamp timestamp, ChatViewModelCallback callback) {
        ChatModel chatModel = new ChatModel(senderId, receiverId, message, timestamp);
        firebaseHelper.saveToDatabase(chatModel, ChatDbModel.TABLE, null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public interface ChatViewModelCallback {
        void onSuccess(Object object);

        void onError(Exception e);
    }
}
