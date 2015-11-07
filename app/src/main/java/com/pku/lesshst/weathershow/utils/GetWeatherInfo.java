package com.pku.lesshst.weathershow.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import com.pku.lesshst.weathershow.utils.ParseXML;
/**
 * Created by lzb on 2015/10/19.
 */
public class GetWeatherInfo {
    public static final int UPDATE_TODAY_WEATHER = 1;
    public static final int UPDATE_PM_VALUE = 2;

    private Handler mainHandler;
    /**
     * 根据城市编号查询所对应的天气信息
     * @param cityCode
     */
    public void queryWeatherCode(String cityCode, Handler handler){
        mainHandler = handler;
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("The url is:", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    TodayWeather todayWeather = null;
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(address);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200){
                        HttpEntity enity = httpResponse.getEntity();

                        InputStream responseStream = enity.getContent();
                        responseStream = new GZIPInputStream(responseStream);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                        StringBuilder response = new StringBuilder();
                        String str;
                        while((str=reader.readLine()) != null){
                            response.append(str);
                        }
                        String responseStr = response.toString();
                        Log.d("Lesshst, responseStr", responseStr);

                        WeatherStringProcess wsp = new WeatherStringProcess(responseStr, false);
                        todayWeather = wsp.getWeather();
                        if (todayWeather != null){
                            //Log.d("wuapp",todayWeather.toString());
                            //发送消息，由主线程更新UI
                            Message msg = new Message();
                            if(todayWeather.getPm25() == null) {
                                msg.what = UPDATE_TODAY_WEATHER;
                            }
                            else{
                                msg.what = UPDATE_PM_VALUE;
                            }
                            msg.obj = todayWeather;
                            mainHandler.sendMessage(msg);
                        }
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
