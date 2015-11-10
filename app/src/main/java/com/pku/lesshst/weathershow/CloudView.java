package com.pku.lesshst.weathershow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by lesshst on 2015/11/9.
 */
public class CloudView extends View{
    public final static int SUNDAY = 0;
    public final static int CLOUDY = 1; //多云
    public final static int OVERCAST = 2;   //阴天
    public final static int LIGHTRAIN = 3;
    public final static int MODERATERAIN = 4;
    public final static int HEAVYRAIN = 5;

    private int offest = 0;
    private int mode = SUNDAY;

    public CloudView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint.setColor(0xffffffff);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
//        setAnimator();
    }

    public void setMode(int mode){
        this.mode = mode;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCloud(canvas);
    }

    public void drawCloud(Canvas canvas){
        switch(this.mode){
            case SUNDAY:
                try {
                    drawSunday(canvas);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case CLOUDY:
                try {
                    drawCloudy(canvas);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case OVERCAST:
                drawOvercast(canvas);
                break;
            case LIGHTRAIN:
                drawLightRain(canvas);
                break;
            case MODERATERAIN:
                drawModerateRain(canvas);
                break;
            case HEAVYRAIN:
                drawHevayRain(canvas);
                break;
        }
    }

    private void drawHevayRain(Canvas canvas) {

    }

    private void drawModerateRain(Canvas canvas) {
        
    }

    private void drawLightRain(Canvas canvas) {

    }

    private void drawOvercast(Canvas canvas) {

    }

    private void drawCloudy(Canvas canvas) throws Exception{
        float w = canvas.getWidth();
        float h = canvas.getHeight();
        if(w != h){
            throw new Exception("Width and Height are not same!");
        }
        RectF rect = new RectF();
        float x_arc1 = w / 2f;
        float y_arc1 = h * 1f / 3f + 20;
        float radius1 = 30;
        rect.set(x_arc1 - radius1, y_arc1 - radius1, x_arc1 + radius1, y_arc1 + radius1);
        canvas.drawArc(rect, 0, 360, false, paint);

        float x_arc2 = w * 2f / 5f;
        float y_arc2 = h * 2f / 3f;
        float radius2 = 25;
        rect.set(x_arc2 - radius2, y_arc2 - radius2, x_arc2 + radius2, y_arc2 + radius2);
        canvas.drawArc(rect, 0, 360, false, paint);

        float x_arc3 = w * 3f / 5f;
        float y_arc3 = h * 2f / 3f;
        float radius3 = 25;
        rect.set(x_arc3 - radius3, y_arc3 - radius3, x_arc3 + radius3, y_arc3 + radius3);
        canvas.drawArc(rect, 0, 360, false, paint);

    }

    private Path mCircleInterrupting = new Path();;
    Paint paint = new Paint();

    private void drawSunday(Canvas canvas) throws Exception{

        float w = canvas.getWidth();
        float h = canvas.getHeight();
        if(w != h){
            throw new Exception("Width and Height are not same!");
        }
        float radius_diff = 50;
        float radius = w / 2 - radius_diff;
        float x_circle = w / 2;
        float y_circle = h / 2;
        canvas.drawCircle(w / 2, h / 2, radius, paint);

        offest += 10;
        if(offest > 100000)
            offest = 0;
        mCircleInterrupting.reset();
        for(int i = 0; i < 360; i += 45){
            double angle = 2 * Math.PI * (((i + offest) % 360) / 360.0);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double startX = cos * (radius + 10) + x_circle;
            double startY = sin * (radius + 10) + y_circle;
            double endX = cos * (radius + 20) + x_circle;
            double endY = sin * (radius + 20) + y_circle;
            mCircleInterrupting.moveTo((float) startX, (float) startY);
            mCircleInterrupting.lineTo((float) endX, (float) endY);
        }
        canvas.drawPath(mCircleInterrupting, paint);
    }
}
