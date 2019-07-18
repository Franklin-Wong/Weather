package com.integration.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import com.integration.weather.gson.Weather;
import com.integration.weather.utils.HttpUtils;
import com.integration.weather.utils.LogUtils;
import com.integration.weather.utils.SharedPreferencesUtils;
import com.integration.weather.utils.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    private static final String TAG = "AutoUpdateService";
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        loadPics();
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int anHour = 8 * 60 * 60 *1000;

        long triggerTme = SystemClock.elapsedRealtime() + anHour;

        PendingIntent pi = PendingIntent.getService(this, 0, new Intent(this, AutoUpdateService.class), 0);
        if (manager != null) {
            manager.cancel(pi);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTme, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void loadPics() {

        String picUrl = HttpUtils.WEB_PIC_URL;

        HttpUtils.setOkHttpRequest(picUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String picString = response.body().string();
                SharedPreferencesUtils.commit(AutoUpdateService.this, "bing_pic", "bing_pic", picString);

                LogUtils.i(TAG, "loadPics: ");
            }
        });


    }

    private void updateWeather() {
        String  weatherString = (String) SharedPreferencesUtils.get(this, "weather", "weather", null);

        if (!TextUtils.isEmpty(weatherString)){

            Weather weather = Utility.handleWeatherResponse(weatherString);

            String weatherId = null;
            if (weather != null) {
                weatherId = weather.basic.weatherId;
            }

            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";

            HttpUtils.setOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String string = response.body().string();
                    Weather updateWeather = Utility.handleWeatherResponse(string);

                    SharedPreferencesUtils.commit(AutoUpdateService.this, "weather", "weather", updateWeather);

                    LogUtils.i(TAG, "updateWeather: ");
                }
            });
        }


    }
}
