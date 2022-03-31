package com.example.counter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;

public class CustomListAdapter extends ArrayAdapter<Model>{
    private final Context mCtx;
    private final int res;

    public CustomListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Model> arr) {
        super(context, resource, arr);
        this.mCtx = context;
        this.res = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater lf = LayoutInflater.from(mCtx);
        convertView = lf.inflate(res, parent, false);
        TextView usernameView = convertView.findViewById(R.id.username);
        TextView scoreView = convertView.findViewById(R.id.score);
        TextView place = convertView.findViewById(R.id.placeNumber);


        place.setText(String.valueOf(position+1));
        usernameView.setText(getItem(position).getUsername());
        scoreView.setText(String.valueOf(getItem(position).getScore()));


        SharedPreferences sharedPref = mCtx.getSharedPreferences("LoginInfo", MODE_PRIVATE);
        if (getItem(position).getUsername().equals(sharedPref.getString("username", "username"))){
            usernameView.setTypeface(null, Typeface.BOLD);
            place.setTypeface(null, Typeface.BOLD);
            scoreView.setTypeface(null, Typeface.BOLD);
            usernameView.setTextColor(Color.parseColor("#DCE7F6"));
            place.setTextColor(Color.parseColor("#DCE7F6"));
            scoreView.setTextColor(Color.parseColor("#DCE7F6"));
        }

        return convertView;
    }
}



