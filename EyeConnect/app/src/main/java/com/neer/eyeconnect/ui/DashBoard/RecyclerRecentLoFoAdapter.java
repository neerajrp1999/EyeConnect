package com.neer.eyeconnect.ui.DashBoard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.neer.eyeconnect.R;

import java.util.ArrayList;

public class RecyclerRecentLoFoAdapter extends RecyclerView.Adapter<RecyclerRecentLoFoAdapter.ViewHolder> {
    private Context context;
    private ArrayList<DashBoardViewModel> arr_recent_lofo;
    private OnItemClickListener onItemClickListener;
    public RecyclerRecentLoFoAdapter(Context context, ArrayList<DashBoardViewModel> arr_recent_lofo) {
        this.arr_recent_lofo = arr_recent_lofo;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(DashBoardViewModel item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_lofo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DashBoardViewModel recentItems = arr_recent_lofo.get(position);

        Glide.with(context)
                .load(recentItems.getImageURI())
                .error(R.drawable.sample_img)
                .into(holder.imageURI);

        holder.description.setText(recentItems.getDescription());

        holder.tag.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_light));
        holder.tag.setText(recentItems.getTag());
        holder.name.setText(recentItems.getName());
        holder.date.setText(recentItems.getDateLost());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(recentItems);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arr_recent_lofo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, tag,date;
        ImageView imageURI;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            description = itemView.findViewById(R.id.description);
            imageURI = itemView.findViewById(R.id.img_lofo_recent);
            tag = itemView.findViewById(R.id.tag);
        }
    }
}