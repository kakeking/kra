package com.usyd.riceapp.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.usyd.riceapp.DfHistoryActivity;
import com.usyd.riceapp.DfRecord;
import com.usyd.riceapp.DfResultActivity;
import com.usyd.riceapp.DfRiskRecord;
import com.usyd.riceapp.R;
import com.usyd.riceapp.RiskCalculator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "Weather Update Receiver";
    private static String CHANNEL = "DiseaseForecastAlarm";
    private static int NOTIFICATION_ID = 200;
    DfRiskRecord riskRecord = new DfRiskRecord();
    RiskCalculator calculator=new RiskCalculator();
    ListenerRegistration mRecordEventListener;
    private ArrayList<DfRecord> recordsFromDb = new ArrayList<DfRecord>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("setUpAlarm".equals(action)) {
            Log.e(TAG, "receive");
            updateRecordsInFirestore(context);
        }
    }

    public void updateRecordsInFirestore(Context context){
        CollectionReference recordsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("dfrecords");
        mRecordEventListener = recordsRef
                .orderBy("createDate", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }
                        if(queryDocumentSnapshots.size()>0){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                DfRecord record = doc.toObject(DfRecord.class);
                                recordsFromDb.add(record);
                            }
                            DfRecord lastRecord = recordsFromDb.get(0);
                            updateRecord(lastRecord, context);
                        }
                    }
                });
    }
    public void updateRecord(DfRecord record, Context context){
        String recordId = record.getRecordId();
        DocumentReference recordsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("dfrecords")
                .document(recordId)
                .collection("riskRecord")
                .document();
        String id = recordsRef.getId();
        riskRecord.setId(id);
        Date plantDate = record.getPlantDate();
        Date currentDate = new Date(System.currentTimeMillis());
        riskRecord.setPlantDate(plantDate);
        riskRecord.setPredictDate(currentDate);
        getWeatherFactors(new DfResultActivity.VolleyCallBack() {
            @Override
            public void onSuccess() {
                String risk = calculator.calculateRisk(record, riskRecord);
                if(risk=="high"){
                    pushNotification(context);
                }
                riskRecord.setRisk(risk);
                recordsRef.set(riskRecord);
            }
        }, record, context);
    }

    public void getWeatherFactors(final DfResultActivity.VolleyCallBack callBack, DfRecord record, Context context){
        GeoPoint location = record.getLocation();
        String url = "http://api.weatherapi.com/v1/current.json?key=64ed820f1daf436382735819202505"
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
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
    public void pushNotification(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notificaiton_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent mainIntent = new Intent(context, DfHistoryActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                NOTIFICATION_ID,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}
