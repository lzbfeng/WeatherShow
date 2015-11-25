package com.pku.lesshst.weathershow;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by lesshst on 2015/11/7.
 */
public class ProbabilityView extends ViewUpdate {

    public static final int startInvalid = 0;
    public static final int endInvalid = 50;

    public class ProbabilityViewInfo{
        public final String thirdDayName = "11/11";
        public final String forthDayName = "11/12";
        private int[] probabilities;

        public String getThirdDayName() {
            return thirdDayName;
        }

        public String getForthDayName() {
            return forthDayName;
        }

        public int[] getProbabilities() {
            return probabilities;
        }

        public void setProbabilities(int[] probabilities) {
            this.probabilities = probabilities;
        }
    }
    float r_animator = 0f;
    Paint paint_rain_drop_inner = new Paint();
    Paint paint_text_probability = new Paint();
    Paint paint_text_baifenhao = new Paint();
    Paint paint_rain_drop_outer = new Paint();
    Paint paint_text_date = new Paint();
    Path mPath = new Path();

    public ProbabilityView(Context context) {
        super(context);

        initAllPaints();

        initAllAnimators();
    }

    private void initAllPaints(){

        paint_rain_drop_inner.setAntiAlias(true);
        paint_rain_drop_inner.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_rain_drop_inner.setColor(0xffffffff);

        paint_rain_drop_outer.setAntiAlias(true);
        paint_rain_drop_outer.setStrokeWidth(8);
        paint_rain_drop_outer.setStyle(Paint.Style.STROKE);
        paint_rain_drop_outer.setColor(0xffffffff);

        paint_text_probability.setAntiAlias(true);
        paint_text_probability.setStrokeWidth(1);
        paint_text_probability.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_text_probability.setColor(0xffffffff);
        paint_text_probability.setTextSize(40);

        paint_text_baifenhao.setAntiAlias(true);
        paint_text_baifenhao.setStrokeWidth(1);
        paint_text_baifenhao.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_text_baifenhao.setColor(0xcccccccc);
        paint_text_baifenhao.setTextSize(20);

        paint_text_date.setAntiAlias(true);
        paint_text_date.setStrokeWidth(1);
        paint_text_date.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_text_date.setColor(0xcccccccc);
        paint_text_date.setTextSize(40);
    }

    ValueAnimator animators[] = new ValueAnimator[4];
    int durations[] = {1000, 1000, 1000, 1000};
    int startDelays[] = {0, 200, 400, 600};
    
    public void initAllAnimators(){
        for(int i = 0; i < animators.length; i++){
            initAnimator(i, durations[i], startDelays[i]);
        }
    }

    private void initAnimator(int animator_index, int duration, int startDelay){
        animators[animator_index] = ValueAnimator.ofFloat(0f, 1f);
        animators[animator_index].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                ProbabilityView.this.r_animator = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animators[animator_index].setDuration(duration);
        animators[animator_index].setStartDelay(startDelay);
        animators[animator_index].setInterpolator(new DecelerateInterpolator());
    }

    public void startAnimator(){
        initAllAnimators();
        for (ValueAnimator anim : animators) {
            anim.start();
        }
        updateByHand = false;
    }

    public void endAnimator(){
        updateByHand = true;
        invalidate();
    }

    private int probabilities[] = new int[]{78, 38, 50, 29};
    private String dates[] = new String[]{"今天", "明天", "11/10", "11/11"};

    public void setProbabilityInfo(ProbabilityViewInfo info){
        this.probabilities = info.getProbabilities();
        this.dates[2] = info.getThirdDayName();
        this.dates[3] = info.getForthDayName();
    }

    public void setProbabilityInfoAndUpdate(ProbabilityViewInfo info){
        setProbabilityInfo(info);
        this.startAnimator();
    }

