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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class FirstTimeLoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private Intent intent;
    private String responseBody;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_login);

        Button buttonCreateLobby = findViewById(R.id.buttonCreateLobby);
        Button buttonJoinLobby = findViewById(R.id.buttonJoinLobby);

        buttonCreateLobby.setOnClickListener(v -> CreateLobby());
        buttonJoinLobby.setOnClickListener(v -> JoinLobby());

    }

    private void JoinLobby() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.join_lobby_form, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow popupJoinLobby = new PopupWindow(popupView, width, height, true);
        popupJoinLobby.showAtLocation(findViewById(R.id.buttonJoinLobby), Gravity.CENTER, 0, 1);
        DimBackground.Dim(popupJoinLobby);

        EditText inputCode = popupView.findViewById(R.id.inputCode);
        Button confirm = popupView.findViewById(R.id.buttonConfirmJoin);

        sharedPref = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String token = sharedPref.getString("token", "");

        confirm.setOnClickListener(v -> {
            FormBody formBody = new FormBody.Builder()
                    .add("code", inputCode.getText().toString())
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.jurmanovic.com/clicker/v1/lobby/join")
                    .header("Authorization", "BEARER "+token)
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    showError(inputCode, "Couldn't connect to database");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        responseBody = Objects.requireNonNull(response.body()).string();
                        JSONObject obj = new JSONObject(responseBody);
                        if (obj.has("errorCode")) {
                            showError(inputCode, obj.getString("message"));
                        }
                        else {
                            System.out.println(responseBody);
                            intent = new Intent(getBaseContext(), CounterActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    private void CreateLobby() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.create_lobby_form, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow popupCreateLobby = new PopupWindow(popupView, width, height, true);
        popupCreateLobby.showAtLocation(findViewById(R.id.buttonJoinLobby), Gravity.CENTER, 0, 1);
        DimBackground.Dim(popupCreateLobby);

        EditText inputLobbyName = popupView.findViewById(R.id.inputLobbyName);
        Button buttonConfirm = popupView.findViewById(R.id.buttonConfirmCreate);

        buttonConfirm.setOnClickListener(v -> {
            if (inputLobbyName.getText().toString().isEmpty()){
                inputLobbyName.setError("This field can't be empty");
            }
            else{
                sharedPref = getSharedPreferences("LoginInfo", MODE_PRIVATE);
                String token = sharedPref.getString("token", "");

                FormBody formBody = new FormBody.Builder()
                        .add("name", inputLobbyName.getText().toString())
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.jurmanovic.com/clicker/v1/lobby")
                        .header("Authorization", "BEARER "+token)
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                            showError(inputLobbyName, "Couldn't connect to database");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {
                            responseBody = Objects.requireNonNull(response.body()).string();
                            JSONObject obj = new JSONObject(responseBody);
                            if (obj.has("errorCode")) {
                                showError(inputLobbyName, obj.getString("message"));
                            }
                            else {
                                System.out.println(responseBody);
                                runOnUiThread(() -> {
                                    try {
                                        Toast.makeText(getBaseContext(), "Successfully joined lobby "+obj.getString("name"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }) ;
                                intent = new Intent(getBaseContext(), CounterActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    private void showError(EditText input, String s) {
        runOnUiThread(() -> input.setError(s));
    }
}


