package com.pku.lesshst.weathershow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lesshst on 2015/11/8.
 */
public class SunRaiseDownView extends ViewUpdate {

    public static final int startInvalid = 0;
    public static final int endInvalid = 50;

    public static class SunRaiseDownViewInfo{
        private final String text_show = "日出日落";
        private String raiseTime = "06:54";
        private String downTime = "17:03";
        private String monthModeText = "晦日";
        private int monthMode = 1;

        public String getText_show() {
            return text_show;
        }

        public String getRaiseTime() {
            return raiseTime;
        }

        public void setRaiseTime(String raiseTime) {
            this.raiseTime = raiseTime;
        }

        public String getDownTime() {
            return downTime;
        }

        public void setDownTime(String downTime) {
            this.downTime = downTime;
        }

        public String getMonthModeText() {
            return monthModeText;
        }

        public void setMonthModeText(String monthModeText) {
            this.monthModeText = monthModeText;
        }

        public int getMonthMode() {
            return monthMode;
        }

        public void setMonthMode(int monthMode) {
            this.monthMode = monthMode;
        }
    }

    float start_angle = 0;
    float end_angle = 80;
    float time_raise = 6;
    float time_down = 18;
    float time_now = 16;
    Paint paint_shader = new Paint();
    Paint paint_circle = new Paint();
    Paint paint_bezier = new Paint();
    Paint paint_circle_fill = new Paint();

    Path path_bezier = new Path();
    Path path_shader = new Path();
    Path path_circle = new Path();

    Paint paint_text_show = new Paint();
    Paint paint_text_show_date = new Paint();

    public SunRaiseDownView(Context context) {
        super(context);
        initAllPaints();
    }

    private void initAllPaints(){

        paint_shader.setStyle(Paint.Style.FILL);
        paint_shader.setColor(0x88888888);

        paint_circle.setAntiAlias(true);
        paint_circle.setStrokeWidth(2);
        paint_circle.setStyle(Paint.Style.STROKE);
        paint_circle.setColor(0xffffffff);

        paint_circle_fill.setAntiAlias(true);
        paint_circle_fill.setStrokeWidth(2);
        paint_circle_fill.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_circle_fill.setColor(0xffffffff);

        paint_bezier.setAntiAlias(true);
        paint_bezier.setStrokeWidth(2);
        paint_bezier.setStyle(Paint.Style.STROKE);
        paint_bezier.setColor(0xffffffff);

        paint_text_show.setAntiAlias(true);
        paint_text_show.setStrokeWidth(1);
        paint_text_show.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_text_show.setColor(0xffffffff);
        paint_text_show.setTextSize(45);

        paint_text_show_date.setAntiAlias(true);
        paint_text_show_date.setStrokeWidth(1);
        paint_text_show_date.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_text_show_date.setColor(0xffffffff);
        paint_text_show_date.setTextSize(25);
    }

    float r_animator = 0f;

    public void startAnimator(){
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                SunRaiseDownView.this.r_animator = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.setDuration(500);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
    }

    public void endAnimator(){
        SunRaiseDownView.this.r_animator = 0f;
        invalidate();
    }

    SunRaiseDownViewInfo info = new SunRaiseDownViewInfo();

    public void setSunRaiseDownViewInfo(SunRaiseDownViewInfo info){
        this.info = info;
        String raise = info.getRaiseTime();
        this.time_raise = Integer.parseInt(raise.substring(0, 2)) * 60 + Integer.parseInt(raise.substring(raise.length() - 2, raise.length()));

        String down = info.getDownTime();
        this.time_raise = Integer.parseInt(down.substring(0, 2)) * 60 + Integer.parseInt(down.substring(down.length() - 2, down.length()));

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm");
        String now = ft.format(dNow);
        this.time_now = Integer.parseInt(now.substring(0, 2)) * 60 + Integer.parseInt(now.substring(now.length() - 2, now.length()));
    }

    public void setSunRaiseDownViewInfoAndUpdate(SunRaiseDownViewInfo info){
        this.setSunRaiseDownViewInfo(info);
        this.startAnimator();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawBezier(canvas);
        drawCircle(canvas);
    }

