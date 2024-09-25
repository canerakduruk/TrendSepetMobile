package com.example.eticaretapp.helpers;

import android.content.Context;
import android.widget.Toast;

import com.example.eticaretapp.databasemodels.ChildCategoryDbModel;
import com.example.eticaretapp.databasemodels.CityDbModel;
import com.example.eticaretapp.databasemodels.DistrictDbModel;
import com.example.eticaretapp.databasemodels.ParentCategoryDbModel;
import com.example.eticaretapp.databasemodels.PropertyDbModel;
import com.example.eticaretapp.databasemodels.ValueDbModel;
import com.example.eticaretapp.datamodels.ChildCategoryModel;
import com.example.eticaretapp.datamodels.CityModel;
import com.example.eticaretapp.datamodels.DistrictModel;
import com.example.eticaretapp.datamodels.ParentCategoryModel;
import com.example.eticaretapp.datamodels.PropertyModel;
import com.example.eticaretapp.datamodels.ValueModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class JsonHelper {

    Context context;

    public JsonHelper(Context context) {
        this.context = context;
    }

    private String loadJSONFromAsset(String jsonFileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            return json;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "0";
    }

    public void saveProperties(String jsonFileName) {

        try {
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(jsonFileName));
            JSONArray jsonArray = jsonObject.getJSONArray("features");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject propertyObject = jsonArray.getJSONObject(i);
                PropertyModel propertyModel = new PropertyModel();
                propertyModel.setName(propertyObject.getString("name"));
                propertyModel.setParentId(String.valueOf(propertyObject.getInt("id")));


                firebaseHelper.saveToDatabase(propertyModel, PropertyDbModel.TABLE, String.valueOf(i + 1));


                JSONArray valueArray = propertyObject.getJSONArray("values");

                for (int j = 0; j < valueArray.length(); j++) {
                    ValueModel valueModel = new ValueModel();
                    valueModel.setName(valueArray.getString(j));
                    valueModel.setParentId(String.valueOf(i+1));
                    firebaseHelper.saveToDatabase(valueModel, ValueDbModel.TABLE, null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Olmadı", Toast.LENGTH_SHORT).show();
        }

    }

    public void saveCities(String jsonFileName) {

        try {
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(jsonFileName)); // JSON verisini bir JSONObject olarak parse edin
            JSONArray citiesArray = jsonObject.getJSONArray("cities"); // "cities" array'ini alın


            for (int i = 0; i < citiesArray.length(); i++) {
                JSONObject cityObject = citiesArray.getJSONObject(i);
                CityModel cityModel = new CityModel();


                cityModel.setName(cityObject.getString("name"));
                firebaseHelper.saveToDatabase(cityModel, CityDbModel.TABLE, String.valueOf(i + 1));

                JSONArray districtsArray = cityObject.getJSONArray("districts");

                for (int j = 0; j < districtsArray.length(); j++) {
                    DistrictModel districtModel = new DistrictModel();
                    districtModel.setName(districtsArray.getString(j));
                    districtModel.setCityId(String.valueOf(i + 1));
                    firebaseHelper.saveToDatabase(districtModel, DistrictDbModel.TABLE, null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Olmadı", Toast.LENGTH_SHORT).show();
        }

    }

    public void saveCategories(String jsonFileName) {

        try {
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(jsonFileName));
            JSONArray citiesArray = jsonObject.getJSONArray("categories");

            for (int i = 0; i < citiesArray.length(); i++) {
                JSONObject cityObject = citiesArray.getJSONObject(i);
                ParentCategoryModel parentCategoryModel = new ParentCategoryModel();

                parentCategoryModel.setName(cityObject.getString("name"));
                firebaseHelper.saveToDatabase(parentCategoryModel, ParentCategoryDbModel.TABLE, String.valueOf(i + 1));

                JSONArray districtsArray = cityObject.getJSONArray("category");

                for (int j = 0; j < districtsArray.length(); j++) {
                    ChildCategoryModel childCategoryModel = new ChildCategoryModel();
                    childCategoryModel.setName(districtsArray.getString(j));
                    childCategoryModel.setParentId(String.valueOf(i + 1));
                    firebaseHelper.saveToDatabase(childCategoryModel, ChildCategoryDbModel.TABLE, null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Olmadı", Toast.LENGTH_SHORT).show();
        }

    }
}

