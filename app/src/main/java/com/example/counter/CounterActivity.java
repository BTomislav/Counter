package com.example.counter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

public class CounterActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v->{
            sharedPref = getSharedPreferences("LoginInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("token");
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}