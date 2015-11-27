package com.pku.lesshst.weathershow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by lesshst on 2015/11/7.
 */
public class LineView extends ViewUpdate {

    public static final int startInvalid = 50;
    public static final int endInvalid = 540;
    private final int count_days = 5;
    Paint paint = new Paint();
    Paint paint_text = new Paint();
    Path path = new Path();

    int[] temps_day = new int[]{23, 34, 12, 34, 21};
    int[] temps_night = new int[]{12, 14, 10, 8, 11};

    float r_animator = 0f;
    Paint paint_yuandian = new Paint();
    Paint paint_dashline = new Paint();

    public void setTemps_day(int[] temps_day) {
        this.temps_day = temps_day;
    }

    public void setTemps_night(int[] temps_night) {
        this.temps_night = temps_night;
    }

    public LineView(Context context) {
        super(context);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);

        paint_yuandian.setStrokeWidth(5);
        paint_yuandian.setStyle(Paint.Style.FILL);
        paint_yuandian.setColor(Color.WHITE);

        paint_dashline.setAntiAlias(true);
        paint_dashline.setStrokeWidth(2);
        paint_dashline.setStyle(Paint.Style.STROKE);
        paint_dashline.setColor(Color.WHITE);

        PathEffect effects = new DashPathEffect(new float[] { 10, 5, 5, 5}, 1);
        paint_dashline.setPathEffect(effects);

        paint_text.setStrokeWidth(1);
        paint_text.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_text.setColor(Color.WHITE);
        paint_text.setTextSize(30);
    }

    public void startAnimator(){
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LineView.this.r_animator = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.setDuration(500);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
    }
    public void endAnimator(){
        LineView.this.r_animator = 0f;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int max = -1000;
        int min = 1000;
        int sum = 0;
        for(int i = 0; i < temps_day.length; i++){
            if(temps_day[i] > max)
                max = temps_day[i];
            if(temps_day[i] < min)
                min = temps_day[i];
            sum += temps_day[i];
        }

        for(int i = 0; i < temps_night.length; i++){
            if(temps_night[i] > max)
                max = temps_night[i];
            if(temps_night[i] < min)
                min = temps_night[i];
            sum += temps_night[i];
        }
        double avg = sum / (double)(temps_day.length * 2);

        paintTemps(canvas, temps_day, true, max, min, avg);
        paintTemps(canvas, temps_night, false, max, min, avg);
    }

    public void setTempsAndUpdate(int [] temps_day, int [] temps_night){
        if(!(temps_day.length == 5 && temps_night.length == 5)){
            Log.e("LineView", "The length of temps should be 5!");
            return;
        }
        for(int i = 0; i < temps_day.length; i++){
            this.temps_day[i] = temps_day[i];
            this.temps_night[i] = temps_night[i];
        }
        this.startAnimator();
    }

    private void paintTemps(Canvas canvas, int [] temps, boolean isDay, int max, int min, double avg){
        path.reset();
        int w = 1080;
        int step = w / count_days;
        int canvas_height = 600;

        double ratio = canvas_height / (max - min + 20);

        int x = 0;
        int y = (int)(canvas_height / 2f - (temps[0] - avg) * ratio * r_animator);
        path.moveTo(x, y);

        for(int i = 0; i < count_days; i++){
            x = i * step + step / 2;
            y = (int)(canvas_height / 2f - ratio * (temps[i] - avg) * r_animator);
            path.lineTo(x, y);

        }
        x = w;
        y = (int)(canvas_height / 2f - (temps[count_days - 1] - avg)* ratio * r_animator);
        path.lineTo(x, y);
        canvas.drawPath(path, paint);

        if(isDay) {
            //绘制虚线图
            x = 1 * step + step / 2;
            y = (int) (canvas_height / 2f - ratio * (temps[1] - avg) * r_animator);
            path.reset();
            path.moveTo(x, canvas_height);
            path.lineTo(x, y);
            canvas.drawPath(path, paint_dashline);
            //绘制小圆点
            canvas.drawCircle(x, y, 10, paint_yuandian);

            canvas.drawText(String.valueOf(temps[1]) + "℃", x + 20, y - 20, paint_text);
        }
    }
}
