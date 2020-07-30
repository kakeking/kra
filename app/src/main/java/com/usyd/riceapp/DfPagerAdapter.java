package com.usyd.riceapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DfPagerAdapter extends FragmentStateAdapter {

    int NUM_PAGE = 6;

    public DfPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new DfStep1Fragment();
            case 1:
                return new DfStep2Fragment();
            case 2:
                return new DfStep3Fragment();
            case 3:
                return new DfStep4Fragment();
            case 4:
                return new DfStep5Fragment();
            default:
                return new DfStep6Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGE;
    }
}
