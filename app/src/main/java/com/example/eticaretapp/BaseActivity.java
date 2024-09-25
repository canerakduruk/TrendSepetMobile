package com.example.eticaretapp;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eticaretapp.databinding.ActivityBaseBinding;
import com.example.eticaretapp.helpers.DBHelper;
import com.example.eticaretapp.helpers.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class BaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;
    public NavigationHelper navigationHelper;
    protected BottomNavigationView bottomNavigationView;
    protected CoordinatorLayout coordinatorLayout;
    protected RecyclerView expandableCategoryMenu;
    protected LinearProgressIndicator baseProgressIndicator;

    private DrawerLayout drawerLayout;
    private DBHelper dbHelper;
    private NavigationView navigationView;
    private ActivityBaseBinding baseBinding;
    public TextView tvAllProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseBinding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(baseBinding.getRoot());

        initializeViews();
        initializeClasses();
        initialSettings();
    }

    private void initializeClasses() {
        dbHelper = new DBHelper(this);
        navigationHelper = new NavigationHelper(this, drawerLayout, bottomNavigationView, toolbar, navigationView, coordinatorLayout);
    }

    private void initializeViews() {
        bottomNavigationView = baseBinding.bottomNavigationView;
        drawerLayout = baseBinding.drawerLayout;
        expandableCategoryMenu = baseBinding.rvDrawerMenu;
        toolbar = baseBinding.toolbar;
        navigationView = baseBinding.navigationView;
        coordinatorLayout = baseBinding.clBottom;
        baseProgressIndicator = baseBinding.baseProgressIndicator;
        tvAllProduct = baseBinding.activityBaseTvallproduct;

    }

    private void initialSettings() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    protected boolean isUserLoggedIn() {
        return !dbHelper.getActiveUser().equals("0");
    }



}