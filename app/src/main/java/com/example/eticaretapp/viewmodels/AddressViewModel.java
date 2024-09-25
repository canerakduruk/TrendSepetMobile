package com.example.eticaretapp.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.eticaretapp.databasemodels.AddressDbModel;
import com.example.eticaretapp.databasemodels.CityDbModel;
import com.example.eticaretapp.databasemodels.DistrictDbModel;
import com.example.eticaretapp.datamodels.AddressModel;
import com.example.eticaretapp.datamodels.CityModel;
import com.example.eticaretapp.datamodels.DistrictModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddressViewModel extends ViewModel {
    private FirebaseHelper firebaseHelper;

    //Constructor Metodum
    public AddressViewModel() {
        firebaseHelper = new FirebaseHelper();
    }

    public void saveAddress(AddressModel addressModel, final AddressViewModelCallback callback) {
        firebaseHelper.saveToDatabase(addressModel, AddressDbModel.TABLE, null).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onError(task.getException());
            }
        });
    }

    //Kullanıcının adreslerini çekme işlemi
    public void getMyAddresses(String ownerId, final AddressViewModelCallback callback) {
        firebaseHelper.getDataByWhere(AddressDbModel.TABLE, AddressDbModel.OWNER_ID, ownerId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<AddressModel> addressList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    AddressModel address = document.toObject(AddressModel.class);
                    address.setId(document.getId());
                    firebaseHelper.getData(CityDbModel.TABLE, address.getCityId()).addOnCompleteListener(task1 -> {
                        address.setCity(task1.getResult().get("name").toString());
                        firebaseHelper.getData(DistrictDbModel.TABLE, address.getDistrictId()).addOnCompleteListener(task2 -> {
                            address.setDistrict(task2.getResult().get("name").toString());
                            callback.onSuccess(addressList);
                        });
                    });
                    addressList.add(address);
                }
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public void updateAddress(HashMap<String, Object> addressInfo, String addressId, final AddressViewModelCallback callback) {
        firebaseHelper.updateDocument(addressInfo, AddressDbModel.TABLE, addressId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onError(task.getException());
            }

        });
    }

    public void getMyAddress(String addressId, final AddressViewModelCallback callback) {
        firebaseHelper.getData(AddressDbModel.TABLE, addressId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AddressModel address = task.getResult().toObject(AddressModel.class);
                address.setId(task.getResult().getId());
                firebaseHelper.getData(CityDbModel.TABLE, address.getCityId()).addOnCompleteListener(task1 -> {
                    address.setCity(task1.getResult().get("name").toString());
                    firebaseHelper.getData(DistrictDbModel.TABLE, address.getDistrictId()).addOnCompleteListener(task2 -> {
                        address.setDistrict(task2.getResult().get("name").toString());
                        callback.onSuccess(address);
                    });
                });
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public void getCities(final AddressViewModelCallback callback) {
        firebaseHelper.getAllData(CityDbModel.TABLE).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<CityModel> cityList = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    CityModel city = document.toObject(CityModel.class);
                    city.setId(document.getId());
                    cityList.add(city);
                }

                callback.onSuccess(cityList);
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public void getDistricts(String cityId, final AddressViewModelCallback callback) {
        firebaseHelper.getDataByWhere(DistrictDbModel.TABLE, DistrictDbModel.CITY_ID, cityId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DistrictModel> districtList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DistrictModel district = document.toObject(DistrictModel.class);
                    district.setId(document.getId());
                    districtList.add(district);
                }
                callback.onSuccess(districtList);
            } else {
                callback.onError(task.getException());
            }
        });
    }

    public void deleteAddress(String addressId, final AddressViewModelCallback callback) {
        firebaseHelper.deleteDocument(AddressDbModel.TABLE, addressId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onError(task.getException());
            }
        });
    }

    // Callback interface
    public interface AddressViewModelCallback {
        void onSuccess(Object object);

        void onError(Exception e);
    }
}
