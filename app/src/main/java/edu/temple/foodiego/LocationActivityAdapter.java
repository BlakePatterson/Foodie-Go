package edu.temple.foodiego;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LocationActivityAdapter extends RecyclerView.Adapter<LocationActivityAdapter.ViewHolder>{

    private ArrayList<FoodieActivityLog> items;

    public LocationActivityAdapter(ArrayList<FoodieActivityLog> activityLogs) {
        this.items = activityLogs;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView timeTextView;
        private final TextView actionTextView;

        public ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.nameTextView);
            timeTextView = view.findViewById(R.id.timeTextView);
            actionTextView = view.findViewById(R.id.actionTextView);
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getTimeTextView() {
            return timeTextView;
        }

        public TextView getActionTextView() {
            return actionTextView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_activity_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getNameTextView().setText(items.get(position).getUser().getUsername());
        holder.getTimeTextView().setText(items.get(position).getTime().toLocalDate() +" "
                + items.get(position).getTime().getHour()+":"
                + items.get(position).getTime().getMinute()+":"
                + items.get(position).getTime().getSecond());
        holder.getActionTextView().setText(items.get(position).getAction());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
