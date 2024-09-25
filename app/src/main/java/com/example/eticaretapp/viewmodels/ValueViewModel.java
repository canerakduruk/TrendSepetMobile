package com.example.eticaretapp.viewmodels;

import com.example.eticaretapp.databasemodels.ValueDbModel;
import com.example.eticaretapp.datamodels.ValueModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ValueViewModel extends ValueModel {

    private final FirebaseHelper firebaseHelper;

    public ValueViewModel() {
        firebaseHelper = new FirebaseHelper();
    }

    public void getValueByPropId(String propertyId, final ViewModelCallback callback) {
        firebaseHelper.getDataByWhere(ValueDbModel.TABLE, ValueDbModel.PARENT_ID, propertyId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ValueModel> valueList = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    ValueModel value = document.toObject(ValueModel.class);
                    value.setId(document.getId());
                    valueList.add(value);
                }
                callback.onSuccess(valueList);
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public void getValueByQuery(HashMap<String,String> propertyMap, final ViewModelCallback callback) {
        Query query = firebaseHelper.getCollection(ValueDbModel.TABLE);

        for (String key : propertyMap.keySet()) {
            query = query.whereEqualTo(key, propertyMap.get(key));
        }
        firebaseHelper.getDataByQuery(query).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ValueModel> valueList = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    ValueModel value = document.toObject(ValueModel.class);
                    value.setId(document.getId());
                    valueList.add(value);
                }

                callback.onSuccess(valueList);
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
