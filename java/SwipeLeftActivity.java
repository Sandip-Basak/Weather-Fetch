package com.example.weatherfetch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class SwipeLeftActivity extends AppCompatActivity {
    private float x1, x2, y1, y2;
    TextView cityName, regionName, mainTemperature, conditionText, feelsLike, highTemperature, lowTemperature, humidityText, windSpeedText, pressureText, precipitationText;
    ImageView conditionIcon, bgImage;
    String cn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_left);
        // To make application full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        cityName = findViewById(R.id.cityName);
        regionName = findViewById(R.id.regionName);
        mainTemperature = findViewById(R.id.mainTemperature);
        conditionText = findViewById(R.id.conditionText);
        feelsLike = findViewById(R.id.feelsLike);
        highTemperature = findViewById(R.id.highTemperature);
        lowTemperature = findViewById(R.id.lowTemperature);
        humidityText = findViewById(R.id.humidityText);
        windSpeedText = findViewById(R.id.windSpeedText);
        pressureText = findViewById(R.id.pressureText);
        precipitationText = findViewById(R.id.precipitationText);
        conditionIcon = findViewById(R.id.conditionIcon);
        bgImage = findViewById(R.id.bgImg);

        Intent intent = getIntent();
        if(intent.getIntExtra("isDay",0)==1){
            //Morning
            Picasso.get().load("https://images.unsplash.com/photo-1474140214938-f0295cf3d18e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80").into(bgImage);
        }
        else {
            //Night
            Picasso.get().load("https://images.unsplash.com/photo-1510057622795-5c8122c2c665?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80").into(bgImage);
        }
        cn = intent.getStringExtra("cityName");
        cityName.setText(cn);
        regionName.setText(intent.getStringExtra("regionName")+"  "+intent.getStringExtra("countryName"));
        mainTemperature.setText(intent.getStringExtra("mainTemperature")+"°C");
        conditionText.setText(intent.getStringExtra("conditionText"));
        feelsLike.setText("Feels Like "+intent.getStringExtra("feelsLike")+"°C");
        highTemperature.setText("Max : "+intent.getStringExtra("highTemperature")+"°C");
        lowTemperature.setText("Min : "+intent.getStringExtra("lowTemperature")+"°C");
        humidityText.setText("Humidity : "+intent.getStringExtra("humidityText")+"%");
        windSpeedText.setText("Wind Speed : "+intent.getStringExtra("windSpeedText")+" Km/hr");
        pressureText.setText("Pressure : "+intent.getStringExtra("pressureText")+" mBar");
        precipitationText.setText("Precipitation : "+intent.getStringExtra("precipitationText")+" mm");
        Picasso.get().load(intent.getStringExtra("conditionIcon")).into(conditionIcon);


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
                }else if(x1 > x2 && (x1-x2)>250){
                    Log.d("myTag", "X1 : "+x1+" X2 : "+x2+" Right Swipe");
                    Intent right = new Intent(SwipeLeftActivity.this, MainActivity.class);
                    right.putExtra("cityName", cn);
                    startActivity(right);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
                break;
        }
        return false;
    }
}