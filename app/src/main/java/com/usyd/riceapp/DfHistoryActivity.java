package com.usyd.riceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.usyd.riceapp.notification.UpdateReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DfHistoryActivity extends BaseActivity {
    private static String TAG = "Disease forecast history activity";
    private RecyclerView mRecordRecyclerView, mRiskRecyclerView;
    private Button create_new, set_alarm;
    private ImageButton speakBtn;
    private TextView last_plant_date, last_create_date;
    private DfHistoryAdapter mRecordRecyclerAdapter;
    private DfRiskItemAdapter mRiskRecyclerAdapter;
    ListenerRegistration mRecordEventListener;
    ListenerRegistration mRiskRecordEventLisener;
    private ArrayList<DfRecord> recordsFromDb = new ArrayList<DfRecord>();
    private ArrayList<DfRiskRecord> riskRecordsFromDb = new ArrayList<DfRiskRecord>();
    private Set<String> mRecordIds = new HashSet<>();
    private Set<String> mRiskRecordIds = new HashSet<>();
    private DfRecord lastRecord;
    private CustomDialog mDialog;
    private CustomDialog aDialog;
    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_df_history, null, false);
        this.drawer.addView(contentView, 0);
        initTextToSpeech();
//        end of set global navigation drawer
        mRecordRecyclerView = findViewById(R.id.user_logs);
        mRiskRecyclerView = findViewById(R.id.log_risk_weather);
        create_new = findViewById(R.id.btn_create_new);
        last_plant_date = findViewById(R.id.df_last_plant_date);
        last_create_date = findViewById(R.id.df_last_create_date);
        speakBtn = findViewById(R.id.df_log_speak_btn);
        set_alarm = findViewById(R.id.btn_set_alarm);
        getRecordsFromFireStore();
        create_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSteps();
            }
        });
        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakRecord(lastRecord, true);
            }
        });
        set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupAlarm();
                Toast.makeText(DfHistoryActivity.this, "Alarm successfully set!",Toast.LENGTH_SHORT).show();
            }
        });
        initRecordListRecyclerView(recordsFromDb);
        initRiskListRecyclerView(riskRecordsFromDb);
    }

    public void initRecordListRecyclerView(final ArrayList<DfRecord> records){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecordRecyclerAdapter = new DfHistoryAdapter(records, new DfHistoryAdapter.DfHistoryAdapterListener() {
            @Override
            public void iconSpeakerOnClick(View v, int position) {
                speakRecord(records.get(position), false);
            }

            @Override
            public void iconDeleteOnClick(View v, int position) {
                confirmDelete(records.get(position), position);
            }
        });
        mRecordRecyclerView.setAdapter(mRecordRecyclerAdapter);
        mRecordRecyclerView.setLayoutManager(layoutManager);
    }

    public void initRiskListRecyclerView(final ArrayList<DfRiskRecord> records){
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRiskRecyclerAdapter = new DfRiskItemAdapter(records);
        mRiskRecyclerView.setAdapter(mRiskRecyclerAdapter);
        mRiskRecyclerView.setLayoutManager(lm);
    }
    //get related records document
    public void getRecordsFromFireStore(){
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
                                if(!mRecordIds.contains(record.getRecordId())){
                                    mRecordIds.add(record.getRecordId());
                                    recordsFromDb.add(record);
                                }
                            }
                            mRecordRecyclerAdapter.notifyDataSetChanged();
                            setLastRecord();
                        }else{
                            goToSteps();
                        }
                    }
                });
    }
    private void goToSteps(){
        Intent DFIntent = new Intent(DfHistoryActivity.this, DiseaseForecastActivity.class);
        startActivity(DFIntent);
    }
    private void goToResult(){
        Intent ResultIntent = new Intent(DfHistoryActivity.this, DfResultActivity.class);
        startActivity(ResultIntent);
    }
    private void setSharedPreference(DfRecord record){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("plantDate", simpleDateFormat.format(record.getPlantDate()));
        editor.putFloat("location_lat", (float)record.getLocation().getLatitude());
        editor.putFloat("location_lon", (float)record.getLocation().getLongitude());
        Set<String> variety = new LinkedHashSet<>();
        ArrayList<String> rice_variety = record.getRiceVariety();
        for(String rv : rice_variety){
            variety.add(rv);
        }
        Set<String> type = new LinkedHashSet<>();
        ArrayList<String> ni_type = record.getNitrogenType();
        for(String nt : ni_type){
            type.add(nt);
        }
        editor.putStringSet("rice_variety", variety);
        editor.putStringSet("nitrogen_type", type);
        editor.putInt("nitrogen_amount", record.getNitrogenAmount());
        editor.putInt("seeding_rate", record.getSeedingRate());
        editor.commit();
    }
    public void setLastRecord(){
        lastRecord = recordsFromDb.get(0);
        Log.e(TAG, "last record id"+lastRecord.getRecordId());
        getRiskRecordsFromFireStore(lastRecord.getRecordId());
        Date plant_date = lastRecord.getPlantDate();
        Date create_date = lastRecord.getCreateDate();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        last_plant_date.setText(simpleDateFormat.format(plant_date));
        last_create_date.setText(simpleDateFormat2.format(create_date));
    }
    public void getRiskRecordsFromFireStore(String recordId){
        CollectionReference recordsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("dfrecords")
                .document(recordId)
                .collection("riskRecord");

        mRiskRecordEventLisener = recordsRef
                .orderBy("predictDate", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }
                        if(queryDocumentSnapshots.size()>0){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                DfRiskRecord record = doc.toObject(DfRiskRecord.class);
                                if(!mRiskRecordIds.contains(record.getId())) {
                                    mRiskRecordIds.add(record.getId());
                                    riskRecordsFromDb.add(record);
                                }
                                }
                            }
                            mRiskRecyclerAdapter.notifyDataSetChanged();
                        }
                });
}
    public void speakRecord(DfRecord record, boolean last){
        ArrayList<String> rice_variety = record.getRiceVariety();
        String rv_string = "";
        for(String rv : rice_variety){
            rv_string = rv_string+" "+rv;
        }
        ArrayList<String> nitrogen_type = record.getNitrogenType();
        String nt_string = "";
        for(String nt : nitrogen_type){
            nt_string = nt_string+" "+nt;
        }
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("EEE, MMM d, ''yyyy");
        String plant_date = simpleDateFormat.format(record.getPlantDate());
        String create_date = simpleDateFormat.format(record.getCreateDate());
        String text = getResources().getString(R.string.df_log_speak_1)
                + create_date + ","
                + getResources().getString(R.string.df_log_pd)
                + plant_date + ","
                + getResources().getString(R.string.df_log_rv)
                + rv_string + ","
                + getResources().getString(R.string.df_log_nf)
                + nt_string + ","
                + getResources().getString(R.string.last_fragment_title)
                + record.getSeedingRate()
                + getResources().getString(R.string.df_unit_speak)
                + ","
                + getResources().getString(R.string.fifth_fragment_title)
                + record.getNitrogenAmount()
                + getResources().getString(R.string.df_unit_speak)
                + ".";
        if(last){
            text += getResources().getString(R.string.df_log_speak_2);
        }
        speakText(text);

    }
    public void confirmDelete(final DfRecord record, final int position){
        mDialog = new CustomDialog(DfHistoryActivity.this);
        mDialog.setTitle(getResources().getString(R.string.df_delete_title));
        mDialog.setMessage(getResources().getString(R.string.df_delete_message));
        mDialog.setYesOnclickListener(getResources().getString(R.string.dialog_ok), new CustomDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                mDialog.dismiss();
                deleteRecord(record, position);
            }
        });
        mDialog.setNoOnclickListener(getResources().getString(R.string.dialog_cancel), new CustomDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
    public void deleteRecord(DfRecord record, final int position){
        DocumentReference recordsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("dfrecords")
                .document(record.getRecordId());
        if(position==0){
            cannotDeleteDialog();
        }else {
            recordsRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            recordsFromDb.remove(position);
                            mRecordRecyclerAdapter.notifyItemRemoved(position);
                            mRecordRecyclerAdapter.notifyItemRangeChanged(position, mRecordRecyclerAdapter.getItemCount());
                            setLastRecord();
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    });
        }
    }
    public void cannotDeleteDialog(){
        aDialog = new CustomDialog(DfHistoryActivity.this);
        aDialog.setTitle(getResources().getString(R.string.df_no_delete_title));
        aDialog.setMessage(getResources().getString(R.string.df_no_delete_message));
        aDialog.setYesOnclickListener(getResources().getString(R.string.dialog_ok), new CustomDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                aDialog.dismiss();
            }
        });
        aDialog.setNoOnclickListener(getResources().getString(R.string.dialog_cancel), new CustomDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                aDialog.dismiss();
            }
        });
        aDialog.show();
    }

    public void setupAlarm(){
        Date when = new Date(System.currentTimeMillis());
        try{
            Intent intent = new Intent(this, UpdateReceiver.class); // intent to be launched
            intent.setAction("setUpAlarm");

            // note this could be getActivity if you want to launch an activity
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0, // id, optional
                    intent, // intent to launch
                    PendingIntent.FLAG_CANCEL_CURRENT); // PendintIntent flag
            Log.e(TAG, "alarm set up");
            long repeatInterval = 24*60*60*1000; //AlarmManager.INTERVAL_DAY;
            long triggerTime = SystemClock.elapsedRealtime()
                    + repeatInterval;

            AlarmManager alarms = (AlarmManager) this.getSystemService(
                    Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= 19) {
                alarms.setWindow(AlarmManager.RTC_WAKEUP, triggerTime, repeatInterval, pendingIntent);}
            else{
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    repeatInterval,
                    pendingIntent);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
