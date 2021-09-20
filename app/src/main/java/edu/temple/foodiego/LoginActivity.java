package edu.temple.foodiego;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUserName,
            editTextPassword,
            editTextFirstName,
            editTextLastName;
    private Button
            buttonOK,
            buttonRegisterOrCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextUserName = findViewById(R.id.etLogInUserName);
        editTextPassword = findViewById(R.id.etLogInPassword);
        editTextFirstName = findViewById(R.id.etLogInFName);
        editTextLastName = findViewById(R.id.etLogInLName);
        buttonOK = findViewById(R.id.btnOK);
        buttonRegisterOrCancel = findViewById(R.id.btnRegOrCan);

        buttonOK.setOnClickListener(v -> {
            finish();
        });

    }
}