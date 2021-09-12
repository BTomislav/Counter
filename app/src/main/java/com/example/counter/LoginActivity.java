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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    private EditText loginUsernameInput, loginPasswordInput, registerUsernameInput, registerPasswordInput, registerPasswordCheck;
    private CheckBox checkRememberMe;
    private final OkHttpClient client = new OkHttpClient();
    private String responseBody, token;
    private JSONObject obj;
    public SharedPreferences sharedPref;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsernameInput = findViewById(R.id.loginUsernameInput);
        loginPasswordInput = findViewById(R.id.loginPasswordInput);
        checkRememberMe = findViewById(R.id.checkRememberMe);
        Button buttonSignIn = findViewById(R.id.buttonSignIn);
        TextView textRegisterNow = findViewById(R.id.textClickToRegister);

        buttonSignIn.setOnClickListener(v -> Login(loginUsernameInput.getText().toString(), loginPasswordInput.getText().toString(), checkRememberMe.isChecked(), false));
        textRegisterNow.setOnClickListener(v -> RegisterForm());
    }

    private void RegisterForm() {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.register_form, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow popupRegister = new PopupWindow(popupView, width, height, true);
        popupRegister.showAtLocation(findViewById(R.id.loginUsernameInput), Gravity.CENTER, 0, 1);
        DimBackground.Dim(popupRegister);

        Button buttonRegister = popupView.findViewById(R.id.buttonRegister);
        registerUsernameInput = popupView.findViewById(R.id.registerInputUsername);
        registerPasswordInput = popupView.findViewById(R.id.registerInputPassword);
        registerPasswordCheck = popupView.findViewById(R.id.registerInputConfirmPassword);

        buttonRegister.setOnClickListener(v -> {
            if(checkInput()){
                FormBody formBody = new FormBody.Builder()
                        .add("username", registerUsernameInput.getText().toString())
                        .add("password", registerPasswordInput.getText().toString())
                        .build();

                Request request = new Request.Builder()
                        .url("https://scoreboard-counter.azurewebsites.net/v1/auth/register")
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
                            responseBody = Objects.requireNonNull(response.body()).string();
                            JSONObject obj = new JSONObject(responseBody);
                            if (obj.has("id")){
                                String username=registerUsernameInput.getText().toString();
                                String password=registerPasswordInput.getText().toString();
                                runOnUiThread(popupRegister::dismiss);
                                Login(username, password, false, true);
                            }
                            else {
                                showError(registerUsernameInput, obj.getString("message"));
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            showError(registerUsernameInput, responseBody);
                        }
                    }
                });
            }
        });

    }

    private void Login(String loginUsername, String loginPassword, boolean check, boolean firstTime) {

        FormBody formBody = new FormBody.Builder()
                .add("username", loginUsername)
                .add("password", loginPassword)
                .add("rememberMe", String.valueOf(check))
                .build();

        Request request = new Request.Builder()
                .url("https://scoreboard-counter.azurewebsites.net/v1/auth/login")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
               e.printStackTrace();
                Toast.makeText(getBaseContext(), "Couldn't connect to database", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    responseBody= Objects.requireNonNull(response.body()).string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!responseBody.contains("accessToken")){
                    showError(loginUsernameInput, "Incorrect username or password");
                }

                else{
                    try {
                        obj = new JSONObject(responseBody);
                        token = obj.getString("accessToken");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Remember me?
                    if (checkRememberMe.isChecked()) SaveToken();
                    //First time login?
                    if (firstTime){
                        intent = new Intent(getBaseContext(), FirstTimeLoginActivity.class);
                    }
                    else
                    {
                        intent = new Intent(getBaseContext(), CounterActivity.class);
                    }
                    //intent = (check) ? new Intent(getBaseContext(), CounterActivity.class) : new Intent(getBaseContext(), FirstTimeLoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean checkInput() {
        if (registerUsernameInput.getText().toString().isEmpty()){
            showError(registerUsernameInput, "This field can't be empty");
            return false;
        }
        else if(registerPasswordInput.getText().toString().isEmpty()){
            showError(registerPasswordInput, "This field can't be empty");
            return false;
        }
        else if (!registerPasswordInput.getText().toString().equals(registerPasswordCheck.getText().toString())){
            showError(registerPasswordCheck, "Password doesn't match");
            return false;
        }
        else{
            return true;
        }
    }

    private void SaveToken() {
        sharedPref = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.apply();
        System.out.println(token);
    }

    private void showError(EditText input, String s) {
        runOnUiThread(() -> input.setError(s));
    }
}