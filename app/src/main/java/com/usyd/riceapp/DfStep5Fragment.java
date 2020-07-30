package com.usyd.riceapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Set;


public class DfStep5Fragment extends Fragment implements View.OnClickListener{
    ImageButton speaker;
    TextView tv_amount;
    SeekBar amount;

    public DfStep5Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_df_step5, container, false);
        speaker = v.findViewById(R.id.speaker_df_step5);
        tv_amount = v.findViewById(R.id.tv_step5_nf);
        amount = v.findViewById(R.id.seekBar_step5);
        amount.setOnSeekBarChangeListener(seekBarChangeListener);
        speaker.setOnClickListener(this);
        int progress = amount.getProgress();
        updateData(progress);
        tv_amount.setText(progress + getResources().getString(R.string.df_unit));
        ((BaseActivity)getActivity()).initTextToSpeech();
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
        switch (v.getId()){
            case R.id.speaker_df_step5:
                Log.e("Step5","textTo Speech");
                ((BaseActivity)getActivity()).speakText(getResources().getString(R.string.fifth_fragment_speak));
                break;

        }
    }
    private void updateData(int amount){
        PreferenceManager.getDefaultSharedPreferences(this.getActivity())
                .edit()
                .putInt("nitrogen_amount", amount)
                .apply();
    }
}
