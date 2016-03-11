package com.pku.lesshst.weathershow;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lesshst on 2015/12/15.
 */
public class MyService extends Service {

    private static final int UPDATAWEATHER = 0X10;

    private final int GOTOBROADCAST = 0X20;

    public static final String BROADCASTACTION = "com.pku.lesshst.weathershow";

    Timer timer;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
//        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        // updateWeather();
        timer = new Timer();
        timer.schedule( new TimerTask()
        {

            @Override
            public void run()
            {
                // 定时更新
//                String jsonString = getWeather();
                // 发送广播
                Intent intent = new Intent();
                intent.setAction(BROADCASTACTION);
                intent.putExtra( "update", "update from service!" );
                sendBroadcast( intent );
            }
        }, 0, 3600 * 1000 );
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText (this, "Service Destroyed", Toast.LENGTH_LONG).show();
        if(timer != null){
            timer.cancel();
        }
    }
}