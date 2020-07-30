package com.usyd.riceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DfResultActivity extends BaseActivity {
    final static String TAG="DfResultActivity";
    TextView mr;
    Button go_back;
    ImageButton speaker_btn;
    RelativeLayout icon_high, icon_medium, icon_low;
    FirebaseFirestore mDb;
    FirebaseUser fuser;
    DfRecord newRecord;
    DfRiskRecord riskRecord;
    RiskCalculator calculator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_df_result, null, false);
        this.drawer.addView(contentView, 0);
        setPagename(DfResultActivity.this, getResources().getString(R.string.df_result_page_name));
        initTextToSpeech();

        mr = findViewById(R.id.df_result_mr);
        icon_high = findViewById(R.id.risk_high);
        icon_medium = findViewById(R.id.risk_medium);
        icon_low = findViewById(R.id.risk_low);
        newRecord = createRecord();
        riskRecord = new DfRiskRecord();
        calculator = new RiskCalculator();
        go_back = findViewById(R.id.btn_back);
        speaker_btn = findViewById(R.id.speaker_df_result);
        speaker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = getResources().getString(R.string.df_result_title_text)
                        + riskRecord.getRisk() + "."
                        + getResources().getString(R.string.df_result_mr_title)
                        + getResources().getString(R.string.df_result_mr);
                speakText(text);
            }
        });
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDfSharedPreference();
                Intent homeIntent = new Intent(DfResultActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });

        getWeatherFactors(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                String riskResult = calculator.calculateRisk(newRecord, riskRecord);
                riskRecord.setRisk(riskResult);
                if(riskResult.equals("medium")){
                    icon_high.setVisibility(View.GONE);
                    icon_low.setVisibility(View.GONE);
                    icon_medium.setVisibility(View.VISIBLE);
                }else if(riskResult.equals("low")){
                    icon_high.setVisibility(View.GONE);
                    icon_low.setVisibility(View.VISIBLE);
                    icon_medium.setVisibility(View.GONE);
                }else{
                    icon_high.setVisibility(View.VISIBLE);
                    icon_low.setVisibility(View.GONE);
                    icon_medium.setVisibility(View.GONE);
                }
                createRecordInDatabase();
            }
        });

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    private DfRecord createRecord() {
        DfRecord record = new DfRecord();
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String plantDate = mPref.getString("plantDate", "");
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        Date datePlant = new Date();
        try {
            datePlant = dtf.parse(plantDate);
            record.setPlantDate(datePlant);
        }catch (ParseException e){
            e.printStackTrace();
        }
        double lat =  mPref.getFloat("location_lat", 0);
        double lon = mPref.getFloat("location_lon", 0);
        GeoPoint location = new GeoPoint(lat, lon);
        record.setLocation(location);
        Set<String> rice_variety = mPref.getStringSet("rice_variety", null);
        ArrayList<String> rice = new ArrayList<>();
        rice.addAll(rice_variety);
        record.setRiceVariety(rice);
        Set<String> nitrogen_type = mPref.getStringSet("nitrogen_type", null);
        ArrayList<String> nitrogen = new ArrayList<>();
        nitrogen.addAll(nitrogen_type);
        record.setNitrogenType(nitrogen);
        int nitrogen_amount = mPref.getInt("nitrogen_amount", 0);
        record.setNitrogenAmount(nitrogen_amount);
        int seeding_rate = mPref.getInt("seeding_rate", 0);
        record.setSeedingRate(seeding_rate);
        return record;
    }

    private void createRecordInDatabase(){
        mDb = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference newRecordDoc = mDb
                .collection("users")
                .document(fuser.getUid())
                .collection("dfrecords")
                .document();
        String recordId = newRecordDoc.getId();
        newRecord.setRecordId(recordId);
        newRecordDoc.set(newRecord).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    createRiskRecordInDatabase(recordId);
                }else{
                    Log.e(TAG, "failed to upload record to database");
                }
            }
        });
    }

    public void createRiskRecordInDatabase(String recordId) {
        mDb = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference newRiskDoc = mDb
                .collection("users")
                .document(fuser.getUid())
                .collection("dfrecords")
                .document(recordId)
                .collection("riskRecord")
                .document();
        String id = newRiskDoc.getId();
        riskRecord.setId(id);
        newRiskDoc.set(riskRecord);
    }

    public interface VolleyCallBack {
        void onSuccess();
    }
    public void getWeatherFactors(final VolleyCallBack callBack){
        GeoPoint location = newRecord.getLocation();
        String url = "http://api.weatherapi.com/v1/current.json?key="
                +getResources().getString(R.string.weather_api_key)
                +"&q="
                +location.getLatitude()
                +","
                +location.getLongitude();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject current_object = response.getJSONObject("current");
                    JSONObject location_object = response.getJSONObject("location");
                    double temperature = current_object.getDouble("temp_c");
                    int humidity = current_object.getInt("humidity");
                    double rainfall = current_object.getDouble("precip_mm");
                    String location = location_object.getString("name");
                    Date currentDate = new Date(System.currentTimeMillis());
                    riskRecord.setHumidity(humidity);
                    riskRecord.setTemp(temperature);
                    riskRecord.setRainfall(rainfall);
                    riskRecord.setPlantDate(newRecord.getPlantDate());
                    riskRecord.setPredictDate(currentDate);
                    newRecord.setLocation_name(location);
                    callBack.onSuccess();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        clearDfSharedPreference();
        shutdownTts();
    }
}
