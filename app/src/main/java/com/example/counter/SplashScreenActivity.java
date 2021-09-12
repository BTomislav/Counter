package com.example.counter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {

    private final OkHttpClient client = new OkHttpClient();
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SharedPreferences sharedPref = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String token = sharedPref.getString("token", "");

        Request request = new Request.Builder()
                .url("https://scoreboard-counter.azurewebsites.net/v1/auth/check-token")
                .addHeader("Authorization", "BEARER "+token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "Couldn't connect to database", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int responseCode = response.code();
                if (responseCode==200){
                    intent = new Intent(getBaseContext(), CounterActivity.class);
                }
                else
                {
                    intent = new Intent(getBaseContext(), LoginActivity.class);
                }
                startActivity(intent);
            }
        });
    }
}