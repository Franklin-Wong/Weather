package com.integration.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Wongerfeng on 2019/7/8.
 */
public class Basic implements Serializable {

    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;


    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
