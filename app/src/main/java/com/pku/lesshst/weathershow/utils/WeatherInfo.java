package com.pku.lesshst.weathershow.utils;

import java.util.ArrayList;

/**
 * Created by lzb on 2015/9/30.
 */
public class WeatherInfo {
    public static class DailyForcast{
        public String date = "";
        public String txt_d = "";
        public String txt_n = "";
        public String wind_dir = "";
        public String wind_sc = "";
        public String wind_spd = "";
        public String tmp_max = "";
        public String tmp_min = "";
        public String pres = "";

        public String toString(){
            String strShow = "今天天气：\n";
            strShow += "日期：" + date + "\n";
            strShow += "白天：" + txt_d + "\n";
            strShow += "夜间：" + txt_n + "\n";
            strShow += "最低温度：" + tmp_min + "\n";
            strShow += "最高温度：" + tmp_max + "\n";
            strShow += "风向：" + wind_dir + "\n";
            strShow += "风类型：" + wind_sc + "\n";
            strShow += "风速：" + wind_spd + "\n";
            strShow += "压强：" + pres + "\n\n";
            return strShow;
        }
    }

    public static class HourlyForcast{
        public String date = "";
        public String wind_dir = "";
        public String wind_sc = "";
        public String wind_spd = "";
        public String pres = "";
        public String tmp = "";
        public String hum = "";

        public String toString(){
            String strShow = "此小时天气：\n";
            strShow += "时间：" + date + "\n";
            strShow += "温度：" + tmp + "\n";
            strShow += "风向：" + wind_dir + "\n";
            strShow += "风类型：" + wind_sc + "\n";
            strShow += "风速：" + wind_spd + "\n";
            strShow += "湿度：" + hum + "\n";
            strShow += "压强：" + pres + "\n\n";
            return strShow;
        }
    }

    public static class NowWeather{
        public String tmp;
        public String txt;
        public String wind_dir;
        public String wind_sc;
        public String wind_spd;
        public String pres;

        public String toString(){
            String strShow = "此刻天气：\n";
            strShow += "天气：" + txt + "\n";
            strShow += "温度：" + tmp + "\n";
            strShow += "风向：" + wind_dir + "\n";
            strShow += "风类型：" + wind_sc + "\n";
            strShow += "风速：" + wind_spd + "\n";
            strShow += "压强：" + pres + "\n\n";
            return strShow;
        }
    }

    public NowWeather nowWeather;
    public ArrayList<DailyForcast> dailyWeatherList;
    public ArrayList<HourlyForcast> hourlyWeatherList;
    public String cityName;
}
