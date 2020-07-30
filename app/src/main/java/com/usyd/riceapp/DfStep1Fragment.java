package com.usyd.riceapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.util.TimeUtils;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class DfStep1Fragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    ImageButton speaker, datePicker;
    TextView dateResult;

    public DfStep1Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_df_step1, container, false);

        speaker = v.findViewById(R.id.speaker_df_step1);
        datePicker = v.findViewById(R.id.btn_df_date);
        dateResult = v.findViewById(R.id.df_step1_text_date);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        month +=1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String date = day + "/" + month + "/" + year;
        dateResult.setText(date);
        setDate(date);
        datePicker.setOnClickListener(this);
        speaker.setOnClickListener(this);
//        ((BaseActivity)getActivity()).initTextToSpeech();

        return v;
    }
    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this.getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()- TimeUnit.DAYS.toMillis(90));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        String date = dayOfMonth + "/" + month + "/" + year;
        dateResult.setText(date);
        setDate(date);
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.speaker_df_step1:
                Log.e("Step1","textTo Speech");
                ((BaseActivity)getActivity()).speakText(getResources().getString(R.string.first_fragment_speak));
                break;

            case R.id.btn_df_date:
                showDatePickerDialog();
                break;
        }
    }

    private void setDate(String plantDate){
        PreferenceManager.getDefaultSharedPreferences(this.getActivity())
                .edit()
                .putString("plantDate", plantDate)
                .apply();
    }
}
