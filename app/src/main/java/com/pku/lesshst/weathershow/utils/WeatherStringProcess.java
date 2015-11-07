package com.pku.lesshst.weathershow.utils;

/**
 * Created by lzb on 2015/10/19.
 */
public class WeatherStringProcess {
    private boolean mIsJson = true;
    private String mWeatherStr = null;

    public WeatherStringProcess(String weatherStr, boolean isJson){
        mIsJson = isJson;
        mWeatherStr = weatherStr;
    }

    public TodayWeather getWeather(){
        if(!mIsJson){
            ParseXML px = new ParseXML();
            TodayWeather todayWeather = px.parseXML(mWeatherStr);
            return todayWeather;
        }
        return null;
    }

}
