package com.example.eticaretapp.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eticaretapp.databasemodels.WishListDbModel;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.datamodels.WishListModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WishListViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;
    private final MutableLiveData<Boolean> isLoading;


    public WishListViewModel() {
        firebaseHelper = new FirebaseHelper();
        isLoading = new MutableLiveData<>();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void addToWishList(String productId, String customerId, ViewModelCallback callback) {
        WishListModel wishListModel = new WishListModel(productId, customerId);

        firebaseHelper.saveToDatabase(wishListModel, WishListDbModel.TABLE, null).addOnCompleteListener(task -> {
            isLoading.setValue(true);
            if (task.isSuccessful()) {
                callback.onSuccess(null);
                isLoading.setValue(false);
            } else {
                callback.onError(task.getException());
                isLoading.setValue(false);
            }
        });
    }

    public void removeFromWishList(String productId, String customerId, ViewModelCallback callback) {
        Query query = firebaseHelper.getCollection(WishListDbModel.TABLE)
                .whereEqualTo(WishListDbModel.PRODUCT_ID, productId)
                .whereEqualTo(WishListDbModel.CUSTOMER_ID, customerId);

        firebaseHelper.getDataByQuery(query).addOnCompleteListener(task -> {
            isLoading.setValue(true);
            if (task.isSuccessful()) {
                // İşlem başarılı
                if (!task.getResult().isEmpty()) {
                    // Eşleşen belge varsa
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);

                    // Belgeyi sil
                    firebaseHelper.deleteDocument(WishListDbModel.TABLE, document.getId()).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            // Silme işlemi başarılı
                            callback.onSuccess(null);
                            isLoading.setValue(false);
                        } else {
                            // Silme işlemi başarısız
                            callback.onError(task1.getException());
                            isLoading.setValue(false);
                        }
                    });
                } else {
                    // Eşleşen belge bulunamadı
                    callback.onError(new Exception("No matching document found"));
                    isLoading.setValue(false);
                }
            } else {
                // Sorgu başarısız
                callback.onError(task.getException());
                isLoading.setValue(false);
            }
        });
    }

    public void getWishList(String customerId, ViewModelCallback callback) {

        firebaseHelper.getDataByWhere(WishListDbModel.TABLE, WishListDbModel.CUSTOMER_ID, customerId).addOnCompleteListener(task -> {
            isLoading.setValue(true);
            if (task.isSuccessful()) {
                List<WishListModel> wishList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    WishListModel wishListModel = document.toObject(WishListModel.class);
                    wishListModel.getId();
                    wishList.add(wishListModel);
                }
                callback.onSuccess(wishList);
                isLoading.setValue(false);
            } else {
                callback.onError(task.getException());
                isLoading.setValue(false);
            }
        });
    }

    public void getProductByWishList(String productId, String customerId, ViewModelCallback callback) {
        Query query = firebaseHelper.getCollection(WishListDbModel.TABLE)
                .whereEqualTo(WishListDbModel.PRODUCT_ID, productId)
                .whereEqualTo(WishListDbModel.CUSTOMER_ID, customerId);

        firebaseHelper.getDataByQuery(query).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) { // Belge var mı kontrol ediliyor
                    WishListModel wishListModel = task.getResult().getDocuments().get(0).toObject(WishListModel.class);
                    callback.onSuccess(wishListModel);
                } else {
                    // Eğer sonuç boşsa uygun bir hata döndürüyoruz
                    callback.onError(new Exception("No documents found"));
                }
            } else {
                // Sorgu başarısız
                callback.onError(task.getException());
            }
        });
    }

    public interface ViewModelCallback {
        void onSuccess(Object object);

        void onError(Exception e);
    }
}
