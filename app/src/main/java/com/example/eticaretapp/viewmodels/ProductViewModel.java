package com.example.eticaretapp.viewmodels;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eticaretapp.constants.Contants;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductViewModel extends ViewModel {
    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Boolean> isLoading;

    public ProductViewModel() {
        firebaseHelper = new FirebaseHelper();
        isLoading = new MutableLiveData<>();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }


    public void addProduct(String name, String description, String category, HashMap<String, String> property, String price, String kdvRate, String imageUrl, String ownerId, final ViewModelCallback callback) {
        ProductModel product = new ProductModel(name, description, category, property, price, kdvRate, imageUrl, ownerId);
        String photoId = ownerId + "_" + System.currentTimeMillis();
        firebaseHelper.uploadImage(photoId, Contants.PRODUCT_IMAGES, Uri.parse(imageUrl)).addOnCompleteListener(task -> {
            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                Uri downloadUri = Uri.parse(task1.getResult().toString());
                product.setImageUrl(String.valueOf(downloadUri));
                firebaseHelper.saveToDatabase(product, ProductDbModel.TABLE, null).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(task2.getException());
                    }
                });
            });
        });

    }

    public void getAllProducts(final ViewModelCallback callback) {
        isLoading.setValue(true);

        firebaseHelper.getAllData(ProductDbModel.TABLE).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ProductModel> productList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ProductModel product = document.toObject(ProductModel.class);
                    product.setId(document.getId());
                    productList.add(product);
                }
                callback.onSuccess(productList);
                isLoading.setValue(false);

            } else {
                callback.onError(task.getException());
                isLoading.setValue(false);

            }
        });
    }

    public void getMyProducts(String ownerId, final ViewModelCallback callback) {
        isLoading.setValue(true);
        firebaseHelper.getDataByWhere(ProductDbModel.TABLE, ProductDbModel.OWNER_ID, ownerId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ProductModel> productList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ProductModel product = document.toObject(ProductModel.class);
                    product.setId(document.getId());
                    productList.add(product);
                }
                callback.onSuccess(productList);
                isLoading.setValue(false);
            } else {
                callback.onError(task.getException());
                isLoading.setValue(false);
            }
        });
    }

    public void getProductByWhere(String categoryId, final ViewModelCallback callback) {
        isLoading.setValue(true);

        firebaseHelper.getDataByWhere(ProductDbModel.TABLE, ProductDbModel.CATEGORY, categoryId).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    List<ProductModel> productList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ProductModel product = document.toObject(ProductModel.class);
                        product.setId(document.getId());
                        productList.add(product);
                    }
                    callback.onSuccess(productList);
                    isLoading.setValue(false);

                } else {
                    callback.onError(task.getException());
                    isLoading.setValue(false);

                }
            }
        });
    }

    public void getSimilarProduct(String categoryId, String productID, final ViewModelCallback callback) {
        isLoading.setValue(true);

        firebaseHelper.getDataByWhere(ProductDbModel.TABLE, ProductDbModel.CATEGORY, categoryId).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                List<ProductModel> productList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (!document.getId().equals(productID)) {
                        ProductModel product = document.toObject(ProductModel.class);
                        product.setId(document.getId());
                        productList.add(product);
                    }
                }
                callback.onSuccess(productList);
                isLoading.setValue(false);

            } else {
                callback.onError(task.getException());
                isLoading.setValue(false);

            }
        });
    }

    public void getProductQuery(HashMap<String, List<String>> selectedFilter, String categoryId, final ViewModelCallback callback) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("products");

        for (Map.Entry<String, List<String>> entry : selectedFilter.entrySet()) {
            String property = entry.getKey();
            List<String> values = entry.getValue();

            // whereIn kullanarak sorgu oluştur
            if (values != null && !values.isEmpty()) {
                query = query.whereEqualTo(ProductDbModel.CATEGORY, categoryId);
                query = query.whereIn(ProductDbModel.PROPERTY + "." + property, values);
            }
        }

        firebaseHelper.getDataByQuery(query).addOnCompleteListener(task -> {
            isLoading.setValue(true);
            if (task.isSuccessful()) {
                List<ProductModel> productList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ProductModel product = document.toObject(ProductModel.class);
                    product.setId(document.getId());
                    productList.add(product);
                }
                callback.onSuccess(productList);
                isLoading.setValue(false);

            } else {
                callback.onError(task.getException());
                isLoading.setValue(false);

            }
        });
    }

    public void getProductById(String id, final ViewModelCallback callback) {
        isLoading.setValue(true);
        firebaseHelper.getData(ProductDbModel.TABLE, id).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ProductModel product = task.getResult().toObject(ProductModel.class);
                callback.onSuccess(product);
                isLoading.setValue(false);
            } else {
                callback.onError(task.getException());
                isLoading.setValue(false);
            }
        });
    }

    public void getProductsById(List<String> idList, final ViewModelCallback callback) {
        firebaseHelper.getDocumentsById(ProductDbModel.TABLE, idList).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ProductModel> productList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    ProductModel product = document.toObject(ProductModel.class);
                    product.setId(document.getId());
                    productList.add(product);
                }
                callback.onSuccess(productList);
            } else {
                callback.onError(task.getException());
            }
        });
    }

    // Callback interface
    public interface ViewModelCallback {
        void onSuccess(Object object);

        void onError(Exception e);
    }
}