    boolean updateByHand = false;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = canvas.getWidth();
        int step = w / 4;
        boolean b = true;
        for(int i = 0; i < 4; i ++){
            if(updateByHand)
                drawNothing();
            else {
                drawRaindrop(canvas, i * step + step / 2, (float) animators[i].getAnimatedValue(), 100, probabilities[i], dates[i]);
            }
        }
    }
    private void drawNothing(){

    }
    private void drawRaindrop(Canvas canvas, float x, float y_ratio, float radius, float probability, String date){

//        Log.e("drawRaindrop", String.valueOf(y_ratio));
        float x_circle = x;
        float y_circle = 300 * y_ratio;

        radius = 50;
        RectF rect = new RectF();
        rect.set(x_circle - radius, y_circle - radius, x_circle + radius, y_circle + radius);
        paint_rain_drop_outer.setAlpha((int)(255 * y_ratio));
        canvas.drawArc(rect, 330, 240, false, paint_rain_drop_outer);

        double angle = 60f / 180 * Math.PI;
        float end_x = x_circle;
        float end_y = (float)(y_circle - radius / Math.cos(angle));

        //绘制左边的直线
        float start_x_left = (float)(x_circle - radius * Math.sin(angle));
        float start_y_left = (float)(y_circle - radius * Math.cos(angle));
        canvas.drawLine(start_x_left, start_y_left, end_x, end_y, paint_rain_drop_outer);
        //绘制右边的直线
        float start_x_right = (float)(x_circle + radius * Math.sin(angle));
        float start_y_right = start_y_left;
        canvas.drawLine(start_x_right, start_y_right, end_x, end_y, paint_rain_drop_outer);

        //绘制雨滴内部的水量
        //probability = 25;
        float scale = probability / 100f;
        float sweep_pro = 360 * scale;
        radius = 40;
        rect.set(x_circle - radius, y_circle - radius, x_circle + radius, y_circle + radius);

        //用于控制雨滴内部水量波动的幅度
        float fluctuation = 40 * (0.5f - Math.abs(0.5f - scale)) * (0.5f - (float)Math.abs(this.r_animator - 1));
        if(sweep_pro < 240) {
            mPath.reset();
            mPath.addArc(rect, 90 - sweep_pro / 2, sweep_pro);
            float angle_pro = sweep_pro / 180 * (float) Math.PI;
            float x_ext = radius * (float) Math.sin(angle_pro / 2);
            float y_ext = radius * (float) Math.cos(angle_pro / 2);
            float startx = x_circle - x_ext;
            float starty = y_circle + y_ext;
            float ctrlx = (x_circle - startx) * 2 / 3 + startx;
            float ctrly = y_circle + y_ext - fluctuation;
            float endx = x_circle;
            float endy = y_circle + y_ext;
            mPath.cubicTo(startx, starty, ctrlx, ctrly, endx, endy);

            startx = endx;
            starty = endy;
            endx = x_circle + x_ext;
            endy = y_circle + y_ext;
            ctrlx = startx + (endx - startx) * 1 / 3;
            ctrly = y_circle + y_ext + fluctuation;
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
            float ctrly = starty - fluctuation;
            float endx = x_circle;
            float endy = starty;
            mPath.cubicTo(startx, starty, ctrlx, ctrly, endx, endy);

            startx = endx;
            starty = endy;
            endx = x_circle + bianchang * (float)Math.cos(jiaodu1);
            endy = starty;
            ctrlx = startx + (endx - startx) * 1 / 3;
            ctrly = starty + fluctuation;
            mPath.cubicTo(startx, starty, ctrlx, ctrly, endx, endy);
        }
        paint_rain_drop_inner.setAlpha((int)(255 * y_ratio));
        canvas.drawPath(mPath, paint_rain_drop_inner);

        //绘制概率文字
        String str_show_pro = String.valueOf((int)probability);
        Rect rectf = new Rect();
        paint_text_probability.getTextBounds(str_show_pro, 0, str_show_pro.length(), rectf);
        float words_width_in_piexl = Math.abs(rectf.right - rectf.left);
        float words_height_in_piexl = Math.abs(rectf.bottom - rectf.top);
        paint_text_probability.setAlpha((int)(255 * y_ratio));
        canvas.drawText(str_show_pro, x_circle - words_width_in_piexl / 2, y_circle + radius + 60, paint_text_probability);

        //绘制百分号
        paint_text_baifenhao.getTextBounds("%", 0, 1, rectf);
        paint_text_baifenhao.setAlpha((int)(255 * y_ratio));
        canvas.drawText("%", x_circle + words_width_in_piexl / 2f, y_circle + radius + 60 - words_height_in_piexl + Math.abs(rectf.bottom - rectf.top) / 2f, paint_text_baifenhao);

        //绘制日期
        paint_text_date.getTextBounds(date, 0, date.length(), rectf);
        paint_text_date.setAlpha((int)(255 * y_ratio));
        canvas.drawText(date, x_circle - Math.abs(rectf.right - rectf.left) / 2f, y_circle - radius / (float) Math.cos(60 / 180f * (float) Math.PI) - 60, paint_text_date);
    }
}
