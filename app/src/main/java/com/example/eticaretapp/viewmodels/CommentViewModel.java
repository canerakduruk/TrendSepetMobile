package com.example.eticaretapp.viewmodels;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.eticaretapp.constants.Contants;
import com.example.eticaretapp.databasemodels.CommentDbModel;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.datamodels.CommentModel;
import com.example.eticaretapp.datamodels.ProductModel;
import com.example.eticaretapp.helpers.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentViewModel extends ViewModel {

    FirebaseHelper firebaseHelper;

    public CommentViewModel () {
        firebaseHelper = new FirebaseHelper();
    }

    public void sendComment(String senderId
            , String productId
            , String orderId
            , String stars
            , String title
            , String comment
            , String date
            , String imageUrl
            , String status
            , final ProductViewModel.ViewModelCallback callback) {

        CommentModel commentModel = new CommentModel(senderId, productId, orderId, stars, title, comment, date, imageUrl, status);
        String photoId = senderId + "_" + System.currentTimeMillis();

        firebaseHelper.uploadImage(photoId, Contants.PRODUCT_IMAGES, Uri.parse(imageUrl)).addOnCompleteListener(task -> {
            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                Uri downloadUri = Uri.parse(task1.getResult().toString());
                commentModel.setImageUrl(String.valueOf(downloadUri));

                firebaseHelper.saveToDatabase(comment, CommentDbModel.TABLE, null).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(task2.getException());
                    }
                });
            });
        });

    }
    public void getCommentByProductId(String productId, ViewModelCallback callback) {
        firebaseHelper.getDataByWhere(CommentDbModel.TABLE, CommentDbModel.PRODUCT_ID, productId).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                List<CommentModel> commentList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    CommentModel comment = document.toObject(CommentModel.class);
                    comment.setId(document.getId());
                    commentList.add(comment);
                }
                callback.onSuccess(commentList);
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
