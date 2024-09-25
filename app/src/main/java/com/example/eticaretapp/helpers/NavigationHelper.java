package com.example.eticaretapp.helpers;


import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.eticaretapp.*;
import com.example.eticaretapp.datamodels.ShoppingCartModel;
import com.example.eticaretapp.viewmodels.ShoppingCartViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class NavigationHelper {

    private AppCompatActivity activity;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private CoordinatorLayout coordinatorLayout;


    public NavigationHelper(AppCompatActivity activity, DrawerLayout drawerLayout, BottomNavigationView bottomNavigationView, Toolbar toolbar, NavigationView navigationView, CoordinatorLayout coordinatorLayout) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.bottomNavigationView = bottomNavigationView;
        this.toolbar = toolbar;
        this.navigationView = navigationView;
        this.coordinatorLayout = coordinatorLayout;
    }


    public void setActionBar(String title) {

        toolbar.setVisibility(View.VISIBLE);
        activity.setSupportActionBar(toolbar);

        if (title == null)
            activity.getSupportActionBar().setTitle("");
        else
            activity.getSupportActionBar().setTitle(title);

    }
    public void setTitle(String title) {
       activity.getSupportActionBar().setTitle(title);
    }


    public void createBackButtonOnActionBar() {
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            activity.finish();
        });
    }

    public void createExitButtonOnActionBar() {
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.vector_exit);
        toolbar.setNavigationOnClickListener(view -> {
            activity.finish();
            activity.overridePendingTransition(R.anim.dont_move, R.anim.slide_out_down);

        });

    }

    public void closeDrawer() {
        drawerLayout.closeDrawers();
    }


    public void createShoppingCartButton(Menu menu) {

        activity.getMenuInflater().inflate(R.menu.menu_toolbar_shoppingcard, menu);
        MenuItem item = menu.findItem(R.id.cartFragment);
        View actionView = item.getActionView();
        TextView txtShoppingCardCount = actionView.findViewById(R.id.cart_action_item_textview);
        getShoppingCount(txtShoppingCardCount);
        actionView.setOnClickListener(view -> {
            Intent intent = new Intent(activity, ShoppingCartActivity.class);
            activity.startActivity(intent);
        });
    }

    public View createFilterButton(Menu menu) {

        activity.getMenuInflater().inflate(R.menu.menu_toolbar_filter, menu);
        MenuItem item = menu.findItem(R.id.filterItem);
        return item.getActionView();

    }

    public void createDrawerMenu() {
        navigationView.setNavigationItemSelectedListener(item -> false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setVisibility(View.VISIBLE);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void createBottomMenu() {
        coordinatorLayout.setVisibility(View.VISIBLE);
        Menu menu = bottomNavigationView.getMenu();
        bottomNavigationView.setOnItemSelectedListener(this::handleBottomNavigationItemSelected);

        if (isCurrentActivity(MainMenuActivity.class))
            menu.findItem(R.id.nav_mainmenu).setChecked(true);

        else if (isCurrentActivity(MyAccountActivity.class))
            menu.findItem(R.id.nav_myaccount).setChecked(true);

        else if (isCurrentActivity(WishListActivity.class))
            menu.findItem(R.id.nav_favorites).setChecked(true);

    }


    public boolean handleBottomNavigationItemSelected(@NonNull MenuItem menuItem) {

        int itemId = menuItem.getItemId();

        if (itemId == R.id.nav_mainmenu)
            handleMenuSelection(MainMenuActivity.class);

        else if (itemId == R.id.nav_myaccount)
            handleMenuSelection(MyAccountActivity.class);

        else if (itemId == R.id.nav_favorites)
            handleMenuSelection(WishListActivity.class);

        return false;

    }

    private void handleMenuSelection(Class<?> targetActivity) {
        if (isCurrentActivity(targetActivity))
            return;

        getTargetActivity(targetActivity);
    }

    private boolean isCurrentActivity(Class<?> targetActivity) {
        if (targetActivity.isInstance(activity))
            return true;
        return false;
    }


    private void getTargetActivity(Class<?> targetActivity) {

        Intent intent = new Intent(activity, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0); // Animasyonları kaldırır


        if (activity instanceof MainMenuActivity) return;

        activity.finish();

    }

    public void getShoppingCount(TextView txtShoppingCardCount) {
        DBHelper dbHelper = new DBHelper(activity);
        ShoppingCartViewModel shoppingCartViewModel = new ShoppingCartViewModel();
        shoppingCartViewModel.getShoppingCartByCustomerId(dbHelper.getActiveUser(), new ShoppingCartViewModel.ViewModelCallback() {
            @Override
            public void onSuccess(Object object) {
                List<ShoppingCartModel> shoppingCartModelList = (List<ShoppingCartModel>) object;
                txtShoppingCardCount.setText(String.valueOf(shoppingCartModelList.size()));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(activity, "Sepet gelmedi", Toast.LENGTH_SHORT).show();
            }
        });


    }


}
