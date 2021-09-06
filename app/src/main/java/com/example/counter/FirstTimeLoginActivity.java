package com.example.counter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

public class FirstTimeLoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_login);

        Button button = findViewById(R.id.button2);
        button.setOnClickListener(v -> {
            sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("token");
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}