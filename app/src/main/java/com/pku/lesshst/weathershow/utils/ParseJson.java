package com.pku.lesshst.weathershow.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.pku.lesshst.weathershow.utils.WeatherInfo.*;
import com.pku.lesshst.weathershow.utils.WeatherInfo;

/**
 * Created by lzb on 2015/9/24.
 */
public class ParseJson {

    public ParseJson(String jsonStr) throws JSONException{
        try{
            getWeather(jsonStr);
        }
        catch (JSONException e) {
            return;
        }
    }

    public WeatherInfo weatherInfo;
    private void getWeather(String jsonWeather) throws JSONException{
        weatherInfo = new WeatherInfo();

        JSONObject json = new JSONObject(jsonWeather);
        JSONArray jsonArray = json.getJSONArray("HeWeather data service 3.0");
        JSONObject firstNode = (JSONObject) jsonArray.get(0);

        //"now"
        JSONObject jaNow = (JSONObject) firstNode.get("now");

        NowWeather nowWeather = new NowWeather();

        nowWeather.txt = (String) ((JSONObject)(jaNow.get("cond"))).get("txt").toString();
        nowWeather.pres = (String) jaNow.get("pres").toString();
        nowWeather.tmp = (String) jaNow.get("tmp").toString();
        nowWeather.wind_dir = (String) ((JSONObject)(jaNow.get("wind"))).get("dir").toString();
        nowWeather.wind_sc = (String) ((JSONObject)(jaNow.get("wind"))).get("sc").toString();
        nowWeather.wind_spd = (String) ((JSONObject)(jaNow.get("wind"))).get("spd").toString();
        weatherInfo.nowWeather = nowWeather;
        //"daily_forcast"
        JSONArray jaDaily = (JSONArray)firstNode.getJSONArray("daily_forecast");
        ArrayList<DailyForcast> dailyWeatherList = new ArrayList<DailyForcast>();

        for(int i = 0;i < jaDaily.length(); i++){
            JSONObject day = (JSONObject) jaDaily.get(i);

            DailyForcast dailyTemp = new DailyForcast();

            dailyTemp.date = (String) day.get("date").toString();
            dailyTemp.pres = (String) day.get("pres").toString();
            dailyTemp.tmp_min = (String) ((JSONObject)(day.get("tmp"))).get("min").toString();
            dailyTemp.tmp_max = (String) ((JSONObject)(day.get("tmp"))).get("max").toString();
            dailyTemp.txt_d = (String) ((JSONObject)(day.get("cond"))).get("txt_d").toString();
            dailyTemp.txt_n = (String) ((JSONObject)(day.get("cond"))).get("txt_n").toString();
            dailyTemp.wind_dir = (String) ((JSONObject)(day.get("wind"))).get("dir").toString();
            dailyTemp.wind_sc = (String) ((JSONObject)(day.get("wind"))).get("sc").toString();
            dailyTemp.wind_spd = (String) ((JSONObject)(day.get("wind"))).get("spd").toString();
            dailyWeatherList.add(dailyTemp);
        }
        weatherInfo.dailyWeatherList = dailyWeatherList;
        //"hourly_forcast"
        JSONArray jaHourly = (JSONArray)firstNode.get("hourly_forecast");
        ArrayList<HourlyForcast> hourlyWeatherList = new ArrayList<HourlyForcast>();

        for(int i = 0;i < jaHourly.length(); i++){
            JSONObject hour = (JSONObject) jaHourly.get(i);

            HourlyForcast hourTemp = new HourlyForcast();

            hourTemp.date = (String) hour.get("date").toString();
            hourTemp.pres = (String) hour.get("pres").toString();
            hourTemp.tmp = (String) hour.get("tmp").toString();
            hourTemp.hum = (String) hour.get("hum").toString();
            hourTemp.wind_dir = (String) ((JSONObject)(hour.get("wind"))).get("dir").toString();
            hourTemp.wind_sc = (String) ((JSONObject)(hour.get("wind"))).get("sc").toString();
            hourTemp.wind_spd = (String) ((JSONObject)(hour.get("wind"))).get("spd").toString();
            hourlyWeatherList.add(hourTemp);
        }
        weatherInfo.hourlyWeatherList = hourlyWeatherList;
    }
}
