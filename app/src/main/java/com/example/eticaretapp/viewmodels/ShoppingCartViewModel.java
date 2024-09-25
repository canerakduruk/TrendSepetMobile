package com.example.eticaretapp.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eticaretapp.databasemodels.ShoppingCartDbModel;
import com.example.eticaretapp.datamodels.ShoppingCartModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCartViewModel extends ViewModel {
    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Boolean> isLoading;


    public ShoppingCartViewModel() {
        isLoading = new MutableLiveData<>();
        firebaseHelper = new FirebaseHelper();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }


    public void addShoppingCart(String customerId, String productId, ViewModelCallback viewModelCallback) {

        isLoading.setValue(true);
        ShoppingCartModel shoppingCart = new ShoppingCartModel();
        shoppingCart.setCustomerId(customerId);
        shoppingCart.setProductId(productId);

        firebaseHelper.saveToDatabase(shoppingCart, ShoppingCartDbModel.TABLE, null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                viewModelCallback.onSuccess(null);
                isLoading.setValue(false);
            } else {
                viewModelCallback.onError(task.getException());
                isLoading.setValue(false);
            }
        });
    }

    public void getShoppingCartByCustomerId(String customerId, ViewModelCallback viewModelCallback) {
        firebaseHelper.getDataByWhere(ShoppingCartDbModel.TABLE, ShoppingCartDbModel.CUSTOMER_ID, customerId).addOnCompleteListener(task -> {
            isLoading.setValue(true);
            if (task.isSuccessful()) {
                Map<String, ShoppingCartModel> uniqueProductsMap = new HashMap<>();

                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    ShoppingCartModel shoppingCart = documentSnapshot.toObject(ShoppingCartModel.class);
                    if (shoppingCart != null) {
                        String productId = shoppingCart.getProductId();
                        if (productId != null && !uniqueProductsMap.containsKey(productId)) {
                            shoppingCart.setId(documentSnapshot.getString(ShoppingCartDbModel.ID));
                            uniqueProductsMap.put(productId, shoppingCart);
                        }
                    }
                }

                List<ShoppingCartModel> shoppingCartModelList = new ArrayList<>(uniqueProductsMap.values());

                viewModelCallback.onSuccess(shoppingCartModelList);
                isLoading.setValue(false);
            } else {
                viewModelCallback.onError(task.getException());
                isLoading.setValue(false);
            }
        });
    }

    public void getProductAtCart(String customerId, String productId, ViewModelCallback viewModelCallback) {
        isLoading.setValue(true);
        Query query = firebaseHelper.getCollection(ShoppingCartDbModel.TABLE)
                .whereEqualTo(ShoppingCartDbModel.CUSTOMER_ID, customerId)
                .whereEqualTo(ShoppingCartDbModel.PRODUCT_ID, productId);

        firebaseHelper.getDataByQuery(query).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ShoppingCartModel> shoppingCartModelList = new ArrayList<>();

                for (DocumentSnapshot document : task.getResult()) {
                    ShoppingCartModel shoppingCart = document.toObject(ShoppingCartModel.class);
                    shoppingCart.setId(document.getString(ShoppingCartDbModel.ID));
                    shoppingCartModelList.add(shoppingCart);
                }

                if (!shoppingCartModelList.isEmpty()) {
                    viewModelCallback.onSuccess(shoppingCartModelList.size());
                } else {
                    viewModelCallback.onSuccess(0);

                }
                isLoading.setValue(false);
            } else {
                viewModelCallback.onError(task.getException());
                isLoading.setValue(false);
            }
        });
    }


    public void deleteShoppingCart(String customerId, String productId, ViewModelCallback viewModelCallback) {
        isLoading.setValue(true);
        Query query = firebaseHelper.getCollection(ShoppingCartDbModel.TABLE)
                .whereEqualTo(ShoppingCartDbModel.CUSTOMER_ID, customerId)
                .whereEqualTo(ShoppingCartDbModel.PRODUCT_ID, productId)
                .limit(1);
        firebaseHelper.getDataByQuery(query).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    // İlk belgeyi al
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);

                    // Belgeyi sil
                    firebaseHelper.getCollection(ShoppingCartDbModel.TABLE)
                            .document(document.getId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                // Silme işlemi başarılı
                                viewModelCallback.onSuccess(null);
                                isLoading.setValue(false);
                            })
                            .addOnFailureListener(e -> {
                                // Silme işlemi başarısız
                                viewModelCallback.onError(e);
                                isLoading.setValue(false);
                            });
                } else {
                    // Eğer belge bulunmazsa
                    viewModelCallback.onError(task.getException());
                    isLoading.setValue(false);
                }
            } else {
                viewModelCallback.onError(task.getException());
                isLoading.setValue(false);
            }

        });
    }

    public void clearShoppingCart(String customerId, String productId, ViewModelCallback viewModelCallback) {
        isLoading.setValue(true);

        Query query = firebaseHelper.getCollection(ShoppingCartDbModel.TABLE)
                .whereEqualTo(ShoppingCartDbModel.CUSTOMER_ID, customerId)
                .whereEqualTo(ShoppingCartDbModel.PRODUCT_ID, productId);

        firebaseHelper.getDataByQuery(query).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    // Belgeleri al
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    // Belge silme işlemleri için bir işlem listesi oluştur
                    List<Task<Void>> deleteTasks = new ArrayList<>();

                    // Her belgeyi silmek için döngü
                    for (DocumentSnapshot document : documents) {
                        deleteTasks.add(
                                firebaseHelper.getCollection(ShoppingCartDbModel.TABLE)
                                        .document(document.getId())
                                        .delete()
                        );
                    }

                    // Tüm silme işlemlerinin tamamlanmasını bekle
                    Tasks.whenAllComplete(deleteTasks).addOnCompleteListener(deleteTask -> {
                        if (deleteTask.isSuccessful()) {
                            // Tüm silme işlemleri başarılı
                            viewModelCallback.onSuccess(null);
                        } else {
                            // Silme işlemlerinde hata
                            viewModelCallback.onError(deleteTask.getException());
                        }
                        isLoading.setValue(false);
                    });
                } else {
                    // Eğer belge bulunmazsa
                    viewModelCallback.onError(new Exception("No documents found to delete"));
                    isLoading.setValue(false);
                }
            } else {
                viewModelCallback.onError(task.getException());
                isLoading.setValue(false);
            }
        });
    }

    public interface ViewModelCallback {
        void onSuccess(Object object);

        void onError(Exception e);
    }
}
