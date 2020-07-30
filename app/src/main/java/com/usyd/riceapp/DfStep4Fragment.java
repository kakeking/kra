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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class DfStep4Fragment extends Fragment {
    private RecyclerView recyclerView;
    private DfRecyclerAdapter mAdapter;
    private List<DfListItem> items = new ArrayList<>();
    private Set<String> types = new LinkedHashSet<>();

    public DfStep4Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_df_step4, container, false);
        initComponent(v);
        return v;
    }
    private void initComponent(View v){
        recyclerView = v.findViewById(R.id.recyclerStep4);
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
                if(!types.contains(item.name)){
                    types.add(item.name);
                }else{
                    types.remove(item.name);
                }
                ((BaseActivity) getActivity()).speakText(item.name);
                Log.e("step 4", "selected item: "+item.name +mAdapter.getSelectedItemCount());
                updateData(types);
            }
        });
        addItems();

    }
    private void addItems(){

        items.clear();
        items.add(new DfListItem(getResources().getString(R.string.df_step4_item1_name),
                ((BaseActivity) getActivity()).getStringByLocal(this.getActivity(), R.string.df_step4_item1_name, "en"), null));
        items.add(new DfListItem(getResources().getString(R.string.df_step4_item2_name),
                ((BaseActivity) getActivity()).getStringByLocal(this.getActivity(), R.string.df_step4_item2_name, "en"), null));
        items.add(new DfListItem(getResources().getString(R.string.df_step4_item3_name),
                ((BaseActivity) getActivity()).getStringByLocal(this.getActivity(), R.string.df_step4_item3_name, "en"), null));
        mAdapter.notifyDataSetChanged();
    }
    private void updateData(Set<String> types){
        PreferenceManager.getDefaultSharedPreferences(this.getActivity())
                .edit()
                .putStringSet("nitrogen_type", types)
                .apply();
    }
}