    private void drawBezier(Canvas canvas){
        path_bezier.reset();
        path_shader.reset();
        int w = 1080;
        int h = 400;
        float x_circle = w / 2;
        float y_circle = h;

        float ctrly_ratio = 1.0f;

        float padding = 160f;
        float startx = 0f + padding;
        float starty = h;
        float ctrlx = x_circle;
        float ctrly = h - ctrly_ratio * h * this.r_animator;
        float endx = w - padding;
        float endy = h;

        //绘制圆弧上面的小圆点
        float circle_x = padding + (w - 2 * padding) * this.r_animator;
        float t = this.r_animator;
        //https://zh.wikipedia.org/zh-cn/%E8%B2%9D%E8%8C%B2%E6%9B%B2%E7%B7%9A
        float circle_y = ((1 - t) * (1 - t) * starty + 2 * t * (1 - t) * ctrly + t * t * endy);
        canvas.drawCircle(circle_x, circle_y, 20, paint_circle);

//        path_bezier.moveTo(startx, starty);
//        path_bezier.cubicTo(startx, starty, ctrlx, ctrly, endx, endy);
        float w_bezier = w - 2 * padding;
        float x_bezier;
        float y_bezier;
        path_bezier.moveTo(startx, starty);
        path_shader.moveTo(startx, starty);
        for(float i = padding; i < padding + w_bezier; i = i + 3) {
            //绘制圆弧（Bezier曲线）
            x_bezier = i;
            t = (i - padding) / w_bezier;
            y_bezier = ((1 - t) * (1 - t) * starty + 2 * t * (1 - t) * ctrly + t * t * endy);
            path_bezier.lineTo(x_bezier, y_bezier);
            //绘制阴影
            if(x_bezier < circle_x){
                path_shader.lineTo(x_bezier, y_bezier);
            }
        }
        path_shader.lineTo(circle_x, h);
        path_shader.lineTo(startx, starty);
        canvas.drawPath(path_bezier, paint_bezier);
        canvas.drawPath(path_shader, paint_shader);
    }

    private void drawCircle(Canvas canvas){
        path_circle.reset();
        path_shader.reset();
        int w = 1080;
        int h = 500;
        float x_circle = w / 2;
        float y_circle = h;

        float padding = 160f;
        float startx = 0f + padding;
        float starty = h;

        float circle_width = (w - 2 * padding) / 2;
        float angle = start_angle + (end_angle - start_angle)* this.r_animator;
        float radius = circle_width / (float)Math.sin(angle / 180 * (float)Math.PI );
        //绘制部分圆
        x_circle = x_circle;
        y_circle = h + circle_width / (float)Math.tan(angle / 180 * (float)Math.PI);
//        canvas.drawCircle(x_circle, y_circle, radius, paint_bezier);
        RectF rect = new RectF();
        rect.set(x_circle - radius, y_circle - radius, x_circle + radius, y_circle + radius);
        canvas.drawArc(rect, 270 - angle, 2 * angle, false, paint_bezier);
        Log.e("drawArc", String.valueOf(angle));
        //绘制阴影
        float angle_radians = angle / 180 * (float)Math.PI;
        float x_shader = padding + (w - 2 * padding) * this.r_animator * (time_now - time_raise) / (time_down - time_raise);
        double angle_temp = Math.asin((x_circle - x_shader) / radius);
        float y_shader = y_circle - radius * (float)Math.cos(angle_temp);
        double time_now_sweep_angle = (angle_radians - angle_temp) / Math.PI * 180;

        rect = new RectF();
        rect.set(x_circle - radius, y_circle - radius, x_circle + radius, y_circle + radius);
        path_shader.moveTo(startx, starty);
        path_shader.addArc(rect, 270 - angle, (float) time_now_sweep_angle);

        path_shader.lineTo(x_shader, h);
        path_shader.lineTo(startx, starty);
        canvas.drawPath(path_shader, paint_shader);

        //绘制两个圆
        canvas.drawCircle(x_shader, y_shader, 25, paint_circle);
        canvas.drawCircle(x_shader, y_shader, 10, paint_circle_fill);

        //绘制水平线
        canvas.drawLine(padding / 2, h, w - padding / 2, h, paint_circle);

        //绘制 “日出日落”
        Rect rectf = new Rect();
        String data = this.info.getText_show();
        paint_text_show.getTextBounds(data, 0, data.length(), rectf);
        canvas.drawText(data, padding / 2, rectf.height() + 30, paint_text_show);
        //绘制“晦日”
        data = this.info.getMonthModeText();
        paint_text_show.getTextBounds(data, 0, data.length(), rectf);
        paint_text_show.setColor(0xffcccccc);
        canvas.drawText(data, w - padding / 2 - rectf.width(), rectf.height() + 30, paint_text_show);
        paint_text_show.setColor(0xffffffff);

        //绘制初升时间
        data = this.info.getRaiseTime();
        paint_text_show.getTextBounds(data, 0, data.length(), rectf);
        canvas.drawText(data, startx - rectf.width() / 2f, h + rectf.height() + 30, paint_text_show);

        //绘制降落时间
        data = this.info.getDownTime();
        paint_text_show.getTextBounds(data, 0, data.length(), rectf);
        canvas.drawText(data, w - padding - rectf.width() / 2f, h + rectf.height() + 30, paint_text_show);
    }
}
