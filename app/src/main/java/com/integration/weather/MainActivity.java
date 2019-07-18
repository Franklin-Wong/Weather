package com.integration.weather;

import android.content.Intent;
import android.os.Bundle;

import com.integration.weather.utils.SharedPreferencesUtils;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
//        implements ChooseAreaFragment.OnListFragmentInteractionListener
{
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String weatherString = (String) SharedPreferencesUtils.get(this, "weather", "weather", null);

        if (weatherString != null){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
        }


    }

//    @Override
//    public void onListFragmentInteraction(DummyContent.DummyItem item) {
//        Log.i(TAG, "onListFragmentInteraction:");
//
//    }
}
