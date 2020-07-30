package com.usyd.riceapp;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class DiseaseForecastActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static String TAG = "Disease forecast activity";
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_disease_forecast);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_disease_forecast, null, false);
        this.drawer.addView(contentView, 0);
        setPagename(DiseaseForecastActivity.this, getResources().getString(R.string.menu_df));
        initTextToSpeech();

        ViewPager2 viewPager2 = findViewById(R.id.steps_viewPager);
        viewPager2.setAdapter(new DfPagerAdapter(this));
        viewPager2.setPageTransformer(new ZoomOutPageTransformer());
        tabLayout = findViewById(R.id.step_tabs);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:{
                        tab.setText(getResources().getString(R.string.first_fragment_label));
                        tab.setIcon(R.drawable.ic_check_fs);
                        break;
                    }
                    case 1:{
                        tab.setText(getResources().getString(R.string.second_fragment_label));
                        tab.setIcon(R.drawable.ic_check_fs);
                        break;
                    }
                    case 2:{
                        tab.setText(getResources().getString(R.string.third_fragment_label));
                        tab.setIcon(R.drawable.ic_check_fs);
                        break;
                    }
                    case 3:{
                        tab.setText(getResources().getString(R.string.fourth_fragment_label));
                        tab.setIcon(R.drawable.ic_check_fs);
                        break;
                    }
                    case 4:{
                        tab.setText(getResources().getString(R.string.fifth_fragment_label));
                        tab.setIcon(R.drawable.ic_check_fs);
                        break;
                    }
                    case 5:{
                        tab.setText(getResources().getString(R.string.sixth_fragment_label));
                        tab.setIcon(R.drawable.ic_check_fs);
                        break;
                    }
                }
            }
        }
        );
        tabLayoutMediator.attach();

    }
    public void stepFinish(int position){
        tabLayout.
                getTabAt(position).
                getIcon().
                setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
    }

    public void stepUnFinish(int position){
        tabLayout.
                getTabAt(position).
                getIcon().clearColorFilter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        clearDfSharedPreference();
    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
////        clearDfSharedPreference();
//        shutdownTts();
//    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.e(TAG, "shared perference changes: key: "+key);
        if(key.equals("plantDate")&&sharedPreferences.getString("plantDate", "")!=""){
            stepFinish(0);
        }else if(key.equals("location_lat")||key.equals("location_lon")){
            stepFinish(1);
        }else if(key.equals("rice_variety")&&!sharedPreferences.getStringSet("rice_variety", null).isEmpty()){
            stepFinish(2);
        }else if(key.equals("rice_variety")&&(sharedPreferences.getStringSet("rice_variety", null).isEmpty()
        ||sharedPreferences.getStringSet("rice_variety", null)==null)){
            stepUnFinish(2);
        }else if(key.equals("nitrogen_type")&&!sharedPreferences.getStringSet("nitrogen_type", null).isEmpty()){
            stepFinish(3);
        }else if(key.equals("nitrogen_type")&&(sharedPreferences.getStringSet("nitrogen_type", null).isEmpty()
                ||sharedPreferences.getStringSet("nitrogen_type", null)==null)) {
            stepUnFinish(3);
        }else if(key.equals("nitrogen_amount")){
            stepFinish(4);
        }else if(key.equals("seeding_rate")){
            stepFinish(5);
        }
    }
}
