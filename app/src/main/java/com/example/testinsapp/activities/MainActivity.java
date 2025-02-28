package com.example.testinsapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;

import com.cloudinary.android.MediaManager;
import com.example.testinsapp.fragments.AboutFragment;
import com.example.testinsapp.fragments.AccountFragment;
import com.example.testinsapp.fragments.ChatsFragment;
import com.example.testinsapp.fragments.HomeFragment;
import com.example.testinsapp.fragments.NotifFragment;
import com.example.testinsapp.fragments.ProjectsFragment;
import com.example.testinsapp.R;
import com.example.testinsapp.fragments.SearchFragment;
import com.example.testinsapp.fragments.TasksFragment;
import com.example.testinsapp.model.NotificationModel;
import com.example.testinsapp.utils.AccessToken;
import com.example.testinsapp.utils.CircleTransform;
import com.example.testinsapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.squareup.picasso.Picasso;
import com.google.auth.oauth2.GoogleCredentials;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String ONESIGNAL_APP_ID = "cf4742e3-b695-478f-82bc-c4820d2d38cb";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView pfp;
    TextView userName;
    Toolbar toolbar;
    View headerView;

    BottomNavigationView bottomView;
    static Boolean isMediaInit=false;
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        OneSignal.initWithContext(this,ONESIGNAL_APP_ID);
        OneSignal.getNotifications().requestPermission(false, Continue.none());

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        toolbar = findViewById(R.id.toolbar);
        bottomView= findViewById(R.id.bottomMenu);
        setSupportActionBar(toolbar);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String lastFragment = prefs.getString("lastFragment", "HomeFragment");
        navigationView = findViewById(R.id.navView);
        headerView = navigationView.getHeaderView(0); 
        userName = headerView.findViewById(R.id.drawerUsername);
        pfp = headerView.findViewById(R.id.drawerpfp);

        FirebaseUtil.allUserCollectionRef().document(FirebaseUtil.getCurrentUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    userName.setText(username);
                } else {
                    Log.d("Firestore", "No such document");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firestore", "Error getting document: ", e);
            }
        });
        initConfig();
        drawerLayout = findViewById(R.id.main);
        FirebaseUtil.loadPfp(FirebaseUtil.getCurrentUserId(), pfp);
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Log.e( "user token: "," "+task.getResult() );
            }
        });


                    NotificationModel notif=new NotificationModel("cGRwOVgoRW2eRj6iBshmsy:APA91bG9ew1CVGSPWetH8K2N8ESkBSJSJ5CAkYhNt0GBnk3yQ0k_3ePkNEGzbnwHByFQA_iLayF06Slt59MqW2gFyTTfUpeGiKc4YLgTfSSvf6Jdmk_zAzo"
                            ,"it works vro","yes it does",this);
            notif.SendNotif();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        switch (lastFragment) {
            case "ChatsFragment":
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatsFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_chats);
                break;
            default:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_home);
                break;
        }
        bottomView.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.nav_home){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_home);
            }else if(item.getItemId()==R.id.nav_chats){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ChatsFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_chats);
            }else if(item.getItemId()==R.id.nav_account){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AccountFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_account);
            }else if(item.getItemId()== R.id.nav_projects){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProjectsFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_projects);
            }else if(item.getItemId()==R.id.nav_tasks){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TasksFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_tasks);
            }
            return true;
        });

    }




    private void initConfig() {
        if(!isMediaInit){
        Map config = new HashMap();
        config.put("cloud_name", "dht7zgdas");
        config.put("api_key","288243323619562");
        config.put("api_secret","-PfCQTocTYXKVASE-2PQcHjjqRY");
        config.put("secure", true);
        MediaManager.init(this,config);
        isMediaInit=true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().putString("lastFragment", "HomeFragment").apply();
        if(item.getItemId()==R.id.nav_home){

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }else if(item.getItemId()==R.id.nav_notif){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new NotifFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_notif);
        }else if(item.getItemId()==R.id.nav_chats){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ChatsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_chats);
        }else if(item.getItemId()==R.id.nav_account){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AccountFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_account);
        }else if(item.getItemId()==R.id.nav_about){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AboutFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_about);
        }else if(item.getItemId()== R.id.nav_projects){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProjectsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_projects);
        }else if(item.getItemId()==R.id.nav_tasks){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TasksFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_tasks);
        }else if(item.getItemId()==R.id.nav_logout){
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   if (FirebaseUtil.getCurrentUserId() != null) {
                       FirebaseUtil.allUserCollectionRef().document(FirebaseUtil.getCurrentUserId()).update("availability", 0);
                   }
                   FirebaseAuth.getInstance().signOut();
                   startActivity(new Intent(MainActivity.this,LoginActivity.class));
                   finish();
               }
                }
            });

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
public void refreshUser(String username){
    userName.setText(username);
}
    public void refreshPfp(Uri uri){
        Picasso.get().load(uri).transform(new CircleTransform()).into(pfp);
    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {super.onBackPressed();}
    }



}
