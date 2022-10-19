package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherModal> WeatherModalArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherModal> weatherModalArrayList) {
        this.context = context;
        WeatherModalArrayList = weatherModalArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_recycler_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        WeatherModal modal = WeatherModalArrayList.get(position);
        holder.txtTemp.setText(modal.getTemperature()+"°C");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.imgCond);
        holder.txtWind.setText(modal.getWindSpeed()+"Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(modal.getTime());
            holder.txtTime.setText(output.format(t));
        }catch (ParseException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return WeatherModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTime , txtTemp , txtWind;
        private ImageView imgCond;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTemp = itemView.findViewById(R.id.txtTemperature);
            txtWind = itemView.findViewById(R.id.txtWindSpeed);
            imgCond = itemView.findViewById(R.id.imgCondition);
        }
    }
}
