package com.usyd.riceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BaseActivity extends GlobalNav {
    TextToSpeech mTTS;

    public void initTextToSpeech(){
        String ttsEngine = "com.google.android.tts";
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result =  mTTS.setLanguage(Locale.US);
                    if(result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("Text to Speech", "Language not supported");
                    }else{
                        Log.e("Text to Speech", "initial sucess");
                    }
                }else{
                    Log.e("Text to Speech", "Initialisation Failed");
                }
            }
        }, ttsEngine);
    }

    public void speakText(String text){
//        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
//        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

//    set up page title on action bar
    public void setPagename(final Activity activity, String pagename){
            Toolbar toolbar = findViewById(R.id.toolbar);

            //set up global nav drawer
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //set up page name
            TextView pageName = findViewById(R.id.page_name);
            pageName.setText(pagename);
            Typeface face = getResources().getFont(R.font.montserrat);
            pageName.setTypeface(face);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void clearSharedPreference(String key){
        SharedPreferences preferences =getSharedPreferences(key,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        finish();
    }
    public void clearDfSharedPreference(){
        String[] keys = new String[]{"plantDate", "location_lat", "location_lon", "rice_variety", "nitrogen_type", "nitrogen_amount", "seeding_rate"};
        for(String key:keys){
            clearSharedPreference(key);
        }
    }
    //get string in specified locale
    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static String getStringByLocal(Activity context, int id, String locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        return context.createConfigurationContext(configuration).getResources().getString(id);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
//        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
        clearDfSharedPreference();
    }

    public void shutdownTts(){
        if(mTTS != null){
            mTTS.stop();
            mTTS.shutdown();
        }
    }
}
