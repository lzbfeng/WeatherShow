package com.pku.lesshst.weathershow.bean;

/**
 * Created by lzb on 2015/10/19.
 */
public class City {
    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFirstPY;

    public City(String province, String city, String number, String firstPY, String allPY, String allFirstPY){
        this.province = province;
        this.city = city;
        this.number = number;
        this.firstPY = firstPY;
        this.allPY = allPY;
        this.allFirstPY = allFirstPY;
    }

    public void setProvince(String province){
        this.province = province;
    }
    public void setCity(String city){
        this.city = city;
    }
    public String getCityNumber(){
        return this.number;
    }
    public String getProvince()
    {
        return this.province;
    }
    public String getCity()
    {
        return this.city;
    }
}
