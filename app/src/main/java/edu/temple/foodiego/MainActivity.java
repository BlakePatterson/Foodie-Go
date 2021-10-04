package edu.temple.foodiego;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    private void onLogout()
    {
        //remove user session
        this.finish();

    }

    @Override
    public void onBackPressed() {
        //send logout request
        //received response then go back
        super.onBackPressed();
    }

    private void onFoodieLocationClicked()
    {
        Intent intent = new Intent(this, LocationDetailActivity.class);
        intent.putExtra("LocationDetail", "LocationDetail");
        startActivity(intent);
    }



    private boolean hasGPSPermission()
    {
        return true;
    }

    private void loadFragments()
    {}

    private void startLocationService()
    {}

    private void stopLocationService()
    {

    }

    private boolean isLocationServiceRunning()
    {
     return true;
    }

    private void createNotificationChannel()
    {

    }

    private void updateLocation(Location location)
    {

    }
}