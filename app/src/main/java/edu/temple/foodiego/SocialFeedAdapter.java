package edu.temple.foodiego;

import android.content.Context;
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
        holder.getTextView().setText(data.get(position).getActivityLogMessage());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public static class SocialItemViewHolder extends RecyclerView.ViewHolder{
        private final TextView text;
        public SocialItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.socialTextView);
        }

        public TextView getTextView() {
            return text;
        }
    }
}