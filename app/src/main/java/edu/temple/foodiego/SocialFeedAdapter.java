package edu.temple.foodiego;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        String s = data.get(position).getUser().getUsername() + " @ " + data.get(position).getLocation().getName();
        holder.getSummaryTextView().setText(s);
        holder.getDetailsTextView().setText(data.get(position).getActivityLogMessage());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public static class SocialItemViewHolder extends RecyclerView.ViewHolder{
        private final TextView summary;
        private final TextView details;
        public SocialItemViewHolder(@NonNull View itemView) {
            super(itemView);
            summary = (TextView) itemView.findViewById(R.id.summaryTextView);
            details = (TextView) itemView.findViewById(R.id.detailsTextView);
        }
        public TextView getDetailsTextView() {
            return details;
        }
        public TextView getSummaryTextView(){
            return summary;
        }
    }
}