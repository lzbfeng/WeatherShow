package com.pku.lesshst.weathershow.MyRefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by lzb on 2015/10/12.
 */
public class RotateView extends View {
    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCircleUpdate = new Path();
        mCircleInterruptingUpdate = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
    }

    private Path mCircleUpdate;
    private Path mCircleInterruptingUpdate;
    Paint paint = new Paint();

    private void drawCircle(Canvas canvas){
        alpha -= 30;
        if(alpha <=0)
            alpha = 255;
        mCircleUpdate.rewind();
        mCircleInterruptingUpdate.rewind();
        RectF circleRect = new RectF();
        int width = this.getWidth();
        int height = this.getHeight();

        int w = 30;
        int left = width/2 - w/2;
        int top = height/2 - w/2;

        circleRect.set(left, top, left + w, top + w);
        mCircleUpdate.addOval(circleRect, Path.Direction.CCW);

        float centerX = circleRect.centerX();
        float centerY = circleRect.centerY();
        offest += 10;
        if(offest > 100000)
            offest = 0;
        for(int i = 0; i < 360; i += 45){
            double angle = 2 * Math.PI * (((i + offest) % 360) / 360.0);
            double r = circleRect.width() / 2.0;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double startX = cos * (r + 10) + centerX;
            double startY = sin * (r + 10) + centerY;
            double endX = cos * (r + 20) + centerX;
            double endY = sin * (r + 20) + centerY;
            mCircleInterruptingUpdate.moveTo((float) startX, (float) startY);
            mCircleInterruptingUpdate.lineTo((float) endX, (float)endY);
        }


        paint.setColor(0xffffffff);
//        p.setAlpha(alpha);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawPath(mCircleUpdate, paint);
        canvas.drawPath(mCircleInterruptingUpdate, paint);
    }
    private int alpha = 255;
    private int offest = 0;
}
