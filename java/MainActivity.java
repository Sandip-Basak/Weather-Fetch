package com.example.weatherfetch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV, location;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;
    private float x1,x2,y1,y2;

     int leftIsDay;
     String leftCondition,leftConditionIcon, leftTemperature, leftHumidity, leftWind, leftFeelsLike, leftPressure, leftPrecipitation,leftCity, leftRegion, leftCountry, leftHighTemp, leftLowTemp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // To make application full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
        location = findViewById(R.id.location);
        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRvWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBlack);
        iconIV = findViewById(R.id.idTVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModelArrayList);
        weatherRV.setAdapter(weatherRVAdapter);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            cityName = getCityName(location.getLatitude(), location.getLongitude());
        }
        Intent intent = getIntent();
        cityName = intent.getStringExtra("cityName");
        if(cityName == null){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                cityName = getCityName(location.getLatitude(), location.getLongitude());
            }
            else {
                cityName = "Delhi";
            }
        }
        getWeatherInfo(cityName);


        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                }
                else {
                    getWeatherInfo(city);
                }
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plsLocation();
            }
        });

    }
    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 < x2 && (x2-x1)>250){
                    Log.d("myTag", "X1 : "+x1+" X2 : "+x2+" Left Swipe");
                    Intent left = new Intent(MainActivity.this, SwipeLeftActivity.class);
                    left.putExtra("isDay",leftIsDay);
                    left.putExtra("cityName", leftCity);
                    left.putExtra("regionName", leftRegion);
                    left.putExtra("countryName", leftCountry);
                    left.putExtra("mainTemperature", leftTemperature);
                    left.putExtra("conditionText", leftCondition);
                    left.putExtra("feelsLike", leftFeelsLike);
                    left.putExtra("highTemperature", leftHighTemp);
                    left.putExtra("lowTemperature", leftLowTemp);
                    left.putExtra("humidityText", leftHumidity);
                    left.putExtra("windSpeedText", leftWind);
                    left.putExtra("pressureText", leftPressure);
                    left.putExtra("precipitationText", leftPrecipitation);
                    left.putExtra("conditionIcon", leftConditionIcon);
                    startActivity(left);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
            }else if(x1 > x2 && (x1-x2)>250){
                    Log.d("myTag", "X1 : "+x1+" X2 : "+x2+" Right Swipe");
                    Intent right = new Intent(MainActivity.this, SwipeRightActivity.class);
                    right.putExtra("RightCity", leftCity);
                    startActivity(right);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
            }
            break;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("myTag","Permission Granted");
                checkLocation();
            }
            else {
                Log.d("myTag", "Permission Not Given");
            }
        }
    }

    private void getWeatherInfo(String name) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=4f35a0596d8c451b90b135624222806 &q="+name+"&days=1&aqi=yes&alerts=yes";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                cityNameTV.setText(name);
                weatherRVModelArrayList.clear();
                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature+"°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);
                    leftTemperature = temperature;
                    leftIsDay = isDay;
                    leftCondition = condition;
                    leftConditionIcon = "https:".concat(conditionIcon);
                    leftFeelsLike = response.getJSONObject("current").getString("feelslike_c");
                    leftHumidity = response.getJSONObject("current").getString("humidity");
                    leftPrecipitation = response.getJSONObject("current").getString("precip_mm");
                    leftWind = response.getJSONObject("current").getString("wind_kph");
                    leftCity = response.getJSONObject("location").getString("name");
                    leftRegion = response.getJSONObject("location").getString("region");
                    leftCountry = response.getJSONObject("location").getString("country");
                    leftPressure = response.getJSONObject("current").getString("pressure_mb");

                    if(isDay==1){
                        //Morning
                        Picasso.get().load("https://images.unsplash.com/photo-1474140214938-f0295cf3d18e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80").into(backIV);
                    }
                    else {
                        //Night
                        Picasso.get().load("https://images.unsplash.com/photo-1510057622795-5c8122c2c665?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80").into(backIV);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO =  forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONObject cast = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    leftHighTemp = cast.getJSONObject("day").getString("maxtemp_c");
                    leftLowTemp = cast.getJSONObject("day").getString("mintemp_c");
                    JSONArray hourArray = forecastO.getJSONArray("hour");
                    for(int i=0;i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temp = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModelArrayList.add(new WeatherRVModel(time,temp,img,wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    private String getCityName(double latitude, double longitude){
        String justCityName="Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);

            for (Address adr : addresses){
                if(adr!=null){
                    String city =  adr.getLocality();
                    if(city!=null && !city.equals("")){
                        justCityName=city;
                    }
                    else {
                        Log.d("myTag","CITY NOT FOUND");
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return justCityName;
    }
    private void checkLocation(){
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLatitude(), location.getLongitude());
        getWeatherInfo(cityName);
    }
    private void plsLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        else{
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            cityName = getCityName(location.getLatitude(), location.getLongitude());
            getWeatherInfo(cityName);
        }
    }
}