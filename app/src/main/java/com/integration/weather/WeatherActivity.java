package com.integration.weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.integration.weather.gson.Forecast;
import com.integration.weather.gson.Suggestion;
import com.integration.weather.gson.Weather;
import com.integration.weather.service.AutoUpdateService;
import com.integration.weather.utils.HttpUtils;
import com.integration.weather.utils.LogUtils;
import com.integration.weather.utils.SharedPreferencesUtils;
import com.integration.weather.utils.Utility;

import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";

    private ScrollView mScrollView;
    public SwipeRefreshLayout mRefreshLayout;

    private TextView mTitleCity, mTitleUpdate, mDegree, mWeatherInfo, mAqi, mPm25, mSport, mCarWash, mComfort;
    private LinearLayout mForecastLayout;

    private Button mBtReturn;
    private ImageView mBgPic;
    public DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initView();
        initData();


    }


    private void initView() {
        mScrollView = findViewById(R.id.scrollView);
        mTitleCity = findViewById(R.id.tv_city_name);
        mDegree = findViewById(R.id.tv_title_degree);
        mTitleUpdate = findViewById(R.id.tv_update);
        mWeatherInfo = findViewById(R.id.tv_title_info);
        mAqi = findViewById(R.id.tv_aqi);
        mPm25 = findViewById(R.id.tv_pm25);
        mSport = findViewById(R.id.sport);
        mCarWash = findViewById(R.id.carWsh);
        mComfort = findViewById(R.id.comfort);

        mBtReturn = findViewById(R.id.btReturn);

        mBgPic = findViewById(R.id.mBgPic);
        mForecastLayout = findViewById(R.id.forecast_layout);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        mRefreshLayout = findViewById(R.id.swipeRefresh);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mBtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
//                Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
    }

    private void initData() {

        final String weatherString = (String) SharedPreferencesUtils.get(this, "weather", "weather", null);
        final String weatherId;

        if (weatherString != null) {

            Weather weather = (Weather) Utility.handleWeatherResponse(weatherString);
            weatherId = weather.getBasic().weatherId;
            if (weather != null) {
                showWeatherInfo(weather);
            }
        } else {
            weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
        }
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

    }

    public void requestWeather(String weatherId) {

        String url = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";

        HttpUtils.setOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String weatherInfo = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(weatherInfo);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.getStatus())) {
                            LogUtils.i(TAG, "Utility: " + weather);
                            SharedPreferencesUtils.commit(WeatherActivity.this, "weather", "weather", weather);

                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                        mRefreshLayout.setRefreshing(false);
                    }
                });

                LogUtils.i(TAG, "onResponse: " + weatherInfo);
            }
        });

        loadPics();


    }

    private void loadPics() {
        String picUrl = HttpUtils.WEB_PIC_URL;

        HttpUtils.setOkHttpRequest(picUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取背景图片失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String string = response.body().string();
                SharedPreferencesUtils.commit(WeatherActivity.this, "bing_pic", "bing_pic", string);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(string).into(mBgPic);
                    }
                });

            }
        });

    }

    private void showWeatherInfo(Weather weather) {

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];

        String temperature = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;


        Suggestion suggestion = weather.suggestion;
        if (suggestion != null) {
            mSport.setText("运动指数："+"\n"+suggestion.sport.info);
            mCarWash.setText("洗车指数："+"\n"+suggestion.carWash.info);
            mComfort.setText("舒适指数:"+"\n"+suggestion.comfort.info);
        }
        mTitleCity.setText(cityName);
        mTitleUpdate.setText(updateTime);
        mDegree.setText(temperature);
        mWeatherInfo.setText(weatherInfo);

        if (weather.getAqi() != null) {
            String aqi = weather.getAqi().city.aqi;
            String pm25 = weather.getAqi().city.pm25;

            mAqi.setText(aqi);
            mPm25.setText(pm25);
        }

        mForecastLayout.removeAllViews();

        List<Forecast> forecastList = weather.getForecastList();

        for (Forecast f : forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forest_item, mForecastLayout, false);
            TextView tvDate = view.findViewById(R.id.tv_date);
            TextView tvInfo = view.findViewById(R.id.tv_info);
            TextView tvMax = view.findViewById(R.id.tv_max);
            TextView tvMin = view.findViewById(R.id.tv_min);

            String date = f.date;
            String info = f.more.info;
            String max = f.temperature.max;
            String min = f.temperature.min;

            tvDate.setText(date);
            tvInfo.setText(info);
            tvMax.setText(max);
            tvMin.setText(min);

            mForecastLayout.addView(view);
        }

        mForecastLayout.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }


}
