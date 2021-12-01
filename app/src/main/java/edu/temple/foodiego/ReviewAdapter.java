package edu.temple.foodiego;

import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    ArrayList<FoodieReview> items;

    public ReviewAdapter(ArrayList<FoodieReview> reviews) {
        this.items = reviews;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final RatingBar ratingBar;
        private final TextView reviewMessage;

        public ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.nameTextView);
            ratingBar = view.findViewById(R.id.reviewRatingBar);
            reviewMessage = view.findViewById(R.id.reviewMessageTextView);
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public RatingBar getRatingBar() {
            return ratingBar;
        }

        public TextView getReviewMessage() {
            return reviewMessage;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getNameTextView().setText(items.get(position).getUser().getUsername());
        holder.getRatingBar().setRating((float) items.get(position).getRating());
        holder.getReviewMessage().setText(items.get(position).getReview());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
