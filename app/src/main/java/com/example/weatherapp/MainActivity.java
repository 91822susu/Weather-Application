package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRel;
    private ProgressBar loadingBar;
    private TextView cityName , tempImg , windyOne;
    private RecyclerView weatherTv;
    private TextInputEditText cityEdit;
    private ImageView backGround , searchIcon , imageCloud;
    private ArrayList<WeatherModal> weatherModalArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityNameVar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        homeRel = findViewById(R.id.idHome);
        loadingBar = findViewById(R.id.idProgress);
        cityName = findViewById(R.id.idCityName);
        tempImg = findViewById(R.id.idTempimg);
        windyOne = findViewById(R.id.idWindy);
        weatherTv = findViewById(R.id.idRecycler);
        cityEdit = findViewById(R.id.idEditText);
        backGround = findViewById(R.id.idBackGround);
        searchIcon = findViewById(R.id.idSearchIcon);
        imageCloud = findViewById(R.id.idImageCloud);
        weatherModalArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this,weatherModalArrayList);
        weatherTv.setAdapter(weatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);

        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityNameVar = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(cityNameVar);



        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdit.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please Enter City Name",Toast.LENGTH_SHORT).show();
                }else{
                    cityName.setText(cityNameVar);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permissions Granted..",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Please Provide The Permissions",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude , double latitude){
       String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);

            for(Address adr : addresses){
                if(adr != null){
                    String city = adr.getLocality();
                    if(city != null && !city.equals("")){
                        cityName = city;
                    }else {
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this,"City Not Found..",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityNameCurrent){
        String url = "http://api.weatherapi.com/v1/forecast.json?key=6dd69dcaca8d4d469d2142021221810&q="+ cityNameCurrent +"&days=1&aqi=yes&alerts=yes";
        cityName.setText(cityNameCurrent);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingBar.setVisibility(View.GONE);
                homeRel.setVisibility(View.VISIBLE);
                weatherModalArrayList.clear();

                try {
                    String temp = response.getJSONObject("current").getString("temp_c");
                    tempImg.setText(temp + "°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(imageCloud);
                    windyOne.setText(condition);
                    if(isDay == 1){
                        //Morning Image
                        Picasso.get().load("https://images.unsplash.com/photo-1566228015668-4c45dbc4e2f5?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80").into(backGround);
                    }else{
                        //Night Image
                        Picasso.get().load("https://images.unsplash.com/photo-1532074534361-bb09a38cf917?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80").into(backGround);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    for(int i=0;i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String airSpeed = hourObj.getString("wind_kph");

                        weatherModalArrayList.add(new WeatherModal(time,temper,img,airSpeed));
                    }
                    weatherAdapter.notifyDataSetChanged();

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Please Enter Valid City Name..",Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}