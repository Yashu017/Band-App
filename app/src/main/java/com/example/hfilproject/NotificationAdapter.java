package com.example.hfilproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    List<Notification> data;
    Context context;
    private LayoutInflater inflater;

    NotificationAdapter(Context context, List<Notification> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = inflater.from(context).inflate(R.layout.notification_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        holder.message.setText(data.get(position).getMessage());
        holder.time.setText(""+data.get(position).getTime());
        holder.time1.setText(""+data.get(position).getTime1());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private TextView time,time1;

        ViewHolder(View itemView) {
            super(itemView);


            message = (TextView) itemView.findViewById(R.id.textView2);
            time = (TextView) itemView.findViewById(R.id.getTime);
            time1=itemView.findViewById(R.id.textView4);
        }
    }
}
