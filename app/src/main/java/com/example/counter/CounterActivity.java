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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class CounterActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private String responseBody;
    private Request request;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        sharedPref = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        TextView count = findViewById(R.id.textCount);
        Button buttonLogout = findViewById(R.id.buttonLogout);
        Button buttonAdd = findViewById(R.id.buttonAdd);

        request = new Request.Builder()
                .url("https://api.jurmanovic.com/clicker/v1/count")
                .header("Authorization", "BEARER " + token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                responseBody = Objects.requireNonNull(response.body()).string();
                try {
                    JSONObject obj = new JSONObject(responseBody);
                    System.out.println(obj.getString("count"));
                    runOnUiThread(()-> {
                        try {
                            count.setText(obj.getString("count"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonAdd.setOnClickListener(v->{

            FormBody formBody = new FormBody.Builder()
                        .build();

            request = new Request.Builder()
                        .url("https://api.jurmanovic.com/clicker/v1/count")
                        .header("Authorization", "Bearer "+token)
                        .post(formBody)
                        .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                }
            });

            request = new Request.Builder()
                    .url("https://api.jurmanovic.com/clicker/v1/count")
                    .header("Authorization", "Bearer "+token)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    responseBody = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject obj = new JSONObject(responseBody);
                        System.out.println(obj.getString("count"));
                        runOnUiThread(()-> {
                            try {
                                count.setText(obj.getString("count"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        /*Request request = new Request.Builder()
                .url("https://scoreboard-counter.azurewebsites.net/v1/auth")
                .header("Authorization", "BEARER "+token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    System.out.println(responseBody);
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        });

        Request request1 = new Request.Builder()
                .url("https://scoreboard-counter.azurewebsites.net/v1/score")
                .header("Authorization", "BEARER "+token)
                .get()
                .build();

        client.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    System.out.println(responseBody);
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        });*/



        buttonLogout.setOnClickListener(v->{
            sharedPref = getSharedPreferences("LoginInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("token");
            editor.remove("rememberMe");
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}