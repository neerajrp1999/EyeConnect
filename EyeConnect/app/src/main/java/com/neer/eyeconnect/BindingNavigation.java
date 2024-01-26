package com.neer.eyeconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neer.eyeconnect.databinding.ActivityBindNavBinding;

import com.neer.eyeconnect.DrawerManipulator;
import com.neer.eyeconnect.ui.AboutUs.AboutUs;
import com.neer.eyeconnect.ui.DashBoard.DashBoardFragment;
import com.neer.eyeconnect.ui.Found.FeedBack;
import com.neer.eyeconnect.ui.Found.FoundFragment;
import com.neer.eyeconnect.ui.Help.HelpFragment;
import com.neer.eyeconnect.ui.Lost.LostFragment;
import com.neer.eyeconnect.ui.MyItems.MyItems;
import com.neer.eyeconnect.ui.MyProfile.MyProfileFragment;


public class BindingNavigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerlayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_nav);

        if(checkCameraHardware(BindingNavigation.this.getApplicationContext())){
            if (
                    (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ){
                ActivityCompat.requestPermissions(BindingNavigation.this,
                        new String[] {android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1888);
            }
        }else{
            Toast.makeText(getApplicationContext(),"not available",Toast.LENGTH_SHORT).show();
            finish();
        }
        caller_after_check();

    }
    public void caller_after_check(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerlayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerlayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        DrawerManipulator.updateDrawerHeader(this, headerView);

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_dashboard) {
                    openFragment(new DashBoardFragment());
                    return true;
                } else if (itemId == R.id.navigation_lost) {
                    openFragment(new LostFragment());
                    return true;
                } else if (itemId == R.id.navigation_found) {
                    openFragment(new FoundFragment());
                    return true;
                } else if (itemId == R.id.navigation_help) {
                    openFragment(new HelpFragment());
                    return true;
                }
                return false;
            }
        });

        fragmentManager = getSupportFragmentManager();
        openFragment(new DashBoardFragment());

    }
    private boolean checkCameraHardware(Context context) {
        return (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) ? true : false ;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.myprofile_drawer) {
            openFragment(new MyProfileFragment());
        }
        else if (itemId == R.id.lost_drawer) {
            openFragment(new LostFragment());
            highlightBottomNavigationItem(R.id.navigation_lost);
        }
        else if (itemId == R.id.found_drawer) {
            openFragment(new FoundFragment());
            highlightBottomNavigationItem(R.id.navigation_found);
        }
        else if (itemId == R.id.feedback_drawer) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            openFragment(new FeedBack(sharedPreferences.getString("userEmail",null)));
        }
        else if (itemId == R.id.myitems_drawer) {
            openFragment(new MyItems());
        }
        else if (itemId == R.id.about_us_drawer) {
            openFragment(new AboutUs());
        }
        else if (itemId == R.id.logout_drawer) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to log out?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                Toast.makeText(this, "Logged out successfully!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finishAffinity(); // Close all activities in the stack
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        drawerlayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
            drawerlayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void highlightBottomNavigationItem(int itemId) {
        bottomNavigationView.getMenu().findItem(itemId).setChecked(true);
    }
}