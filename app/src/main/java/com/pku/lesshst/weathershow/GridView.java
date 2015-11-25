package com.pku.lesshst.weathershow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by lesshst on 2015/11/7.
 */
public class GridView extends ViewUpdate {

    public static final int startInvalid = 0;
    public static final int endInvalid = 50;

    public class GridViewInfo {
        public final static String humidity = "湿度";
        public final static String visibility = "可见度";
        public String windDirection = "东风";
        public final static String UVRays = "紫外线";
        public final static String pressure = "气压";
        public final static String bodyFeeling = "体感";

        private String humidityValue = "86";
        private String visibilityValue = "6.4";
        private String windDirectionValue = "一级";
        private String UVRaysValue = "最弱";
        private String pressureValue = "1022.0";
        private String bodyFeelingValue = "3.9";

        public void setWindDirection(String windDirection) {
            this.windDirection = windDirection;
        }

        public void setHumidityValue(String humidityValue) {
            this.humidityValue = humidityValue;
        }

        public void setVisibilityValue(String visibilityValue) {
            this.visibilityValue = visibilityValue;
        }

        public void setWindDirectionValue(String windDirectionValue) {
            this.windDirectionValue = windDirectionValue;
        }

        public void setUVRaysValue(String UVRaysValue) {
            this.UVRaysValue = UVRaysValue;
        }

        public void setPressureValue(String pressureValue) {
            this.pressureValue = pressureValue;
        }

        public void setBodyFeelingValue(String bodyFeelingValue) {
            this.bodyFeelingValue = bodyFeelingValue;
        }

        public String getWindDirection() {
            return windDirection;
        }

        public String getHumidityValue() {
            return humidityValue;
        }

        public String getVisibilityValue() {
            return visibilityValue;
        }

        public String getWindDirectionValue() {
            return windDirectionValue;
        }

        public String getUVRaysValue() {
            return UVRaysValue;
        }

        public String getPressureValue() {
            return pressureValue;
        }

        public String getBodyFeelingValue() {
            return bodyFeelingValue;
        }
    }
    String[] titles = new String[]{"湿度(%)", "可见度(km)", "北风", "紫外线", "气压", "体感(℃)"};
    String[] values = new String[]{"86", "6.4", "一级", "最弱", "1022.0", "3.9"};
    float r_animator = 0f;
    Paint paint = new Paint();
    Paint paint_values = new Paint();
    Paint paint_titles = new Paint();
    int text_values_size = 60;

    public GridView(Context context) {
        super(context);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);

        paint_titles.setAntiAlias(true);
        paint_titles.setStrokeWidth(2);
        paint_titles.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_titles.setColor(0xcccccccc);
        paint_titles.setTextSize(30);

        paint_values.setStrokeWidth(2);
        paint_values.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_values.setColor(Color.WHITE);
        paint_values.setTextSize(60);
    }
    public void startAnimator(){
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                GridView.this.r_animator = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.setDuration(500);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
    }

    public void endAnimator(){
        GridView.this.r_animator = 0f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrid(canvas);
    }

    public void setGridViewInfo(GridViewInfo info){
        titles[2] = info.getWindDirection();
        values[0] = info.getHumidityValue();
        values[1] = info.getVisibilityValue();
        values[2] = info.getWindDirectionValue();
        values[3] = info.getUVRaysValue();
        values[4] = info.getPressureValue();
        values[5] = info.getBodyFeelingValue();
    }

    public void setGridViewInfoAndUpdate(GridViewInfo info){
        this.setGridViewInfo(info);
        this.startAnimator();
    }

    private void drawGrid(Canvas canvas) {
        int w = 1080;
        int h = 600;

        int step_x = w / 3;
        int step_y = h / 2;

        //绘制横轴
        canvas.drawLine(0, 0, w, 0, paint);
        canvas.drawLine(0, step_y, w, step_y, paint);
        canvas.drawLine(0, h, w, h, paint);

        //绘制纵轴
        canvas.drawLine(0 + step_x, 0, 0 + step_x, h, paint);
        canvas.drawLine(step_x + step_x, 0, step_x+ step_x, h, paint);

        int x = 0;
        int y = 0;
        //绘制
        Rect rect = new Rect();
        int index = 0;
        int text_w = 0;
        int text_h = 0;
        int padding = 5;
        int title_text_shift = 30;
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++) {
                x = j * step_x + step_x / 2;
                y = i * step_y + step_y / 2;
                index = i * 3 + j;
                paint_titles.getTextBounds(titles[index], 0, titles[index].length(), rect);
                text_w = Math.abs(rect.right - rect.left);
                text_h = Math.abs(rect.bottom - rect.top);
                int x_show = x - text_w / 2;
                int y_show = (int)(y + text_h / 2 - title_text_shift * this.r_animator); //以左下角为原点
                canvas.drawText(titles[index], x_show, y_show, paint_titles);

                paint_values.setTextSize(text_values_size * this.r_animator);
//                paint_values.setAlpha((int)(this.r_animator));
                paint_values.getTextBounds(values[index], 0, values[index].length(), rect);
                text_w = Math.abs(rect.right - rect.left);
                text_h = Math.abs(rect.bottom - rect.top);
                x_show = x - text_w / 2;
                y_show = y + text_h / 2 + 30;

                canvas.drawText(values[index], x_show, y_show, paint_values);
//                Log.e("r_animator", "" + this.r_animator);
            }
        }
    }
}
