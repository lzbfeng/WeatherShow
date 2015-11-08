package com.pku.lesshst.weathershow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by lesshst on 2015/11/7.
 */
public class PMView extends View {

    float r_animator = 0f;
    Paint paint_circle_out = new Paint();
    Paint paint_circle_left = new Paint();
    Paint paint_circle_right  = new Paint();
    Paint paint_pointer = new Paint();
    Paint paint_circle_inner = new Paint();
    Paint paint_text_pm_value = new Paint();
    Paint paint_text_pm_source = new Paint();

    int all_quality = 500;
    int quality = 300;
    private final int all_angle = 300;
    private final int left_right_circle_stroke_width = 30;
    private String text_pm_source = "中国环境监测总站 11月08日21:00发布";

    public PMView(Context context) {
        super(context);
        initAllPaints();
    }

    private void initAllPaints(){
        paint_circle_out.setStrokeWidth(2);
        paint_circle_out.setStyle(Paint.Style.STROKE);
        paint_circle_out.setColor(Color.WHITE);

        paint_circle_left.setAntiAlias(true);
        paint_circle_left.setStrokeWidth(left_right_circle_stroke_width);
        paint_circle_left.setStyle(Paint.Style.STROKE);
        paint_circle_left.setColor(0xffffffff);
        paint_circle_left.setTextSize(30);

        paint_circle_right.setAntiAlias(true);
        paint_circle_right.setStrokeWidth(left_right_circle_stroke_width);
        paint_circle_right.setStyle(Paint.Style.STROKE);
        paint_circle_right.setColor(0xcccccccc);
        paint_circle_right.setTextSize(30);

        paint_pointer.setAntiAlias(true);
        paint_pointer.setStrokeWidth(5);
        paint_pointer.setStyle(Paint.Style.STROKE);
        paint_pointer.setColor(0xffffffff);
        paint_pointer.setTextSize(30);

        paint_circle_inner.setAntiAlias(true);
        paint_circle_inner.setStrokeWidth(5);
        paint_circle_inner.setStyle(Paint.Style.STROKE);
        paint_circle_inner.setColor(0xffffffff);
        paint_circle_inner.setTextSize(30);

        paint_text_pm_value.setAntiAlias(true);
        paint_text_pm_value.setStrokeWidth(1);
        paint_text_pm_value.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_text_pm_value.setColor(0xffffffff);
        paint_text_pm_value.setTextSize(50);

        paint_text_pm_source.setAntiAlias(true);
        paint_text_pm_source.setStrokeWidth(1);
        paint_text_pm_source.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_text_pm_source.setColor(0xcccccccc);
        paint_text_pm_source.setTextSize(40);
    }

    public void startAnimator(){
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PMView.this.r_animator = (float) animation.getAnimatedValue();
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
        drawCircles(canvas);
    }

    private void drawCircles(Canvas canvas) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        int radius_out = 300;
        int radius_left_right = 250;
        int radius_inner = 30;
        int x_circle = w / 2;
        int y_circle = h / 2;

        RectF rect = new RectF();
        //画外围圆
        rect.set(x_circle - radius_out, y_circle - radius_out, x_circle + radius_out, y_circle + radius_out);
        canvas.drawArc(rect, 120, 300, false, paint_circle_out);
        canvas.drawArc(rect, 120, 180, false, paint_circle_out);
        //画左侧圆
        rect.set(x_circle - radius_left_right, y_circle - radius_left_right, x_circle + radius_left_right, y_circle + radius_left_right);
        float sweepAngle = quality / (float)all_quality * all_angle * this.r_animator;
        canvas.drawArc(rect, 120, sweepAngle, false, paint_circle_left);
        //画右侧圆，设置渐变色
        int[] colors = {0x55555555, 0xdddddddd};
        float[] positions = {0.33f, 1f};
        SweepGradient gradient = new SweepGradient(x_circle, y_circle, colors , positions);
        paint_circle_right.setShader(gradient);
        canvas.drawArc(rect, 120, 240, false, paint_circle_right);

        int[] colors1 = {0xdddddddd, 0xffffffff};
        float[] positions1 = {0.0f, 0.167f};
        SweepGradient gradient1 = new SweepGradient(x_circle, y_circle, colors1 , positions1);
        paint_circle_right.setShader(gradient1);
        canvas.drawArc(rect, 0, 60, false, paint_circle_right);
        //画内圆
        canvas.drawCircle(x_circle, y_circle, radius_inner, paint_circle_inner);
        //画指针
        double angle = (sweepAngle - 60) / 360 * 2 * Math.PI;
        float startx = (float)(x_circle - Math.cos(angle) * radius_inner);
        float starty = (float)(y_circle - Math.sin(angle) * radius_inner);
        float endx = (float)(x_circle - Math.cos(angle) * (radius_left_right + left_right_circle_stroke_width / 2));
        float endy = (float)(y_circle - Math.sin(angle) * (radius_left_right + left_right_circle_stroke_width / 2));
        canvas.drawLine(startx, starty, endx, endy, paint_pointer);

        //显示PM值
        String pm_value = String.valueOf((int)(quality * this.r_animator));
        Rect text_rect = new Rect();
        paint_text_pm_value.getTextBounds(pm_value, 0, pm_value.length(), text_rect);
        canvas.drawText(pm_value, x_circle - Math.abs(text_rect.right - text_rect.left) / 2f, y_circle + radius_out * 2 / 3f, paint_text_pm_value);

        //显示数据来源
        paint_text_pm_source.getTextBounds(text_pm_source, 0, text_pm_source.length(), text_rect);
        canvas.drawText(text_pm_source, x_circle - Math.abs(text_rect.right - text_rect.left) / 2f, y_circle + radius_out + 30, paint_text_pm_source);
    }
}
