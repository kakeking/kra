package com.usyd.riceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Set;


public class DfStep6Fragment extends Fragment implements View.OnClickListener {
    ImageButton speaker;
    Button submit;
    TextView tv_amount;
    SeekBar amount;
    CustomDialog mDialog;
    String alert_message;

    public DfStep6Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_df_step6, container, false);
        speaker = v.findViewById(R.id.speaker_df_step6);
        speaker.setOnClickListener(this);
        submit = v.findViewById(R.id.df_submit);
        submit.setOnClickListener(this);
        tv_amount = v.findViewById(R.id.tv_step6_rv);
        amount = v.findViewById(R.id.seekBar_step6);
        amount.setOnSeekBarChangeListener(seekBarChangeListener);
        int progress = amount.getProgress();
        updateData(progress);
        tv_amount.setText(progress + getResources().getString(R.string.df_unit));
//        ((DiseaseForecastActivity)getActivity()).stepFinish(4);
        return v;
    }
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            tv_amount.setText(progress + getResources().getString(R.string.df_unit));
            ((BaseActivity) getActivity()).speakText(progress + getResources().getString(R.string.df_unit_speak));
            updateData(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.speaker_df_step6:
                Log.e("Step6", "text To Speech");
                ((BaseActivity) getActivity()).speakText(getResources().getString(R.string.last_fragment_speak));
                break;
            case R.id.df_submit:
                boolean valid = validateData();
                if(valid){
                goToDfResult();
                }else {
                    showDialog(alert_message);
                }
                break;
        }
    }
    private void updateData(int amount){
        PreferenceManager.getDefaultSharedPreferences(this.getActivity())
                .edit()
                .putInt("seeding_rate", amount)
                .apply();
    }

    private void goToDfResult() {
        Intent resultIntent =  new Intent(getActivity(), DfResultActivity.class);
        startActivity(resultIntent);
    }

    private boolean validateData(){
        boolean valid=true;
        alert_message = getResources().getString(R.string.df_alert_first);
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        double lat =  mPref.getFloat("location_lat", 0);
        double lon = mPref.getFloat("location_lon", 0);
        Set<String> rice_variety = mPref.getStringSet("rice_variety", null);
        Set<String> nitrogen_type = mPref.getStringSet("nitrogen_type", null);
        if(lat==0 && lon==0){
            valid=false;
            alert_message = alert_message+"\r\n"+getResources().getString(R.string.second_fragment_label)+": "
                    +getResources().getString(R.string.second_fragment_title);
        }
        if(rice_variety==null||rice_variety.isEmpty()){
            alert_message = alert_message+"\r\n"+getResources().getString(R.string.third_fragment_label)+": "
                    +getResources().getString(R.string.third_fragment_title);
            valid=false;
        }
        if(nitrogen_type==null||nitrogen_type.isEmpty()){
            alert_message = alert_message+"\r\n"+getResources().getString(R.string.fourth_fragment_label)+": "
                    +getResources().getString(R.string.fourth_fragment_title);
            valid=false;
        }
        return valid;
    }

    private void showDialog(String message){
        mDialog = new CustomDialog(getActivity());
        mDialog.setTitle(getResources().getString(R.string.df_alert_title));
        mDialog.setMessage(message);
        mDialog.setYesOnclickListener(getResources().getString(R.string.dialog_cancel), new CustomDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                mDialog.dismiss();
            }
        });
        mDialog.setNoOnclickListener(getResources().getString(R.string.dialog_ok), new CustomDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        ((BaseActivity) getActivity()).shutdownTts();
    }
}
