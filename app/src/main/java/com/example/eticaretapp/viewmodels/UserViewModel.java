package com.example.eticaretapp.viewmodels;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eticaretapp.constants.Contants;
import com.example.eticaretapp.databasemodels.UserDbModel;
import com.example.eticaretapp.datamodels.UserModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserViewModel extends ViewModel {

    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<Boolean> isLogged;


    //Constructor Metodum
    public UserViewModel() {
        firebaseHelper = new FirebaseHelper();

        isLoading = new MutableLiveData<>();
        isLogged = new MutableLiveData<>();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsLogged() {
        return isLogged;
    }


    //Kullanıcı kayıt işlemi
    public void registerUser(String profileImageUrl, String email, String password, String name, String surname, String gender, String phoneNumber, final ViewModelCallback callback) {
        // Kullanıcı bilgilerini UserModel nesnesine dönüştür
        UserModel user = new UserModel(profileImageUrl, email, name, surname, gender, phoneNumber);
        isLoading.setValue(true);

        // Firebase'den kullanıcı kayıt işlemini yap
        firebaseHelper.signUp(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Kullanıcı bilgileri veritabanına kaydet
                String userId = firebaseHelper.getCurrentUser().getUid();
                firebaseHelper.saveToDatabase(user, UserDbModel.TABLE, userId);
                callback.onSuccess(userId);
                isLoading.setValue(false);

            } else {
                // Kayıt işlemi başarısız oldu
                callback.onError(task.getException());
                isLoading.setValue(false);
            }
        });
    }

    //Kullanıcı giriş işlemi
    public void loginUser(String email, String password, final ViewModelCallback callback) {
        isLoading.setValue(true);

        // Firebase'den kullanıcı giriş işlemini yap
        firebaseHelper.signIn(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(firebaseHelper.getCurrentUser().getUid());
                isLogged.setValue(true);
                isLoading.setValue(false);


            } else {
                callback.onError(task.getException());
                isLoading.setValue(false);
            }

        });

    }

    //Kullanıcı bilgileri çekme işlemi
    public void getUser(String userId, final ViewModelCallback callback) {
        isLoading.setValue(true);
        firebaseHelper.getData(UserDbModel.TABLE, userId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    isLoading.setValue(false);
                    UserModel user = document.toObject(UserModel.class);
                    user.setId(document.getId());
                    callback.onSuccess(user);
                } else {
                    isLoading.setValue(false);

                    // Document exists but no data
                    callback.onError(new Exception("Document does not exist"));
                }
            } else {
                isLoading.setValue(false);

                // Handle cases where task failed
                Exception exception = task.getException();
                if (exception != null) {
                    isLoading.setValue(false);

                    callback.onError(exception);
                } else {
                    isLoading.setValue(false);

                    callback.onError(new Exception("Unknown error occurred"));
                }
            }
            isLoading.setValue(false);

        });
    }

    public void getUsers(Set<String> userIds, final ViewModelCallback callback) {

        List<DocumentReference> references = new ArrayList<>();
        for (String id : userIds) {
            references.add(firebaseHelper.getCollectionWithDocument(UserDbModel.TABLE, id));
        }

        // Document'leri çekmek için topluca okuma yapın
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (DocumentReference reference : references) {
            tasks.add(reference.get());
        }

        Tasks.whenAllSuccess(tasks).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
            @Override
            public void onComplete(Task<List<Object>> task) {
                if (task.isSuccessful()) {
                    List<UserModel> userModels = new ArrayList<>();
                    // `List<Object>` içindeki her bir nesneyi `DocumentSnapshot` olarak dönüştürün
                    for (Object result : task.getResult()) {
                        DocumentSnapshot snapshot = (DocumentSnapshot) result;
                        UserModel userModel = snapshot.toObject(UserModel.class);

                        userModel.setId(snapshot.getId());
                        userModels.add(userModel);
                    }
                    callback.onSuccess(userModels);

                } else {
                    // Hata işleme
                    System.err.println("Error getting documents: " + task.getException());

                    callback.onError(task.getException());

                }
            }
        });
    }

    //Kullanıcı bilgileri güncelleme
    public void updateUserData(String userId, HashMap<String, Object> newUserInfo, final ViewModelCallback callback) {
        isLoading.setValue(true);

        if (newUserInfo.containsKey(UserDbModel.IMAGE)) {
            firebaseHelper.uploadImage(userId, Contants.PROFILE_IMAGES, Uri.parse((String) newUserInfo.get(UserDbModel.IMAGE))).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task12 -> {
                        if (task12.isSuccessful()) {
                            Uri downloadUri = task12.getResult();
                            newUserInfo.put(UserDbModel.IMAGE, downloadUri);
                            firebaseHelper.updateDocument(newUserInfo, UserDbModel.TABLE, userId).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    isLoading.setValue(false);
                                    callback.onSuccess(null);
                                } else {
                                    isLoading.setValue(false);
                                }
                            });
                        }

                    });

                }
            });
        } else {
            firebaseHelper.updateDocument(newUserInfo, UserDbModel.TABLE, userId).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    isLoading.setValue(false);
                    callback.onSuccess(null);
                } else {
                    isLoading.setValue(false);
                }
            });
        }

    }

    //Şifre değiştirme işlemi
    public void changePassword(String currentPassword, String newPassword, final ViewModelCallback callback) {
        isLoading.setValue(true);
        firebaseHelper.verifyPassword(currentPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseHelper.changePassword(newPassword);
                isLoading.setValue(false);
                callback.onSuccess(null);
            } else {
                isLoading.setValue(false);
                callback.onError(task.getException());
            }
        });
    }

    //Şifre sıfırlama işlemi
    public void resetPassword(ViewModelCallback callback) {
        firebaseHelper.resetPassword().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //Şifre sıfırlama işlemi başarılı
                callback.onSuccess(null);
            } else {
                //Şifre sıfırlama işlemi başarısız oldu
                callback.onError(task.getException());
            }
        });
    }

    //Kullanıcı çıkış işlemi
    public void signOut() {
        // Firebase'den kullanıcı çıkışını yap
        firebaseHelper.signOut();

        // Çıkış işlemi tamamlandı, LiveData'ı güncelle
        isLogged.setValue(false);
    }

    // Callback interface
    public interface ViewModelCallback {
        void onSuccess(Object object);

        void onError(Exception e);
    }
}
