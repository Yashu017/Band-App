package com.example.hfilproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TempAdapter extends RecyclerView.Adapter<TempAdapter.ViewHolder> {
    List<TempLog> data;
    Context context;
    private LayoutInflater layoutInflater;

    TempAdapter(Context context, List<TempLog> data) {
        this.data = data;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.temp_logs, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TempAdapter.ViewHolder holder, int position) {
        holder.tempCelsius.setText(""+data.get(position).getCelsius());
        holder.tempFaren.setText(""+data.get(position).getFarenheit());
        holder.UserStatus.setText(data.get(position).getStatus());
        holder.LogDay.setText(data.get(position).getDay());
        holder.LogTime.setText(data.get(position).getTime());


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tempCelsius, tempFaren, UserStatus, LogDay, LogTime;

        ViewHolder(View itemView) {
            super(itemView);
            tempCelsius = (TextView) itemView.findViewById(R.id.tempC);
            tempFaren = (TextView) itemView.findViewById(R.id.tempF);
            UserStatus = (TextView) itemView.findViewById(R.id.status);
            LogDay = (TextView) itemView.findViewById(R.id.day);
            LogTime = (TextView) itemView.findViewById(R.id.time);
        }
    }
}
