package com.pku.lesshst.weathershow;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import com.pku.lesshst.weathershow.utils.GetWeatherInfo;
import com.pku.lesshst.weathershow.utils.TodayWeather;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by lesshst on 2015/12/15.
 */
public class MyWidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds){
        // TODO Auto-generated method stub
        //显示时间的定时器，每秒刷新一次
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager), 1, 1000);
        //显示天气的定时器，设置为没小时刷新一次
        Timer wTimer = new Timer();
        wTimer.scheduleAtFixedRate(new MyWeather(context, appWidgetManager), 1, 3600000);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
    }

    //显示时间信息处理
    public class MyTime extends TimerTask {
        RemoteViews remoteViews;
        AppWidgetManager appWidgetManager;
        ComponentName thisWidget;
        DateFormat format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
        public MyTime(Context context, AppWidgetManager appWidgetManager) {
            this.appWidgetManager = appWidgetManager;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
            thisWidget = new ComponentName(context, MyWidgetProvider.class);
        }
        @Override
        public void run() {
            remoteViews.setTextViewText(R.id.time, format.format(new Date()));
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
    }
    //显示天气信息处理
    public class MyWeather extends TimerTask {
        RemoteViews remoteViews;
        AppWidgetManager appWidgetManager;
        ComponentName thisWidget;
        public MyWeather(Context context, AppWidgetManager appWidgetManager) {
            this.appWidgetManager = appWidgetManager;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
            thisWidget = new ComponentName(context, MyWidgetProvider.class);
        }
        private Handler mainHandler = new Handler() {
            public void handleMessage(Message msg){
                switch (msg.what){
                    case GetWeatherInfo.UPDATE_TODAY_WEATHER:
                        updateTodayWeather((TodayWeather)msg.obj);
                        break;
//                case GetWeatherInfo.UPDATE_PM_VALUE:
//                    updateAdminAreaPMValue((TodayWeather)msg.obj);
//                    break;】}
                    default:
                        break;
                }
            }
        };
        private void updateTodayWeather(TodayWeather todayWeather){
            remoteViews.setTextViewText(R.id.temperature, city +":"+ todayWeather.getWendu());
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
        String city = "大兴";
        @Override
        public void run() {
            String temp = "";

            try {
                GetWeatherInfo getweatherInfo = new GetWeatherInfo();
                getweatherInfo.queryWeatherCode("101010100", mainHandler);

            } catch (Exception e) {
                e.printStackTrace();
                remoteViews.setTextViewText(R.id.temperature, city+"：-5℃");
                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
            }
        }
        //根据天气图标url从网络获取图片
        public Bitmap getBitmap(String path) throws IOException {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode() == 200){
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
            return null;
        }
    }
}
