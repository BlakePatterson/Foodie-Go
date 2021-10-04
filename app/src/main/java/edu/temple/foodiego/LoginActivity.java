package edu.temple.foodiego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void onLoginClicked()
    {

    }

    private void onRegisterClicked()
    {

    }

    private void onSuccessLoginOrRegister()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", "userInfo");
        startActivity(intent);
    }

}