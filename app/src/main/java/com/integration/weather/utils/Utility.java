package com.integration.weather.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.integration.weather.db.City;
import com.integration.weather.db.County;
import com.integration.weather.db.Province;
import com.integration.weather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Wongerfeng on 2019/7/5.
 */
public class Utility {
    /**
     * 解析省份数据
     * @param response 响应
     */
    public static boolean handleProvinceResponse(String response){

        if (!TextUtils.isEmpty(response)){
            try {
                //创建response的json数组
                JSONArray array = new JSONArray(response);
                //遍历数组元素。获取json对象
                for(int i = 0; i < array.length(); i++){
                    JSONObject jsonObject = array.getJSONObject(i);
                    //初始化实体类对象
                    Province province = new Province();
                    //将json对象的属性赋予实体类对象
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    //保存
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     *
     * @param string
     * @return
     */
    public static boolean handleCityResponse(String string, int provinceId){
        if (!TextUtils.isEmpty(string)){
            try {
                JSONArray array = new JSONArray(string);

                for (int i = 0;i < array.length(); i++){
                    JSONObject cityObject =  array.getJSONObject(i);

                    City city = new City();
                    city.setCityId(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);

                    city.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
    public static boolean handleCountyResponse(String string, int cityId){
        if (!TextUtils.isEmpty(string)){
            try {
                JSONArray array = new JSONArray(string);

                for (int i = 0;i < array.length(); i++){
                    JSONObject countyObject =  array.getJSONObject(i);

                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setId(countyObject.getInt("id"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);

                    county.save();
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static Weather handleWeatherResponse(String string){
        if (!TextUtils.isEmpty(string)){
            try {

                JSONObject object = new JSONObject(string);

                JSONArray jsonArray = object.getJSONArray("HeWeather");
                String content = jsonArray.getJSONObject(0).toString();

                return new Gson().fromJson(content, Weather.class);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

}
