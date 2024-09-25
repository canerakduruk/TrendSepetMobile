package com.example.eticaretapp.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.eticaretapp.databasemodels.PropertyDbModel;
import com.example.eticaretapp.databasemodels.ValueDbModel;
import com.example.eticaretapp.datamodels.PropertyModel;
import com.example.eticaretapp.datamodels.ValueModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyViewModel extends ViewModel {

    private final FirebaseHelper firebaseHelper;

    public PropertyViewModel() {
        firebaseHelper = new FirebaseHelper();
    }

    public void getProperties(String categoryId, final ViewModelCallback callback) {
        firebaseHelper.getDataByWhere(PropertyDbModel.TABLE, PropertyDbModel.PARENT_ID, categoryId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PropertyModel> propertyList = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    PropertyModel property = document.toObject(PropertyModel.class);
                    property.setId(document.getId());
                    propertyList.add(property);
                }
                callback.onSuccess(propertyList);
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public void getPropertyByProduct(HashMap<String, String> propertyMap, final ViewModelCallback callback) {

        List<String> keyList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();

        for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
            String property = entry.getKey();
            String values = entry.getValue();
            keyList.add(property);
            valueList.add(values);
        }
        System.out.println(keyList);
        System.out.println(valueList);

        firebaseHelper.getDocumentsById(PropertyDbModel.TABLE, keyList).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {


                List<PropertyModel> propertyModelList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    PropertyModel property = document.toObject(PropertyModel.class);
                    property.setId(document.getId());
                    propertyModelList.add(property);
                }
                firebaseHelper.getDocumentsById(ValueDbModel.TABLE, valueList).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {

                        List<ValueModel> valueModelList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task1.getResult()) {
                            ValueModel value = document.toObject(ValueModel.class);
                            value.setId(document.getId());
                            valueModelList.add(value);
                        }

                        HashMap<String, String> map = new HashMap<>();
                        for (int i = 0; i < valueModelList.size(); i++) {
                            String property = propertyModelList.get(i).getName();
                            String values = valueModelList.get(i).getName();
                            map.put(property, values);
                        }

                        callback.onSuccess(map);

                    } else {
                        callback.onError(task1.getException());
                    }

                });

            } else {
                callback.onError(task.getException());
            }
        });
    }

    public interface ViewModelCallback {
        void onSuccess(Object object);

        void onError(Exception e);
    }
}
