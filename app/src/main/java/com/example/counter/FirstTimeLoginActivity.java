package com.example.counter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Context;
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

import java.io.IOException;

public class FirstTimeLoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private Intent intent;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_login);

        Button buttonCreateLobby = findViewById(R.id.buttonCreateLobby);
        Button buttonJoinLobby = findViewById(R.id.buttonJoinLobby);
        Button buttonContinueWithoutLobby = findViewById(R.id.buttonContinueWithoutLobby);

        buttonCreateLobby.setOnClickListener(v -> CreateLobby());
        buttonJoinLobby.setOnClickListener(v -> JoinLobby());
        buttonContinueWithoutLobby.setOnClickListener(v -> ContinueWithoutLobby());

        /*button.setOnClickListener(v -> {
            sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("token");
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });*/
    }

    private void ContinueWithoutLobby() {
        intent = new Intent(getBaseContext(), CounterActivity.class);
        startActivity(intent);
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

        EditText inputCode = findViewById(R.id.inputCode);
        Button confirm = findViewById(R.id.buttonConfirmJoin);

        confirm.setOnClickListener(v -> {
            FormBody formBody = new FormBody.Builder()
                    .add("name", "")
                    .build();
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

        EditText inputLobbyName = findViewById(R.id.inputLobbyName);
        Button buttonConfirm = findViewById(R.id.buttonConfirmCreate);

        buttonConfirm.setOnClickListener(v -> {
            if (inputLobbyName.getText().toString().isEmpty()){
                inputLobbyName.setError("This field can't be empty");
            }
            else{
                Request request = new Request.Builder()
                        .url("https://scoreboard-counter.azurewebsites.net/v1/auth/check-token")
                        .addHeader("name", inputLobbyName.getText().toString())
                        .get()
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    }
                });


            }


        });
    }
}