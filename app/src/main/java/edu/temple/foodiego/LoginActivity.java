package edu.temple.foodiego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

public class LoginActivity extends AppCompatActivity implements OnMapReadyCallback{
    Intent[] intents = new Intent[10];
    GoogleMap  googleMap;
    MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intents[0] = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intents[0]);

        //Map
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync( this);
    }
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        //googleMap1.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0,0), 18.0f));
        mapView.onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
}