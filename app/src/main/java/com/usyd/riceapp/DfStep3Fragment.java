package com.usyd.riceapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class DfStep3Fragment extends Fragment {
    private RecyclerView recyclerView;
    private DfRecyclerAdapter mAdapter;
    private List<DfListItem> items = new ArrayList<>();
    private Set<String> variety = new LinkedHashSet<>();

    public DfStep3Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_df_step3, container, false);
        initComponent(v);
        return v;
    }

    private void initComponent(View v){
        recyclerView = v.findViewById(R.id.recyclerStep3);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        mAdapter = new DfRecyclerAdapter(this.getContext(), items);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new DfRecyclerAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, DfListItem obj, int pos) {
                    mAdapter.toggleSelection(pos);
                    // read the item which removes bold from the row
                    DfListItem item = mAdapter.getItem(pos);
                    if(!variety.contains(item.name)){
                        variety.add(item.name);
                        updateData(variety);
                    }else{
                        variety.remove(item.name);
                        updateData(variety);
                        Log.e("step 3", "varieties "+variety +mAdapter.getSelectedItemCount());
                    }
                    ((BaseActivity) getActivity()).speakText(item.name);
                    Log.e("step 3", "selected item: "+item.name +mAdapter.getSelectedItemCount());
                }
        });
        addItems();
    }

    private void addItems(){

        items.clear();
        items.add(new DfListItem(getResources().getString(R.string.df_step3_item1_name),
                ((BaseActivity) getActivity()).getStringByLocal(this.getActivity(), R.string.df_step3_item1_name, "en"),null));
        items.add(new DfListItem(getResources().getString(R.string.df_step3_item2_name),
                ((BaseActivity) getActivity()).getStringByLocal(this.getActivity(), R.string.df_step3_item2_name, "en"), null));
        items.add(new DfListItem(getResources().getString(R.string.df_step3_item3_name),
                ((BaseActivity) getActivity()).getStringByLocal(this.getActivity(), R.string.df_step3_item3_name, "en"),null));
        items.add(new DfListItem(getResources().getString(R.string.df_step3_item4_name),
                ((BaseActivity) getActivity()).getStringByLocal(this.getActivity(), R.string.df_step3_item4_name, "en"), null));
        items.add(new DfListItem(getResources().getString(R.string.df_step3_item5_name),
                ((BaseActivity) getActivity()).getStringByLocal(this.getActivity(), R.string.df_step3_item5_name, "en"), null));
        items.add(new DfListItem(getResources().getString(R.string.df_step3_item6_name),
                ((BaseActivity) getActivity()).getStringByLocal(this.getActivity(), R.string.df_step3_item6_name, "en"), null));

        mAdapter.notifyDataSetChanged();
    }
    private void updateData(Set<String> varieties){
        PreferenceManager.getDefaultSharedPreferences(this.getActivity())
                .edit()
                .putStringSet("rice_variety", varieties)
                .apply();
    }
}
