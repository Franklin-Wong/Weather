package com.integration.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Wongerfeng on 2019/7/8.
 */
public class Forecast implements Serializable {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;

    public class  More{
        @SerializedName("txt_d")
        public String info;
    }

    public class Temperature {
        public String max;
        public String min;
    }
}
