package com.pku.lesshst.weathershow;

/**
 * Created by lesshst on 2015/11/24.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {

    private ScrollViewListener scrollViewListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if(scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
//            Log.e("onScrollChanged", String.valueOf(x) + "--" + String.valueOf(y) + "--"
//                    + String.valueOf(oldx) + "--" + String.valueOf(oldy) );
        }
    }
}