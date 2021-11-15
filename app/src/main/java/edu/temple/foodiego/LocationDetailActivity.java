package edu.temple.foodiego;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class LocationDetailActivity extends AppCompatActivity {
    final String location_database = "location";
    TextView foodieName;
    Button reviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        Intent receivedIntent= getIntent();
        String s = receivedIntent.getStringExtra("data");

        foodieName = findViewById(R.id.TVLocationName);
        foodieName.setText(s);
        reviewButton = findViewById(R.id.launchReviewButton);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLeaveReviewDialog();
            }
        });
    }
    public void openLeaveReviewDialog(){
        new AlertDialog.Builder(this).setView(R.layout.dialog_leave_review)
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog d = (Dialog) dialog;
                        RatingBar bar = d.findViewById(R.id.ratingBar);
                        EditText reviewBox = d.findViewById(R.id.reviewBox);
                        double rating = bar.getRating();
                        String review = reviewBox.getText().toString();
                        if(review.equals("") || rating == 0){
                            Toast.makeText(LocationDetailActivity.this, "Please fill in both the rating and the review.", Toast.LENGTH_LONG).show();
                        }else{
                            //call postReview()
                            d.dismiss();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}