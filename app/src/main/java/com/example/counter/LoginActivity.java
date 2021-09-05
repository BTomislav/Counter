package com.example.counter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText loginUsernameInput, loginPasswordInput, registerUsernameInput, registerPasswordInput, registerPasswordCheck;
    private final OkHttpClient client = new OkHttpClient();
    private Button buttonSignIn, buttonRegister;
    private String responseBody, token;
    private JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsernameInput = findViewById(R.id.loginUsernameInput);
        loginPasswordInput = findViewById(R.id.loginPasswordInput);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        TextView textRegisterNow = findViewById(R.id.textClickToRegister);

        buttonSignIn.setOnClickListener(v -> checkCredentials());
        textRegisterNow.setOnClickListener(v -> RegisterForm());
    }

    private void RegisterForm() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.register_form, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(findViewById(R.id.loginUsernameInput), Gravity.CENTER, 0, 1);
        DimBackground.Dim(popupWindow);

        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(v -> {
            FormBody formBody = new FormBody.Builder()
                    .add("username", registerUsernameInput.getText().toString())
                    .add("email", registerUsernameInput.getText().toString())
                    .add("password", registerPasswordInput.getText().toString())
                    .build();

            Request request = new Request.Builder()
                    .url("https://wallet-go-webapi.herokuapp.com/v1/auth/register")
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity. "Couldn't connect to database", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        responseBody= Objects.requireNonNull(response.body()).string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!responseBody.contains("token")){
                        showError(loginUsernameInput, "Incorrect username or password");
                    }
                    else{
                        try {
                            obj = new JSONObject(responseBody);
                            token = obj.getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(token);
                    }
                }
            });
        });

    }

    private void checkCredentials() {

        String loginUsername = loginUsernameInput.getText().toString();
        String loginPassword = loginPasswordInput.getText().toString();

        FormBody formBody = new FormBody.Builder()
                .add("email", loginUsername)
                .add("password", loginPassword)
                .build();

        Request request = new Request.Builder()
                .url("https://wallet-go-webapi.herokuapp.com/v1/auth/login")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
               e.printStackTrace();

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    responseBody= Objects.requireNonNull(response.body()).string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!responseBody.contains("token")){
                    showError(loginUsernameInput, "Incorrect username or password");
                }
                else{
                    try {
                        obj = new JSONObject(responseBody);
                        token = obj.getString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                   System.out.println(token);
                }
            }
        });
    }

    private void showError(EditText input, String s) {
        runOnUiThread(() -> input.setError(s));
    }
}