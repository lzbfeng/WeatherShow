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
public class ProbabilityView extends View {
    public ProbabilityView(Context context) {
        super(context);
        initAllPaints();
    }

    Paint paint_circle_out = new Paint();
    Paint paint_pointer = new Paint();
    Paint paint_circle_inner = new Paint();
    Path mPath = new Path();

    private void initAllPaints(){
        paint_circle_out.setStrokeWidth(2);
        paint_circle_out.setStyle(Paint.Style.STROKE);
        paint_circle_out.setColor(Color.WHITE);

        paint_pointer.setAntiAlias(true);
        paint_pointer.setStrokeWidth(2);
        paint_pointer.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_pointer.setColor(0xffffffff);
        paint_pointer.setTextSize(30);

        paint_circle_inner.setAntiAlias(true);
        paint_circle_inner.setStrokeWidth(8);
        paint_circle_inner.setStyle(Paint.Style.STROKE);
        paint_circle_inner.setColor(0xffffffff);
        paint_circle_inner.setTextSize(30);
    }

    float r_animator = 0f;

    public void startAnimator(){
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ProbabilityView.this.r_animator = (float) animation.getAnimatedValue();
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

        drawRaindrop(canvas, 100, 100, 50, 10);
        drawRaindrop(canvas, 200, 100, 50, 30);
        drawRaindrop(canvas, 300, 100, 50, 50);
        drawRaindrop(canvas, 400, 100, 50, 70);
        drawRaindrop(canvas, 500, 100, 50, 90);

        drawRaindrop(canvas, 100, 300, 50, 20);
        drawRaindrop(canvas, 200, 300, 50, 40);
        drawRaindrop(canvas, 300, 300, 50, 60);
        drawRaindrop(canvas, 400, 300, 50, 80);
        drawRaindrop(canvas, 500, 300, 50, 100);
        drawRaindrop(canvas, 600, 300, 50, 7);
    }

    private void drawRaindrop(Canvas canvas, float x, float y, float radius, float probability){
        float x_circle = x;
        float y_circle = y;

        radius = 50;
        RectF rect = new RectF();
        rect.set(x_circle - radius, y_circle - radius, x_circle + radius, y_circle + radius);
        canvas.drawArc(rect, 330, 240, false, paint_circle_inner);

        double angle = 60f / 180 * Math.PI;
        float end_x = x_circle;
        float end_y = (float)(y_circle - radius / Math.cos(angle));
        //绘制左边的直线
        float start_x_left = (float)(x_circle - radius * Math.sin(angle));
        float start_y_left = (float)(y_circle - radius * Math.cos(angle));
        canvas.drawLine(start_x_left, start_y_left, end_x, end_y, paint_circle_inner);
        //绘制右边的直线
        float start_x_right = (float)(x_circle + radius * Math.sin(angle));
        float start_y_right = start_y_left;
        canvas.drawLine(start_x_right, start_y_right, end_x, end_y, paint_circle_inner);

        //绘制雨滴内部的水量
        //probability = 25;
        float scale = probability / 100f;
        float sweep_pro = 360 * scale;
        radius = 40;
        rect.set(x_circle - radius, y_circle - radius, x_circle + radius, y_circle + radius);
        if(sweep_pro < 240) {
            mPath.reset();
            mPath.addArc(rect, 90 - sweep_pro / 2, sweep_pro);
            float angle_pro = sweep_pro / 180 * (float) Math.PI;
            float startx = x_circle - radius * (float) Math.sin(angle_pro / 2);
            float starty = y_circle + radius * (float) Math.cos(angle_pro / 2);
            float ctrlx = (x_circle - startx) * 2 / 3 + startx;
            float ctrly = y_circle + radius * (float) Math.cos(angle_pro / 2) - 40 * (0.5f - Math.abs(0.5f - scale));
            float endx = x_circle;
            float endy = y_circle + radius * (float) Math.cos(angle_pro / 2);
            mPath.cubicTo(startx, starty, ctrlx, ctrly, endx, endy);

            startx = endx;
            starty = endy;
            endx = x_circle + radius * (float) Math.sin(angle_pro / 2);
            endy = y_circle + radius * (float) Math.cos(angle_pro / 2);
            ctrlx = startx + (endx - startx) * 1 / 3;
            ctrly = y_circle + radius * (float) Math.cos(angle_pro / 2) + 40 * (0.5f - Math.abs(0.5f - scale));
            mPath.cubicTo(startx, starty, ctrlx, ctrly, endx, endy);
        }
        else{
            mPath.reset();
            mPath.addArc(rect, 90 - 240 / 2, 240);

            float jiaodu = (sweep_pro - 240) / 2;
            jiaodu = jiaodu / 180 * (float) Math.PI;
            float jiaodu1 = jiaodu + 30 / 180f * (float) Math.PI;
            float bianchang = radius / (float) Math.cos(jiaodu);
            float startx = x_circle - bianchang * (float)Math.cos(jiaodu1);
            float starty = y_circle - bianchang * (float) Math.sin(jiaodu1);
            float ctrlx = (x_circle - startx) * 2 / 3 + startx;
            float ctrly = starty - 4 * (5 - Math.abs(5 - scale));
            float endx = x_circle;
            float endy = starty;
            mPath.cubicTo(startx, starty, ctrlx, ctrly, endx, endy);

            startx = endx;
            starty = endy;
            endx = x_circle + bianchang * (float)Math.cos(jiaodu1);
            endy = starty;
            ctrlx = startx + (endx - startx) * 1 / 3;
            ctrly = starty + 4 * (5 - Math.abs(5 - scale));
            mPath.cubicTo(startx, starty, ctrlx, ctrly, endx, endy);
        }
        canvas.drawPath(mPath, paint_pointer);
        String str_show_pro = String.valueOf((int)probability);
        Rect rectf = new Rect();
        paint_pointer.getTextBounds(str_show_pro, 0, str_show_pro.length(), rectf);

        canvas.drawText(str_show_pro, x_circle - Math.abs(rect.right - rect.left) / 2, y_circle + radius + 40, paint_pointer);
    }
}
