package com.example.weatherfetch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SwipeRightActivity extends AppCompatActivity {
    private float x1,x2,y1,y2;
    private ImageView RightBgImg, RightConditionIcon;
    private TextView Date, RightCityName, RightRegionName, RightCondition, RightHighTemperature, RightLowTemperature, RightHumidity;
    private RelativeLayout RightRl;
    private RecyclerView RightWeather;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private ProgressBar progressBar;
    String cityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_right);
        // To make application full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        RightBgImg = findViewById(R.id.RightBgImg);
        RightConditionIcon = findViewById(R.id.RightConditionIcon);
        Date = findViewById(R.id.Date);
        RightCityName = findViewById(R.id.RightCityName);
        RightRegionName = findViewById(R.id.RightRegionName);
        RightCondition = findViewById(R.id.RightCondition);
        RightHighTemperature = findViewById(R.id.RightHighTemperature);
        RightLowTemperature = findViewById(R.id.RightLowTemperature);
        RightHumidity = findViewById(R.id.RightHumidity);
        progressBar = findViewById(R.id.RightLoading);
        RightRl = findViewById(R.id.RightRl);
        RightWeather = findViewById(R.id.RightWeather);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModelArrayList);
        RightWeather.setAdapter(weatherRVAdapter);
        Intent intent = getIntent();
        cityName = intent.getStringExtra("RightCity");
        getWeatherInfo(cityName);


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
                    Intent left = new Intent(SwipeRightActivity.this, MainActivity.class);
                    left.putExtra("cityName", cityName);
                    startActivity(left);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }else if(x1 > x2 && (x1-x2)>250){
                    Log.d("myTag", "X1 : "+x1+" X2 : "+x2+" Right Swipe");
                }
                break;
        }
        return false;
    }
    private void getWeatherInfo(String city){
        String url = "https://api.weatherapi.com/v1/forecast.json?key=4f35a0596d8c451b90b135624222806 &q="+city+"&days=2&aqi=yes&alerts=yes";
        RequestQueue requestQueue = Volley.newRequestQueue(SwipeRightActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                RightRl.setVisibility(View.VISIBLE);
                RightCityName.setText(city);
                weatherRVModelArrayList.clear();
                try {
                    RightRegionName.setText(response.getJSONObject("location").getString("region")+"  "+response.getJSONObject("location").getString("country"));

                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONObject cast = forecast.getJSONArray("forecastday").getJSONObject(1);
                    RightCondition.setText(cast.getJSONObject("day").getJSONObject("condition").getString("text"));
                    Picasso.get().load("https:".concat(cast.getJSONObject("day").getJSONObject("condition").getString("icon"))).into(RightConditionIcon);
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    if(isDay==1){
                        //Morning
                        Picasso.get().load("https://images.unsplash.com/photo-1474140214938-f0295cf3d18e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80").into(RightBgImg);
                    }
                    else {
                        //Night
                        Picasso.get().load("https://images.unsplash.com/photo-1510057622795-5c8122c2c665?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80").into(RightBgImg);
                    }
                    RightHighTemperature.setText("Max : "+cast.getJSONObject("day").getString("maxtemp_c")+"°C");
                    RightLowTemperature.setText("Min : "+cast.getJSONObject("day").getString("mintemp_c")+"°C");
                    RightHumidity.setText(cast.getJSONObject("day").getString("daily_chance_of_rain")+"% chance of precipitation");

                    JSONArray hourArray = cast.getJSONArray("hour");
                    for(int i=0;i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temp = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModelArrayList.add(new WeatherRVModel(time,temp,img,wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SwipeRightActivity.this, "Something went wrong !!!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}