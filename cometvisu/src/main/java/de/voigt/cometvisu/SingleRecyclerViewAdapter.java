package de.voigt.cometvisu;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class SingleRecyclerViewAdapter extends RecyclerView.Adapter<SingleRecyclerViewAdapter.DataObjectHolder> {
 
    List<Map<String, Object>> urlsMap;
    private static SingleClickListener sClickListener;
    private static int sSelected = -1;
 
    public SingleRecyclerViewAdapter(List<Map<String, Object>> urlsMap) {
        this.urlsMap = urlsMap;
    }
 
    static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
 
        RadioButton mRadioButton;
 
        public DataObjectHolder(View itemView) {
            super(itemView);
            this.mRadioButton = itemView.findViewById(R.id.selectRadioButton);
            itemView.setOnClickListener(this);
        }
 
        @Override
        public void onClick(View view) {
            sSelected = getAdapterPosition();
            sClickListener.onItemClickListener(getAdapterPosition(), view);
        }
    }
 
    public void selectedItem() {
        notifyDataSetChanged();
    }
 
    void setOnItemClickListener(SingleClickListener clickListener) {
        sClickListener = clickListener;
    }
 
    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_single_check, parent, false);
 
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }
 
    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.mRadioButton.setText((String)urlsMap.get(position).get("url"));
 
        //if (sSelected == position) {
        if ((Boolean)urlsMap.get(position).get("checked")) {
            holder.mRadioButton.setChecked(true);
        } else {
            holder.mRadioButton.setChecked(false);
        }
 
    }
 
    @Override
    public int getItemCount() {
        return urlsMap.size();
    }
 
    interface SingleClickListener {
        void onItemClickListener(int position, View view);
    }
 
}