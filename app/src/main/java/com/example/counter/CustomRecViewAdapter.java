package com.example.counter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomRecViewAdapter extends RecyclerView.Adapter<CustomRecViewAdapter.ViewHolder> {
    private final ArrayList<LobbyModel> arr;
    Context mCtx;

    public CustomRecViewAdapter(Context context, ArrayList<LobbyModel> arrayList){
        this.mCtx = context;
        this.arr = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.custom_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.lobbyName.setText(arr.get(position).getLobbyName());
        if (position==0){
            holder.inviteCode.setText("");
        }
        else{
            holder.inviteCode.setText("Invite code: "+arr.get(position).getInviteCode());
        }

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView lobbyName;
        TextView inviteCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lobbyName = itemView.findViewById(R.id.lobbyName);
            inviteCode = itemView.findViewById(R.id.inviteCode);


        }
    }
}
