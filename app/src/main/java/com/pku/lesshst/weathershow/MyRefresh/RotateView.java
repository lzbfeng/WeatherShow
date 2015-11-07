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
            double startX = Math.cos(angle) * (r + 10) + centerX;
            double startY = Math.sin(angle) * (r + 10) + centerY;
            double endX = Math.cos(angle) * (r + 20) + centerX;
            double endY = Math.sin(angle) * (r + 20) + centerY;
            mCircleInterruptingUpdate.moveTo((float) startX, (float) startY);
            mCircleInterruptingUpdate.lineTo((float) endX, (float)endY);
        }

        Paint p = new Paint();
        p.setColor(0xffffffff);
//        p.setAlpha(alpha);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);
        canvas.drawPath(mCircleUpdate, p);
        canvas.drawPath(mCircleInterruptingUpdate, p);
    }
    private int alpha = 255;
    private int offest = 0;
}
