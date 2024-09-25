package com.example.eticaretapp.helpers;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;

public class FirebaseHelper {

    private static FirebaseAuth auth;
    private static FirebaseFirestore firestore;
    private static FirebaseStorage storage;


    public FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // Giriş yapmış kullanıcıyı döndür
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // Kullanıcı girişi
    public Task<AuthResult> signIn(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    // Kullanıcı kaydı
    public Task<AuthResult> signUp(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    //Şifre doğrulama
    public Task<Void> verifyPassword(String currentPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(), currentPassword);
        return getCurrentUser().reauthenticate(credential);
    }

    //Şifre değiştirme
    public Task<Void> changePassword(String newPassword) {
        return getCurrentUser().updatePassword(newPassword);
    }

    //Şifre sıfırlama
    public Task<Void> resetPassword() {
        return auth.sendPasswordResetEmail(getCurrentUser().getEmail());
    }

    // Kullanıcı çıkışı
    public void signOut() {
        auth.signOut();
    }

    // Veriyi Firestore'a kaydet
    public <T> Task<Void> saveToDatabase(T model, String collectionName, String id) {
        if (id == null || id.isEmpty()) {// Id parametresi verilmezse kendi id oluşturur.
            return firestore.collection(collectionName).document().set(model);

        } else {// Vermiş olduğumuz id parametresine göre kaydeder.
            return firestore.collection(collectionName).document(id).set(model);
        }
    }

    // Veriyi güncelle
    public Task<Void> updateDocument(HashMap<String, Object> model, String collectionName, String id) {
        return firestore.collection(collectionName).document(id).update(model);
    }

    // Firestore'dan veri çek
    public Task<DocumentSnapshot> getData(String collectionName, String id) {

        return firestore.collection(collectionName).document(id).get();

    }

    // Firestore'dan tüm verileri çek
    public Task<QuerySnapshot> getAllData(String collectionName) {
        return firestore.collection(collectionName).get();
    }

    // Firestore'dan veriyi field belirli bir value değerine göre çek
    public Task<QuerySnapshot> getDataByWhere(String collectionName, String field, String value) {
        return firestore.collection(collectionName).whereEqualTo(field, value).get();
    }

    public CollectionReference getCollection (String collectionName){
        return firestore.collection(collectionName);
    }
    public DocumentReference getCollectionWithDocument (String collectionName, String documentId){
        return firestore.collection(collectionName).document(documentId);
    }

    public Task<QuerySnapshot> getDataByQuery(Query query) {
        return query.get();
    }

    public Task<QuerySnapshot> getDocumentsById(String collectionName, List<String> ids){
        return firestore.collection(collectionName).whereIn(FieldPath.documentId(), ids).get();
    }

    public Task<Void> deleteDocument(String collectionName, String id) {
        return firestore.collection(collectionName).document(id).delete();
    }
    //Fotoğraf yükleme işlemi
    public UploadTask uploadImage(String id, String path, Uri imageUri) {
        return storage.getReference().child(path + id + ".jpg").putFile(imageUri);
    }


}
