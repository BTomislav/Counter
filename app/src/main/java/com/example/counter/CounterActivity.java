package com.example.counter;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class CounterActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private String responseBody;
    private Request request;
    private TextView count;
    private String token;
    private String lobbyId;
    private ArrayList<Model> listUsers;
    private ArrayList<LobbyModel> listLobbies;
    private CustomListAdapter customListAdapter;
    private CustomRecViewAdapter customRecViewAdapter;
    private RecyclerView recyclerLobbies;
    private int a;
    private boolean join;
    private LinearLayoutManager llm;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onStart(){
        super.onStart();
        UpdateCounter();

        //get lobbies
        UpdateLobbyList(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        //initialization
        listUsers = new ArrayList<>();
        listLobbies = new ArrayList<>();

        sharedPref = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        token = sharedPref.getString("token", "");
        String username = sharedPref.getString("username", "username");
        count = findViewById(R.id.textCount);
        listLobbies.add(new LobbyModel("Global lobby", 0));

        SetRecyclerViewAdapter();
        //declaring widgets
        TextView textUsername = findViewById(R.id.textUsername);
        TextView buttonLogout = findViewById(R.id.buttonLogout);
        ListView listScores = findViewById(R.id.listScores);

        View buttonRemoveCount = findViewById(R.id.buttonMinus);
        View buttonAddCount = findViewById(R.id.buttonPlus);
        View buttonAddLobby = findViewById(R.id.addLobby);

        //display username
        textUsername.setText(String.format(getResources().getString(R.string.welcome_message), username));

        //adapter for list view - users and scores
        customListAdapter = new CustomListAdapter(this, R.layout.custom_list_item, listUsers);
        listScores.setAdapter(customListAdapter);
        //listLobbies.add(new LobbyModel("Global lobby", 0));
        //get your score
       // UpdateCounter();

        //get lobbies
       // UpdateLobbyList(true);

        //listeners
        buttonAddLobby.setOnClickListener(v -> AddLobby());

        buttonAddCount.setOnClickListener(v -> AddCount());

        buttonRemoveCount.setOnClickListener(v -> RemoveCount());

        buttonLogout.setOnClickListener(v -> {
            sharedPref = getSharedPreferences("LoginInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("token");
            editor.remove("rememberMe");
            editor.remove("username");
            editor.apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void AddLobby() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window_join_or_create_lobby, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        PopupWindow popupAddLobby = new PopupWindow(popupView, width, height, true);

        popupAddLobby.showAtLocation(findViewById(R.id.textUsername), Gravity.CENTER, 0, 1);

        TextView joinOrCreate = popupView.findViewById(R.id.textJoinOrCreate);
        EditText inputCodeOrName = popupView.findViewById(R.id.inputCodeOrName);
        Button buttonConfirm = popupView.findViewById(R.id.buttonConfirm);

        String text = "Join lobby/";
        String text2 = text + "Create lobby";

        Typeface myTypeface = Typeface.create("", Typeface.BOLD);
        Typeface myTypefaceDef = Typeface.create("", Typeface.NORMAL);

        SpannableString spannable = new SpannableString(text2);

        DimBackground.Dim(popupAddLobby);

        //set join lobby bold
        spannable.setSpan(new TypefaceSpan(myTypeface), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //set create lobby default
        spannable.setSpan(new TypefaceSpan(myTypefaceDef), text.length(), text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#C1D5F1")), text.length(), text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        joinOrCreate.setText(spannable, TextView.BufferType.SPANNABLE);
        join = true;

        joinOrCreate.setOnClickListener(v -> {
            if (join){
                inputCodeOrName.setHint("Enter lobby name");
                //set join lobby default
                spannable.setSpan(new TypefaceSpan(myTypefaceDef), 0, text.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#C1D5F1")), 0, text.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                //set create lobby bold
                spannable.setSpan(new TypefaceSpan(myTypeface), text.length(), text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(Color.WHITE), text.length(), text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                joinOrCreate.setText(spannable, TextView.BufferType.SPANNABLE);
                join=false;
            }
            else{
                inputCodeOrName.setHint("Enter invite code");

                //set join lobby bold
                spannable.setSpan(new TypefaceSpan(myTypeface), 0, text.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                //set create lobby default
                spannable.setSpan(new TypefaceSpan(myTypefaceDef), text.length(), text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#C1D5F1")),  text.length(), text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                joinOrCreate.setText(spannable, TextView.BufferType.SPANNABLE);
                join = true;
            }
        });

        buttonConfirm.setOnClickListener(v -> {
            if (join){
                FormBody formBody = new FormBody.Builder()
                        .add("code", inputCodeOrName.getText().toString())
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.jurmanovic.com/clicker/v1/lobby/join")
                        .header("Authorization", "BEARER "+token)
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        showError(inputCodeOrName, "Couldn't connect to database");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        try {
                            responseBody = Objects.requireNonNull(response.body()).string();
                            JSONObject obj = new JSONObject(responseBody);
                            if (obj.has("errorCode")) {
                                showError(inputCodeOrName, obj.getString("message"));
                            }
                            else {
                                runOnUiThread(() -> {
                                    try {
                                        Toast.makeText(getBaseContext(), "Successfully joined lobby "+obj.getString("name"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                                UpdateLobbyList(false);
                                runOnUiThread(popupAddLobby::dismiss);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        response.close();
                    }
                });

            }
            else{
                if (inputCodeOrName.getText().toString().isEmpty()){
                    inputCodeOrName.setError("This field can't be empty");
                }
                else{
                    sharedPref = getSharedPreferences("LoginInfo", MODE_PRIVATE);
                    String token = sharedPref.getString("token", "");

                    FormBody formBody = new FormBody.Builder()
                            .add("name", inputCodeOrName.getText().toString())
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
                            showError(inputCodeOrName, "Couldn't connect to database");
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            try {
                                responseBody = Objects.requireNonNull(response.body()).string();
                                JSONObject obj = new JSONObject(responseBody);
                                if (obj.has("errorCode")) {
                                    showError(inputCodeOrName, obj.getString("message"));
                                }
                                else {
                                    runOnUiThread(() -> {
                                        try {
                                            Toast.makeText(getBaseContext(), "Successfully created lobby "+obj.getString("name"), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    UpdateLobbyList(false);
                                    runOnUiThread(popupAddLobby::dismiss);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            response.close();
                        }
                    });
                }
            }
        });
    }

    private void GetLobbyId(int position) {
        request = new Request.Builder()
                .url("https://api.jurmanovic.com/clicker/v1/lobby?page=1&itemsPerPage=10")
                .header("Authorization", "Bearer " + token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getBaseContext(), "Couldn't connect to database", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                responseBody = Objects.requireNonNull(response.body()).string();
                CounterActivity.this.runOnUiThread(()->{
                    try {
                        if (position==0){
                            lobbyId = "";
                        }
                        else{
                            JSONObject obj = new JSONObject(responseBody);
                            JSONArray arr = obj.getJSONArray("items");
                            lobbyId = arr.getJSONObject(position-1).getString("id");
                        }

                        UpdateLobbyUsers();
                        a = llm.findFirstCompletelyVisibleItemPosition();
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                });
                response.close();
            }
        });
    }

    private void RemoveCount() {
        request = new Request.Builder()
                .url("https://api.jurmanovic.com/clicker/v1/count")
                .header("Authorization", "Bearer " + token)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getBaseContext(), "Couldn't connect to database", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                UpdateLobbyUsers();
                UpdateCounter();
                response.close();
            }
        });
    }

    private void AddCount() {
        FormBody formBody = new FormBody.Builder()
                .build();

        request = new Request.Builder()
                .url("https://api.jurmanovic.com/clicker/v1/count")
                .header("Authorization", "Bearer " + token)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getBaseContext(), "Couldn't connect to database", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                UpdateLobbyUsers();
                UpdateCounter();
                response.close();
            }
        });
    }

    private void UpdateLobbyList(boolean firstTimeInit) {
        if (!firstTimeInit){
            listLobbies.clear();
            listLobbies.add(new LobbyModel("Global lobby", 0));
        }
        request = new Request.Builder()
                .url("https://api.jurmanovic.com/clicker/v1/lobby?page=1&itemsPerPage=50")
                .header("Authorization", "Bearer " + token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getBaseContext(), "Couldn't connect to database", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                responseBody = Objects.requireNonNull(response.body()).string();
                if (response.isSuccessful()){
                    CounterActivity.this.runOnUiThread(() -> {
                            try {
                            JSONObject obj = new JSONObject(responseBody);
                            JSONArray arr = obj.getJSONArray("items");
                            if (Integer.parseInt(obj.getString("count"))==0){
                                GetLobbyId(0);
                            }
                            for (int i = 0; i < Integer.parseInt(obj.getString("totalItems")); i++) {
                                JSONObject lobby = arr.getJSONObject(i);
                                listLobbies.add(new LobbyModel(lobby.getString("name"), lobby.getInt("code")));
                                if (i==(Integer.parseInt(obj.getString("totalItems"))-1)){
                                    customRecViewAdapter.notifyDataSetChanged();
                                    GetLobbyId(0);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
                response.close();
            }
        });
    }

    private void SetRecyclerViewAdapter() {

        recyclerLobbies = findViewById(R.id.recyclerLobbies);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerLobbies.setLayoutManager(layoutManager);
        customRecViewAdapter = new CustomRecViewAdapter(this, listLobbies);
        recyclerLobbies.setAdapter(customRecViewAdapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerLobbies);

        llm = (LinearLayoutManager) recyclerLobbies.getLayoutManager();
        assert llm != null;

        recyclerLobbies.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE && llm.findFirstCompletelyVisibleItemPosition()!=-1)
                {
                    if (a!=llm.findFirstCompletelyVisibleItemPosition()){
                        GetLobbyId(llm.findFirstCompletelyVisibleItemPosition());
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    private void UpdateCounter() {
        request = new Request.Builder()
                .url("https://api.jurmanovic.com/clicker/v1/count")
                .header("Authorization", "Bearer " + token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getBaseContext(), "Couldn't connect to database", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                responseBody = Objects.requireNonNull(response.body()).string();
                try {
                    JSONObject obj = new JSONObject(responseBody);
                    runOnUiThread(() -> {
                        try {
                            count.setText(obj.getString("count"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                response.close();
            }
        });
    }

    private void UpdateLobbyUsers() {
        request = new Request.Builder()
                .url("https://api.jurmanovic.com/clicker/v1//score?page=1&itemsPerPage=50&lobbyid="+lobbyId)
                .header("Authorization", "Bearer " + token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getBaseContext(), "Couldn't connect to database", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                listUsers.clear();
                responseBody = Objects.requireNonNull(response.body()).string();
                if (response.isSuccessful()){
                    CounterActivity.this.runOnUiThread(()->{
                        try {
                            JSONObject obj = new JSONObject(responseBody);
                            JSONArray arr = obj.getJSONArray("items");
                            for (int i = 0; i < Integer.parseInt(obj.getString("totalItems")); i++) {
                                JSONObject user = arr.getJSONObject(i);
                                listUsers.add(new Model(user.getJSONObject("user").getString("username"),user.getInt("count")));
                            }
                            customListAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
                response.close();
            }
        });
    }

    private void showError(EditText input, String s) {
        runOnUiThread(() -> input.setError(s));
    }
}