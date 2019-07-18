package com.integration.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Wongerfeng on 2019/7/8.
 */
public class Now implements Serializable {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
