package com.example.eticaretapp.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.eticaretapp.databasemodels.ChildCategoryDbModel;
import com.example.eticaretapp.datamodels.ChildCategoryModel;
import com.example.eticaretapp.datamodels.CityModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChildCategoryViewModel extends ViewModel {
    FirebaseHelper firebaseHelper;

    public ChildCategoryViewModel() {
        firebaseHelper = new FirebaseHelper();
    }

    public void getCategories(String parentId, ViewModelCallback viewModelCallback) {
        firebaseHelper.getDataByWhere(ChildCategoryDbModel.TABLE, ChildCategoryDbModel.PARENT_ID, parentId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ChildCategoryModel> childCategoryList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ChildCategoryModel childCategory = document.toObject(ChildCategoryModel.class);
                    childCategory.setId(document.getId());
                    childCategoryList.add(childCategory);
                }
                viewModelCallback.onSuccess(childCategoryList);
            } else {
                viewModelCallback.onError(task.getException());
            }
        });
    }
    public void getCategoryById(String id, ViewModelCallback callback){
        firebaseHelper.getData(ChildCategoryDbModel.TABLE,id).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               DocumentSnapshot document = task.getResult();
               callback.onSuccess(document.toObject(ChildCategoryModel.class));
           }else {
               callback.onError(task.getException());
           }
        });
    }

    public interface ViewModelCallback {
        void onSuccess(Object object);

        void onError(Exception e);
    }
}
