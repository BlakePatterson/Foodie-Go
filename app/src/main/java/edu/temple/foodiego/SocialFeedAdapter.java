package edu.temple.foodiego;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SocialFeedAdapter extends RecyclerView.Adapter<SocialFeedAdapter.SocialItemViewHolder>{
    ArrayList<FoodieActivityLog> data;

    public SocialFeedAdapter(ArrayList<FoodieActivityLog> data){
        this.data = data;
    }
    @NonNull
    @Override
    public SocialItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_social_item, parent, false);
        return new SocialItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialItemViewHolder holder, int position) {
        holder.getName().setText(data.get(position).getUser().getUsername());
        holder.getAction().setText(data.get(position).getAction());
        holder.getLocation().setText(data.get(position).getLocation().getName());
        LocalDateTime time = data.get(position).getTime();
        String display = time.getMonth().name() + " " + time.getDayOfMonth() + " " + time.getHour() + ":" + time.getMinute();
        holder.getTime().setText(display);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public static class SocialItemViewHolder extends RecyclerView.ViewHolder{
        private final TextView name;
        private final TextView action;
        private final TextView location;
        private final TextView time;
        public SocialItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.nameTextView);
            action = (TextView) itemView.findViewById(R.id.actionTextView);
            location = (TextView) itemView.findViewById(R.id.locationTextView);
            time = (TextView) itemView.findViewById(R.id.timeTextView);
        }
        public TextView getAction() {
            return action;
        }
        public TextView getName(){
            return name;
        }
        public TextView getLocation() {
            return location;
        }
        public TextView getTime() {
            return time;
        }
    }
}