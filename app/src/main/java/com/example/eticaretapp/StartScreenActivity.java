package com.example.eticaretapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.JsonHelper;
import com.example.eticaretapp.helpers.NetworkUtils;

public class StartScreenActivity extends AppCompatActivity {
    private Handler splashScreenHandler = new Handler();
    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        EdgeToEdge.enable(this);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        if (dbHelper.getActiveUser() == null){
            dbHelper.insertActiveUser("0");
        }

        //initializeClasses();
        //initialSettings();


       JsonHelper jsonHelper = new JsonHelper(this);
//        dbHelper.deleteAllData(CommentDbModel.TABLE);
      //  jsonHelper.loadJSONFromAsset("productpropery.json", "features", "values");
      //   jsonHelper.saveProperties("productpropery.json");
  //      JsonHelper jsonHelper = new JsonHelper(this);
//        jsonHelper.saveProperties("productpropery.json");
//        jsonHelper.saveProperties("productpropery.json");

        //aThousandComment();

        if (NetworkUtils.isInternetAvailable(this)) {
            splashScreen();
        } else {
            Toast.makeText(this, "İnternet bağlantısı bulunmamaktadır.", Toast.LENGTH_SHORT).show();
        }

    }


    private void splashScreen() {
        splashScreenHandler.postDelayed(() -> {
            Intent loginActivityIntent = new Intent(StartScreenActivity.this, MainMenuActivity.class);
            startActivity(loginActivityIntent);
            finish();
        }, 3000);
    }
}