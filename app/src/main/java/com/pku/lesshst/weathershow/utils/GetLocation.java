package com.pku.lesshst.weathershow.utils;


import com.pku.lesshst.weathershow.utils.GeoJsonControl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lzb on 2015/10/10.
 */
public class GetLocation {

    public String GetLocationFromLatLng(double lat, double lng){
        String httpUrl = "http://apis.baidu.com/3023/geo/address";
        String httpArg = "l=40.0492023635%2C116.2955249742";
        httpArg = "l=" + Double.toString(lat) + "%2C" + Double.toString(lng);
        String jsonResult = request(httpUrl, httpArg);

        String quname = null;
        GeoJsonControl geojson = new GeoJsonControl(jsonResult);
        try{
            quname = geojson.getLocationCity();
        }
        catch(Exception e){

        }
        return quname;
    }


    /**
     * @param httpUrl
     *            :请求接口
     * @param httpArg
     *            :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey",  "a101f0b8decf115d4ae30f6611d1034f");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
