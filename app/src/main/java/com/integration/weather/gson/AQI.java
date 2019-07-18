package com.integration.weather.gson;

import java.io.Serializable;

/**
 * Created by Wongerfeng on 2019/7/8.
 */
public class AQI implements Serializable {

    public AQICity city;
    public class AQICity{

        public String aqi;

        public String pm25;

    }

}
