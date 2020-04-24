package com.example.hfilproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.hfilproject.Model.ModelBegin;
import com.example.hfilproject.R;

import java.util.List;

public class AdapterBegin extends PagerAdapter {

    private List<ModelBegin> modelBegins;
    private LayoutInflater layoutInflater;
    private Context context;

    public AdapterBegin(List<ModelBegin> modelBegins,  Context context) {
        this.modelBegins = modelBegins;
        this.context = context;
    }

    @Override
    public int getCount() {
        return modelBegins.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.item,container,false);


        ImageView imageView;
        TextView title,desc;
        imageView=view.findViewById(R.id.img1);
        title=view.findViewById(R.id.title);
        desc=view.findViewById(R.id.desc);


        imageView.setImageResource(modelBegins.get(position).getImgStart());
        title.setText(modelBegins.get(position).getTitle());
        desc.setText(modelBegins.get(position).getDescr());
        container.addView(view,0);

        return view;


    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
