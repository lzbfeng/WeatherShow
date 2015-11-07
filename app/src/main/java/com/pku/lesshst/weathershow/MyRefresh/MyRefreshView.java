package com.pku.lesshst.weathershow.MyRefresh;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.pku.lesshst.weathershow.R;

/**
 * 可进行下拉刷新的自定义控件。
 *
 * @author guolin
 *
 */
public class MyRefreshView extends LinearLayout implements OnTouchListener {

    public static final int STATUS_PULL_TO_REFRESH = 0;

    public static final int STATUS_RELEASE_TO_REFRESH = 1;

    public static final int STATUS_REFRESHING = 2;

    public static final int STATUS_REFRESH_FINISHED = 3;

    public static final int SCROLL_SPEED = -20;

    public static final long ONE_MINUTE = 60 * 1000;

    public static final long ONE_HOUR = 60 * ONE_MINUTE;

    public static final long ONE_DAY = 24 * ONE_HOUR;

    public static final long ONE_MONTH = 30 * ONE_DAY;

    public static final long ONE_YEAR = 12 * ONE_MONTH;

    private static final String UPDATED_AT = "updated_at";

    private PullToRefreshListener mListener;

    private SharedPreferences preferences;

    private HeaderView header;

    private View secondView;

    private RotateView rotateView;

    private TextView description;

    private TextView updateAt;

    private MarginLayoutParams headerLayoutParams;

    private long lastUpdateTime;

    private int mId = -1;

    private int hideHeaderHeight;

    private int currentStatus = STATUS_REFRESH_FINISHED;

    private int lastStatus = currentStatus;

    private float yDown;

    private int touchSlop;

    private boolean loadOnce;


    public MyRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        header = (HeaderView)(LayoutInflater.from(context).inflate(R.layout.refresh_header, null, true));

        rotateView = (RotateView) header.findViewById(R.id.rotate_view);
        description = (TextView) header.findViewById(R.id.description);
        updateAt = (TextView) header.findViewById(R.id.updated_at);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        refreshUpdatedAtValue();
        setOrientation(VERTICAL);
        addView(header, 0);
    }
    private MarginLayoutParams secondViewParams;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            hideHeaderHeight = -header.getHeight();
            headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
            headerLayoutParams.topMargin = hideHeaderHeight;
            secondView =  getChildAt(1);
            secondView.setOnTouchListener(this);
            secondViewParams = (MarginLayoutParams)secondView.getLayoutParams();
            loadOnce = true;
        }
    }

    private void setIsAbleToPull(MotionEvent event) {
        float scrollY = secondView.getScrollY();
//        if (firstChild != null)
        {

            if (Math.abs(scrollY - 0.0) < 0.3) {
                if (!ableToPull) {
                    yDown = event.getRawY();
                }
                // 如果首个元素的上边缘，距离父布局值为0，就说明ListView滚动到了最顶部，此时应该允许下拉刷新
                ableToPull = true;
            } else {
                if (headerLayoutParams.topMargin != hideHeaderHeight) {
                    headerLayoutParams.topMargin = hideHeaderHeight;
                    header.setLayoutParams(headerLayoutParams);
                }
                ableToPull = false;
            }
        }
//       else {
//            // 如果ListView中没有元素，也应该允许下拉刷新
//            ableToPull = true;
//        }
    }
    private boolean ableToPull;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setIsAbleToPull(event);
        if(!ableToPull){
            return false;
        }
        int top = secondView.getTop();
        int topMargin = secondViewParams.topMargin;
        int firstVisiblePos = secondView.getPaddingTop();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                yDown = event.getRawY();

                top = top;
                break;
            case MotionEvent.ACTION_MOVE:
                float yMove = event.getRawY();
                int dist = (int) (yMove - yDown);

                if (dist <= 0 && headerLayoutParams.topMargin <= hideHeaderHeight) {
                    return false;
                }
                if (dist < touchSlop) {
                    return false;
                }
                if (currentStatus != STATUS_REFRESHING) {
                    if (headerLayoutParams.topMargin > 0) {
                        currentStatus = STATUS_RELEASE_TO_REFRESH;
                    } else {
                        currentStatus = STATUS_PULL_TO_REFRESH;
                    }
                    headerLayoutParams.topMargin = (dist / 2) + hideHeaderHeight;
                    header.setLayoutParams(headerLayoutParams);
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
                if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                    new RefreshingTask().execute();
                } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                    new HideHeaderTask().execute();
                }
                break;
        }
        //更新HeadView
        if (currentStatus == STATUS_PULL_TO_REFRESH
                || currentStatus == STATUS_RELEASE_TO_REFRESH) {
            updateHeaderView();

            secondView.setPressed(false);
            secondView.setFocusable(false);
            secondView.setFocusableInTouchMode(false);
            lastStatus = currentStatus;

            return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setOnRefreshListener(PullToRefreshListener listener, int id) {
        mListener = listener;
        mId = id;
    }

    public void finishRefreshing() {
        currentStatus = STATUS_REFRESH_FINISHED;
        preferences.edit().putLong(UPDATED_AT + mId, System.currentTimeMillis()).commit();
        new HideHeaderTask().execute();
    }

    private void updateHeaderView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL_TO_REFRESH) {
                description.setText(getResources().getString(R.string.pull_to_refresh));
                rotateView.setVisibility(View.VISIBLE);
            } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                description.setText(getResources().getString(R.string.release_to_refresh));
                rotateView.setVisibility(View.VISIBLE);
            } else if (currentStatus == STATUS_REFRESHING) {
                description.setText(getResources().getString(R.string.refreshing));
                rotateView.setVisibility(View.VISIBLE);
            }
            refreshUpdatedAtValue();
        }
    }


    private void refreshUpdatedAtValue() {
        lastUpdateTime = preferences.getLong(UPDATED_AT + mId, -1);
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastUpdateTime;
        long timeIntoFormat;
        String updateAtValue;
        if (lastUpdateTime == -1) {
            updateAtValue = getResources().getString(R.string.not_updated_yet);
        } else if (timePassed < 0) {
            updateAtValue = getResources().getString(R.string.time_error);
        } else if (timePassed < ONE_MINUTE) {
            updateAtValue = getResources().getString(R.string.updated_just_now);
        } else if (timePassed < ONE_HOUR) {
            timeIntoFormat = timePassed / ONE_MINUTE;
            String value = timeIntoFormat + "分钟";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_HOUR;
            String value = timeIntoFormat + "小时";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_MONTH) {
            timeIntoFormat = timePassed / ONE_DAY;
            String value = timeIntoFormat + "天";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_YEAR) {
            timeIntoFormat = timePassed / ONE_MONTH;
            String value = timeIntoFormat + "个月";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else {
            timeIntoFormat = timePassed / ONE_YEAR;
            String value = timeIntoFormat + "年";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        }
        updateAt.setText(updateAtValue);
    }

    public void RefreshByHand(){
        headerLayoutParams.topMargin = 20;
        header.setLayoutParams(headerLayoutParams);
        RefreshingTask tash = new RefreshingTask();
        tash.execute();

    }
    class RefreshingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= 0) {
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            if (mListener != null) {
                mListener.onRefresh();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            updateHeaderView();
            headerLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerLayoutParams);
        }

    }

    class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= hideHeaderHeight) {
                    topMargin = hideHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            headerLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            headerLayoutParams.topMargin = topMargin;
            header.setLayoutParams(headerLayoutParams);
            currentStatus = STATUS_REFRESH_FINISHED;
        }
    }

    public interface PullToRefreshListener {

        void onRefresh();

    }

}
