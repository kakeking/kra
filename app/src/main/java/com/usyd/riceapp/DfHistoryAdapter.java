package com.usyd.riceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DfHistoryAdapter extends RecyclerView.Adapter<DfHistoryAdapter.MyViewHolder> {
    private static final String TAG = "Disease forecast history Adapter";
    private ArrayList<DfRecord> mRecords;
    private static DfHistoryAdapterListener onClickListener;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.df_item_history, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Date plantDate = mRecords.get(position).getPlantDate();
        Date createDate = mRecords.get(position).getCreateDate();
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String plant_date = simpleDateFormat.format(plantDate);
        holder.log_plant_date.setText(plant_date);
        String create_date = simpleDateFormat2.format(createDate);
        holder.log_create_date.setText(create_date);
        ArrayList<String> rice_variety = mRecords.get(position).getRiceVariety();
        String rv_string = "";
        for(String rv : rice_variety){
            rv_string = rv_string+" "+rv;
        }
        holder.log_rice_variety.setText(rv_string);
        ArrayList<String> nitrogen_type = mRecords.get(position).getNitrogenType();
        String nt_string = "";
        for(String nt : nitrogen_type){
            nt_string = nt_string+" "+nt;
        }
        holder.log_nitrogen_type.setText(nt_string);
        int nitrogen_amount = mRecords.get(position).getNitrogenAmount();
        holder.log_nitrogen_amount.setText(nitrogen_amount+holder.itemView.getContext().getResources().getString(R.string.df_unit));
        int seeding_rate = mRecords.get(position).getSeedingRate();
        holder.log_seed_rate.setText(seeding_rate+holder.itemView.getContext().getResources().getString(R.string.df_unit));
        String location = mRecords.get(position).getLocation_name();
        holder.log_location.setText(location);
    }
    public DfHistoryAdapter(ArrayList<DfRecord> records, DfHistoryAdapterListener listener)
    {
        mRecords = records;
        onClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public CardView mCardView;
        public TextView log_create_date, log_plant_date,
                log_rice_variety,log_nitrogen_type, log_seed_rate,
                log_nitrogen_amount, log_location;
        public ImageButton btn_speaker, btn_delete;

        public MyViewHolder(final View v) {
            super(v);
            mCardView = v.findViewById(R.id.history_card_view);
            log_create_date = v.findViewById(R.id.df_log_create_date);
            log_plant_date = v.findViewById(R.id.df_log_plant_date);
//            log_predict_date = v.findViewById(R.id.df_log_predict_date);
            log_rice_variety = v.findViewById(R.id.df_log_rv);
            log_nitrogen_type = v.findViewById(R.id.df_log_nt);
            log_seed_rate = v.findViewById(R.id.df_log_sr);
            log_nitrogen_amount = v.findViewById(R.id.df_log_na);
            log_location = v.findViewById(R.id.df_log_location);
//            log_risk = v.findViewById(R.id.df_log_risk);
            btn_speaker = v.findViewById(R.id.df_log_speaker);
            btn_delete = v.findViewById(R.id.df_log_delete);
            btn_speaker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.iconSpeakerOnClick(v, getAdapterPosition());
                }
            });
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.iconDeleteOnClick(v, getAdapterPosition());
                }
            });
        }
    }
    public interface DfHistoryAdapterListener{
        void iconSpeakerOnClick(View v, int position);
        void iconDeleteOnClick(View v, int position);
    }

}
