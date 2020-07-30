package com.usyd.riceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DfRiskItemAdapter extends RecyclerView.Adapter<DfRiskItemAdapter.MyViewHolder> {
        private static final String TAG = "Disease forecast history Adapter";
        private ArrayList<DfRiskRecord> mRecords;

        @NonNull
        @Override
        public DfRiskItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.df_item_risk, parent, false);
            final DfRiskItemAdapter.MyViewHolder holder = new DfRiskItemAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull DfRiskItemAdapter.MyViewHolder holder, int position) {
            Date predictDate = mRecords.get(position).getPredictDate();
            Date plantDate = mRecords.get(position).getPlantDate();
            long diff = predictDate.getTime() - plantDate.getTime();
            int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            holder.log_risk_days.setText(String.valueOf(days));
            int humidity = mRecords.get(position).getHumidity();
            holder.log_risk_humidity.setText(String.valueOf(humidity)+"%");
            double temp = mRecords.get(position).getTemp();
            holder.log_risk_temp.setText(String.valueOf(temp)+"°C");
            double rainfall = mRecords.get(position).getRainfall();
            holder.log_risk_rain.setText(String.valueOf(rainfall)+"mm");
            String risk = mRecords.get(position).getRisk();
            holder.log_risk_risk.setText(risk);
            if(risk.equals("high")){
                holder.log_risk_risk
                        .setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.risk_high));
            }else if(risk.equals("medium")){
                holder.log_risk_risk
                        .setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.risk_medium));
            }else if(risk.equals("low")){
                holder.log_risk_risk
                        .setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.risk_low));
            }
        }
        public DfRiskItemAdapter(ArrayList<DfRiskRecord> records)
        {
            mRecords = records;
        }

        @Override
        public int getItemCount() {
            return mRecords.size();
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder{
            public TextView log_risk_days, log_risk_humidity, log_risk_temp, log_risk_rain, log_risk_risk;
//            public ImageButton btn_speaker, btn_delete;

            public MyViewHolder(final View v) {
                super(v);
                log_risk_days = v.findViewById(R.id.log_risk_days);
                log_risk_humidity = v.findViewById(R.id.log_risk_humidity);
                log_risk_temp = v.findViewById(R.id.log_risk_temp);
                log_risk_rain = v.findViewById(R.id.log_risk_rain);
                log_risk_risk = v.findViewById(R.id.log_risk_risk);
            }
        }
}
