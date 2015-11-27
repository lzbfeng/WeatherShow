package com.pku.lesshst.weathershow.utils;

import com.pku.lesshst.weathershow.GridView;
import com.pku.lesshst.weathershow.PMView;
import com.pku.lesshst.weathershow.ProbabilityView;
import com.pku.lesshst.weathershow.SunRaiseDownView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by lzb on 2015/10/19.
 */
public class TodayWeather {
    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String aqi;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;
    private String dayType;
    private String nightType;
    private String sunrise;
    private String sunset;
    private String time;
    private ForcastWeather forcastWeathers[];

    public static class ForcastWeather{
        private String high;
        private String low;
        private String type;

        public void setHigh(String high) {
            this.high = high;
        }

        public void setLow(String low) {
            this.low = low;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getHigh() {
            return high;
        }

        public String getLow() {
            return low;
        }

        public String getType() {
            return type;
        }
    }
    public void setTime(String time) {
        this.time = time;
    }

    public ForcastWeather[] getForcastWeathers() {
        return forcastWeathers;
    }

    public void setForcastWeathers(ForcastWeather[] forcastWeathers) {
        this.forcastWeathers = forcastWeathers;
    }

    public String getTime() {
        return time;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getAqi() {
        return aqi;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public String getDayType() {
        return this.dayType;
    }

    public void setNightType(String nightType) {
        this.nightType = nightType;
    }

    public String getNightType() {
        return this.nightType;
    }

    public int[] getTempsHighDay(){
        int[] temps_day = new int[]{23, 34, 12, 34, 21};
        for(int i = 0; i < 5; i++){
            String high = forcastWeathers[i].getHigh();
            temps_day[i] = Integer.parseInt(high.substring(3, high.length() - 1));
        }
        return temps_day;
    }

    public int[] getTempsLowDay(){
        int[] temps_day = new int[]{23, 34, 12, 34, 21};
        for(int i = 0; i < 5; i++){
            String low = forcastWeathers[i].getLow();
            temps_day[i] = Integer.parseInt(low.substring(3, low.length() - 1));
        }
        return temps_day;
    }
    String UVRayValues[] = {"最强", "强", "中", "弱", "最弱"};
    public GridView.GridViewInfo getGridViewInfo(){
        GridView.GridViewInfo gridViewInfo = new GridView.GridViewInfo();
        gridViewInfo.setHumidityValue(getShidu().substring(0, getShidu().length() - 1));
        Random rdm = new Random(System.currentTimeMillis());
        gridViewInfo.setVisibilityValue(String.valueOf(Math.abs(rdm.nextInt()) % 9) + "." + String.valueOf(Math.abs(rdm.nextInt()) % 10)); //weather api don't have this data
        gridViewInfo.setWindDirection(getFengxiang());
        gridViewInfo.setWindDirectionValue(getFengli());
        gridViewInfo.setUVRaysValue(UVRayValues[Math.abs(rdm.nextInt()) % 5]);  //weather api don't have this data
        int pressure = 1020 + rdm.nextInt() % 10;
        String pressure_str = String.valueOf(pressure) + "." + Math.abs(rdm.nextInt()) % 10;
        gridViewInfo.setPressureValue(pressure_str);    //weather api don't have this data
        gridViewInfo.setBodyFeelingValue(String.valueOf(Math.abs(rdm.nextInt()) % 9) + "." + String.valueOf(Math.abs(rdm.nextInt()) % 10));    //weather api don't have this data
        return gridViewInfo;
    }

    public PMView.PMViewInfo getPMViewInfo(){
        PMView.PMViewInfo pminfo = new PMView.PMViewInfo();
        pminfo.setAQI(Integer.parseInt(getAqi()));
        pminfo.setPM2_5Value(Integer.parseInt(getPm25()));
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("MM月dd日");
        String day = ft.format(dNow);
        pminfo.setDate(day);
        pminfo.setTime(getTime().substring(0, getTime().length() - 2));
        return pminfo;
    }

    public ProbabilityView.ProbabilityViewInfo getProbabilityViewInfo(){
        int probabilities[] = new int[]{12, 45, 76, 29};
        Random rdm = new Random(System.currentTimeMillis());
        for(int i = 0; i < 4; i++)
            probabilities[i] = Math.abs(rdm.nextInt()) % 99;
        ProbabilityView.ProbabilityViewInfo probabilityViewInfo = new ProbabilityView.ProbabilityViewInfo();
        probabilityViewInfo.setProbabilities(probabilities);
        return probabilityViewInfo;
    }

    public SunRaiseDownView.SunRaiseDownViewInfo getSunRaiseDownViewInfo(){
        SunRaiseDownView.SunRaiseDownViewInfo info = new SunRaiseDownView.SunRaiseDownViewInfo();
        info.setRaiseTime(getSunrise());
        info.setDownTime(getSunset());
        return info;
    }
    @Override
    public String toString() {
        return "TodayWeather{" +
                "city='" + city + '\'' +
                ",updatetime='" + updatetime + '\'' +
                ",wendu='" + wendu + '\'' +
                ",shidu='" + shidu + '\'' +
                ",pm25='" + pm25 + '\'' +
                ",quality='" + quality + '\'' +
                ",fengxiang='" + fengxiang + '\'' +
                ",fengli='" + fengli + '\'' +
                ",date='" + date + '\'' +
                ",high='" + high + '\'' +
                ",low='" + low + '\'' +
                ",type='" + type + '\'' +
                "}";
    }
}
