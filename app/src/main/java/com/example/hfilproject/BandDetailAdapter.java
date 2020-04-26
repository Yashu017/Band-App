package com.example.hfilproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BandDetailAdapter extends RecyclerView.Adapter<BandDetailAdapter.ViewHolder> {

    List<BandDetail> data;
    Context context;
    private LayoutInflater layoutInflater;

    BandDetailAdapter(Context context, List<BandDetail> data) {
        this.data = data;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.band_details, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.description.setText(data.get(position).getDescription());
        holder.image.setImageResource(data.get(position).getImageResource());
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.Description);
            image = (ImageView) itemView.findViewById(R.id.ProductImg);
        }
    }
}
