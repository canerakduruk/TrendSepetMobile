package com.example.eticaretapp.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.eticaretapp.databasemodels.ParentCategoryDbModel;
import com.example.eticaretapp.datamodels.CityModel;
import com.example.eticaretapp.datamodels.ParentCategoryModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ParentCategoryViewModel extends ViewModel {
    FirebaseHelper firebaseHelper;



    public ParentCategoryViewModel() {
        firebaseHelper = new FirebaseHelper();
    }

   public void getCategories(ViewModelCallback viewModelCallback) {
        firebaseHelper.getAllData(ParentCategoryDbModel.TABLE).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                List<ParentCategoryModel> parentCategoryList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ParentCategoryModel parentCategory = document.toObject(ParentCategoryModel.class);
                    parentCategory.setId(document.getId());
                    parentCategoryList.add(parentCategory);
                }
                viewModelCallback.onSuccess(parentCategoryList);
            }else{
                viewModelCallback.onError(task.getException());
            }
        });

   }
   public void getCategoryByChildId(String childId, ViewModelCallback callback){
        firebaseHelper.getData(ParentCategoryDbModel.TABLE,childId).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ParentCategoryModel parentCategory = task.getResult().toObject(ParentCategoryModel.class);
                callback.onSuccess(parentCategory);
            }else{
                callback.onError(task.getException());
            }
        });
   }

   public interface ViewModelCallback {
        void onSuccess(Object object);
        void onError(Exception e);
   }


}
