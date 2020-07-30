package com.usyd.riceapp;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DfRecyclerAdapter extends RecyclerView.Adapter<DfRecyclerAdapter.ViewHolder>{
    private Context ctx;
    private List<DfListItem> items;
    private OnClickListener onClickListener = null;
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public CircleImageView image;
        public RelativeLayout lyt_checked, lyt_image;
        public View lyt_parent;

        public ViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.item_name);
            image =  view.findViewById(R.id.image);
            lyt_checked = view.findViewById(R.id.lyt_checked);
            lyt_image = view.findViewById(R.id.lyt_image);
            lyt_parent = view.findViewById(R.id.lyt_parent);
        }
    }
    public DfRecyclerAdapter(Context mContext, List<DfListItem> items){
        this.ctx = mContext;
        this.items = items;
        selected_items = new SparseBooleanArray();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.df_item_recycler, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final DfListItem listItem = items.get(position);
        holder.title.setText(listItem.display_name);
        holder.lyt_parent.setActivated(selected_items.get(position, false));

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                onClickListener.onItemClick(v, listItem, position);
            }
        });
        toggleCheckedIcon(holder, position);
        displayImage(holder, listItem);
    }

    private void displayImage(ViewHolder holder, DfListItem item) {
        if (item.image != null) {
            holder.image.setImageResource(item.image);
            holder.image.setColorFilter(null);
        } else {
            holder.image.setImageResource(R.drawable.df_shape_circle);
//            holder.image.setColorFilter(R.color.colorGrey);
        }
    }

    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.lyt_image.setVisibility(View.GONE);
            holder.lyt_checked.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            holder.lyt_checked.setVisibility(View.GONE);
            holder.lyt_image.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }
    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }
        Log.e("toggleSelection",selected_items.toString());
        notifyItemChanged(pos);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public DfListItem getItem(int position) {
        return items.get(position);
    }
    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        items.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public interface OnClickListener {
        void onItemClick(View view, DfListItem obj, int pos);

    }

}
