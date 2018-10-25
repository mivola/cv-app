package de.voigt.cometvisu;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;
import java.util.Map;

public class SingleRecyclerViewAdapter extends RecyclerSwipeAdapter<SingleRecyclerViewAdapter.DataObjectHolder> {
 
    List<Map<String, Object>> urlsMap;
    private static SingleClickListener sClickListener;
 
    public SingleRecyclerViewAdapter(List<Map<String, Object>> urlsMap) {
        this.urlsMap = urlsMap;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.urlSwipeLayout;
    }

    static class DataObjectHolder extends RecyclerView.ViewHolder {
 
        private RadioButton mRadioButton;
        private SwipeLayout swipeLayout;
 
        public DataObjectHolder(View itemView) {
            super(itemView);
            this.mRadioButton = itemView.findViewById(R.id.selectRadioButton);
            this.swipeLayout = itemView.findViewById(R.id.urlSwipeLayout);
            ImageView deleteIcon = itemView.findViewById(R.id.delete_icon);
            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("---","delete!");
                }
            });
            
            
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.bottom_wrapper));
            
            swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sClickListener.onItemClickListener(getAdapterPosition(), view);
                }
            });

        /*    
            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    //when the SurfaceView totally cover the BottomView.
                }
    
                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                   //you are swiping.
                }
    
                @Override
                public void onStartOpen(SwipeLayout layout) {
    
                }
    
                @Override
                public void onOpen(SwipeLayout layout) {
                   //when the BottomView totally show.
                }
    
                @Override
                public void onStartClose(SwipeLayout layout) {
    
                }
    
                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                   //when user's hand released.
                }
            });
       */     
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