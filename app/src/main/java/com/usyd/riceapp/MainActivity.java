package com.usyd.riceapp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.usyd.riceapp.notification.UpdateReceiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;


public class MainActivity extends BaseActivity {
    private static String TAG = "MainActivity";
    private static String CHANNEL = "DiseaseForecastAlarm";
    ImageButton btn_df, btn_lt, btn_fs, btn_ml;
    ImageButton speaker_df, speaker_lt, speaker_fs, speaker_ml;
    FirebaseFirestore mDb;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        this.drawer.addView(contentView, 0);
        setPagename(MainActivity.this, getResources().getString(R.string.menu_home));
//        setLanguage();

        initTextToSpeech();
        createNotificationChannel();

        btn_df = findViewById(R.id.btn_df);
        btn_lt = findViewById(R.id.btn_lt);
        btn_fs = findViewById(R.id.btn_fs);
        btn_ml = findViewById(R.id.btn_ml);

        speaker_df = findViewById(R.id.speaker_df);
        speaker_lt = findViewById(R.id.speaker_lt);
        speaker_fs = findViewById(R.id.speaker_fs);
        speaker_ml = findViewById(R.id.speaker_ml);

        speaker_df.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakText(getResources().getString(R.string.menu_df));
            }
        });

        speaker_lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakText(getResources().getString(R.string.menu_lt));
            }
        });

        speaker_fs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakText(getResources().getString(R.string.menu_fs));
            }
        });

        speaker_ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakText(getResources().getString(R.string.menu_ml));
            }
        });
        
        btn_df.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRecordsFromFireStore();
            }
        });
        btn_ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MLIntent = new Intent(MainActivity.this , PhotoMLActivity.class);
                startActivity(MLIntent);
            }
        });
        btn_lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LTIntent = new Intent(MainActivity.this , TutLearningActivity.class);
                startActivity(LTIntent);
            }
        });

        }
    public void getRecordsFromFireStore() {
        CollectionReference DfrecordsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("dfrecords");
        DfrecordsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        Intent DFHIntent = new Intent(MainActivity.this, DfHistoryActivity.class);
                        startActivity(DFHIntent);
                    } else {
                        Intent DFIntent = new Intent(MainActivity.this, DiseaseForecastActivity.class);
                        startActivity(DFIntent);
                    }
                }
            }
        });
    }


        private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= (Build.VERSION_CODES.O)){
            CharSequence name = "DiseaseForecastAlarmChannel";
            String description = "Channel for Disease Forecast Risk Alarm";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            }
        }
}
