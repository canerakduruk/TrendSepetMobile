package com.example.eticaretapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.eticaretapp.databasemodels.OrderDbModel;
import com.example.eticaretapp.databasemodels.ProductDbModel;
import com.example.eticaretapp.databasemodels.UserDbModel;
import com.example.eticaretapp.databinding.ActivityRateProductBinding;
import com.example.eticaretapp.helpers.BindingHelper;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.ImageHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class RateProductActivity extends BaseActivity {
    private ActivityRateProductBinding binding;

    private DBHelper dbHelper;
    private ActivityResultLauncher<Intent> resultLauncher;
    private float rating;

    private String brand;
    private String name;
    private String sellerName;
    private String price;
    private String image;
    private String productId;
    private String deliveryId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = (ActivityRateProductBinding) BindingHelper.attachBindingToContentFrame(this,ActivityRateProductBinding.inflate(getLayoutInflater()));
        dbHelper = new DBHelper(this);

        setListener();
        getData();
        registerResult();

    }





    private void setListener() {
        binding.tvRate.setOnClickListener(view -> setComment());
        binding.etRatingbar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            this.rating = rating;
        });
        binding.ivCommentImage.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            resultLauncher.launch(intent);
        });
    }

    private void getData() {

        String id = "SELECT "
                + OrderDbModel.TABLE + "." + OrderDbModel.ID + " AS DeliveryId"
                + ","
                + OrderDbModel.TABLE + "." + OrderDbModel.PRODUCT_INFO
                + ","
                + " FROM " + OrderDbModel.TABLE
                + " JOIN " + UserDbModel.TABLE
                + " ON "
                + UserDbModel.TABLE + "." + UserDbModel.ID + " = " + OrderDbModel.TABLE + "." + OrderDbModel.SELLER_ID
                + " WHERE " + OrderDbModel.TABLE + "." + OrderDbModel.ID
                + " = '" + getIntent().getStringExtra("id") + "'";

        ArrayList<HashMap<String, String>> productList = dbHelper.getArrayList(id);
        ArrayList<HashMap<String, String>> productData = new ArrayList<>();
        String jsonData;
        Gson gson;
        Type listType;

        HashMap<String, String> hashMap;
        jsonData = productList.get(0).get(OrderDbModel.PRODUCT_INFO);
        productList.get(0).remove(OrderDbModel.PRODUCT_INFO);
        gson = new Gson();
        listType = new TypeToken<HashMap<String, String>>() {
        }.getType();
        hashMap = gson.fromJson(jsonData, listType);
        hashMap.putAll(productList.get(0));
        productData.add(hashMap);


        brand = "BRAND";
        name = productData.get(0).get(ProductDbModel.NAME);
        price = productData.get(0).get(ProductDbModel.PRICE);
        image = productData.get(0).get(ProductDbModel.IMAGE);
        productId = productData.get(0).get(ProductDbModel.ID);
        deliveryId = productData.get(0).get("DeliveryId");

        binding.etBrand.setText(brand);
        binding.etProductName.setText(name);
        binding.etSellerName.setText(sellerName);
        binding.etPrice.setText(price);
        binding.ivProductImage.setImageBitmap(ImageHelper.decodeBase64ToBitmap(image));

    }
    private void setComment(){
        String comment = binding.etComment.getText().toString();
        String formattedDateTime = null;
        String ownerId = dbHelper.getActiveUser();
        byte[] imageBytes = ImageHelper.getBitmapAsByteArray(binding.ivCommentImage);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = null;
            LocalDateTime now = LocalDateTime.now();
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            formattedDateTime = now.format(formatter);
        }

    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        Uri imageUri = result.getData().getData();
                        binding.ivCommentImage.setImageURI(imageUri);
                    } catch (Exception e) {
                        Toast.makeText(RateProductActivity.this, "Fotoğraf Seçilmedi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}