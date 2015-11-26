package com.pku.lesshst.weathershow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * Created by lesshst on 2015/11/26.
 */
public class TemperatureView extends ViewUpdate  {
    public TemperatureView(Context context) {
        super(context);
        Typeface tf = Typeface.createFromAsset(Global.myAsset, "fonts/msyh.ttf");
        paint_temp.setTypeface(tf);
        paint_temp.setStrokeWidth(1);
        paint_temp.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_temp.setColor(Color.WHITE);
        paint_temp.setTextSize(200);

        paint_weather.setStrokeWidth(1);
        paint_weather.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_weather.setColor(0xffdddddd);
        paint_weather.setTextSize(40);
    }

    int temp = -8;
    String weather = "晴天";
    String air_quality = "空气质量";

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setAir_quality(String air_quality) {
        this.air_quality = air_quality;
    }

    Paint paint_temp = new Paint();
    Paint paint_weather = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        Rect rect = new Rect();

        //temperature
        String data = String.valueOf(temp) + "°";
        paint_temp.getTextBounds(data, 0, data.length(), rect);
        canvas.drawText(data, w / 2f - rect.width() / 2, rect.height() + h / 3f, paint_temp);

        //weather
        data = weather + "|" + air_quality;
        paint_weather.getTextBounds(data, 0, data.length(), rect);
        canvas.drawText(data, w / 2f - rect.width() / 2, rect.height() + h / 3f + 200, paint_weather);
    }
}
