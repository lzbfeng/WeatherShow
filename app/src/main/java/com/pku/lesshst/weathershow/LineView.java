package com.pku.lesshst.weathershow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by lesshst on 2015/11/7.
 */
public class LineView extends View {
    public LineView(Context context) {
        super(context);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);

        paint_yuandian.setStrokeWidth(5);
        paint_yuandian.setStyle(Paint.Style.FILL);
        paint_yuandian.setColor(Color.WHITE);

        paint_dashline.setAntiAlias(true);
        paint_dashline.setStrokeWidth(2);
        paint_dashline.setStyle(Paint.Style.STROKE);
        paint_dashline.setColor(Color.WHITE);

        PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5}, 1);
        paint_dashline.setPathEffect(effects);
    }
    public void startAnimator(){
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LineView.this.r_animator = (float)animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.setDuration(500);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paintTemps(canvas, temps_day);
//        paintTemps(canvas, temps_night);
    }
    private final int count_days = 5;
    Paint paint = new Paint();
    Path p = new Path();
    int[] temps_day = new int[]{23, 34, 12, 34, 21};
    int[] temps_night = new int[]{12, 14, 10, 8, 11};
    float r_animator = 0f;
    Paint paint_yuandian = new Paint();
    Paint paint_dashline = new Paint();
    Paint strockpaint;
    private void paintTemps(Canvas canvas, int [] temps){
        p.reset();
        int w = 1080;
        int step = w / count_days;
        int canvas_height = 400;
        double ratio = 10;

        int x = 0;
        int y = (int)(canvas_height - temps[0] * ratio * r_animator);
        p.moveTo(x, y);

        for(int i = 0; i < count_days; i++){
            x = i * step + step / 2;
            y = (int)(canvas_height - ratio * temps[i] * r_animator);
            p.lineTo(x, y);
            if(i == 1){
                //绘制虚线图
                x = 1 * step + step / 2;
                y = (int)(canvas_height - ratio * temps[1] * r_animator);
                strockpaint = new Paint();
                strockpaint.setAntiAlias(true);
                strockpaint.setStyle(Paint.Style.STROKE);
                strockpaint.setColor(Color.WHITE);
                strockpaint.setStrokeWidth(1);
                DashPathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
                strockpaint.setPathEffect(effects);
                canvas.drawLine(x, canvas_height, x, y, strockpaint);
                //绘制小圆点
                canvas.drawCircle(x, y, 10, paint_yuandian);
            }
        }
        x = w;
        y = (int)(canvas_height - temps[count_days - 1] * ratio * r_animator);
        p.lineTo(x, y);
        canvas.drawPath(p, paint);

        //canvas.drawLine(0, 200, 400, 200, paint_dashline);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE);
        p.setStrokeWidth(1);
        PathEffect effects = new DashPathEffect(new float[] { 10, 20, 10, 20}, 1);
        p.setPathEffect(effects);
        canvas.drawLine(0, 200, 400, 200, p);
    }
}
