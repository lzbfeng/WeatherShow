package com.pku.lesshst.weathershow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by lesshst on 2015/11/8.
 */
public class SunRaiseDownView extends View {
    public SunRaiseDownView(Context context) {
        super(context);
        initAllPaints();
    }

    Paint paint_shader = new Paint();
    Paint paint_circle = new Paint();
    Paint paint_bezier = new Paint();
    Path path_bezier = new Path();
    Path path_shader = new Path();

    private void initAllPaints(){

        paint_shader.setStyle(Paint.Style.FILL);
        paint_shader.setColor(0x88888888);

        paint_circle.setAntiAlias(true);
        paint_circle.setStrokeWidth(2);
        paint_circle.setStyle(Paint.Style.STROKE);
        paint_circle.setColor(0xffffffff);
        paint_circle.setTextSize(30);

        paint_bezier.setAntiAlias(true);
        paint_bezier.setStrokeWidth(8);
        paint_bezier.setStyle(Paint.Style.STROKE);
        paint_bezier.setColor(0xffffffff);
        paint_bezier.setTextSize(30);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas){
        path_bezier.reset();
        path_shader.reset();
        int w = 1080;
        int h = 600;
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
}
