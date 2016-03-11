package com.pku.lesshst.weathershow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by lesshst on 2015/12/15.
 */
public class ServiceActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_layout);
    }

    public void StartService(View view){
        startService(new Intent(getBaseContext(), MyService.class));
    }
    public void StopService(View view){
        stopService(new Intent(getBaseContext(), MyService.class));
    }
}
