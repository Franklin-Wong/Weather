package com.integration.weather.utils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Wongerfeng on 2019/7/5.
 */
public class HttpUtils {

    public static final String WEB_URL = "http://guolin.tech/api/china/";

    public static final String WEB_PIC_URL = "http://guolin.tech/api/bing_pic";

    public static void setOkHttpRequest(String url, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);

    }

}
