package com.usyd.riceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;


public class GlobalNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth mAuth;
    TextView phoneNum;
    Switch langSwitch;
    private String phoneNumber;
    private Locale myLocale = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fixLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        let user control volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        langSwitch = findViewById(R.id.lang_switch);
        setLanguage();
        langSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!langSwitch.isChecked()) {
                    changeAppLang("en");
                } else {
                    changeAppLang("km");
                }
            }
        });
        View headerView = navigationView.getHeaderView(0);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle.isDrawerIndicatorEnabled()) {
                    drawer.openDrawer(GravityCompat.START);
                } else {
                    onBackPressed();
                }
            }
        });
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        phoneNum = headerView.findViewById(R.id.drawer_header_phone);
        mAuth = FirebaseAuth.getInstance();
//        User currentUser = ((UserClient)(getApplicationContext())).getUser();
        getUserNumber();
    }
    public void setLanguage(){
        Boolean language = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean("switch", false);
        langSwitch.setChecked(language);
    }

    public void showBackButton(boolean isBack) {
        toggle.setDrawerIndicatorEnabled(!isBack);
        getSupportActionBar().setDisplayHomeAsUpEnabled(isBack);
        toggle.syncState();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
        } else if (id == R.id.nav_disease_forecast) {
            Intent dfIntent = new Intent(this, DiseaseForecastActivity.class);
            startActivity(dfIntent);
        } else if (id == R.id.nav_machine_learning) {
            Intent mlIntent = new Intent(this, PhotoMLActivity.class);
            startActivity(mlIntent);
        } else if (id == R.id.nav_log_out) {
            mAuth.signOut();
            Intent signoutIntent = new Intent(this, LoginActivity.class);
            startActivity(signoutIntent);
        } else if (id == R.id.nav_learning_tutorials) {
            Intent ltIntent = new Intent(this, TutLearningActivity.class);
            startActivity(ltIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // language changer
    public void setAppLocale(String localeCode) {
        Log.v("Language state= ", localeCode);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(localeCode.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }

    private void getUserNumber() {
        DocumentReference userRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    phoneNumber = user.getPhoneNum();
                    phoneNum.setText(phoneNumber);
                }
            }
        });
    }

    public void changeAppLang(String language) {
        if (language == "en") {
            setAppLocale("en");
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean("switch", false)
                    .apply();
            Intent intent = getIntent();
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else if (language == "km") {
            setAppLocale("km");
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean("switch", true)
                    .apply();
            Intent intent = getIntent();
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }
        public static void fixLocale(Context context)
        {
            String language;
            Locale locale = null;
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            Boolean bswitch = prefs.getBoolean("switch", false);
            if(bswitch==false){
                language = "en";
            } else {language = "km";}

            if ((language != null) && (!language.isEmpty())) {
                locale = new Locale(language); // overwrite "use android-locale"
            }
            if (locale != null) {
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                Resources resources = context.getResources();
                resources.updateConfiguration(config, resources.getDisplayMetrics());
                // recreate();

                if (context instanceof GlobalNav) {
                    ((GlobalNav) context).myLocale = locale;
                }
            }
        }
    @Override
    protected void onResume() {
        super.onResume();

        // Locale has changed by other Activity ?
        if ((myLocale != null) && (myLocale.getLanguage() != Locale.getDefault().getLanguage())) {
            myLocale = null;
            recreate();
        }
    }
}
