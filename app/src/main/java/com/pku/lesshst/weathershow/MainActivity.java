package com.pku.lesshst.weathershow;
//update this file at time: 20151125.15.28
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.pku.lesshst.weathershow.MyRefresh.MyRefreshView;
import com.pku.lesshst.weathershow.MyRefresh.RotateView;
import com.pku.lesshst.weathershow.bean.City;
import com.pku.lesshst.weathershow.bean.CityDB;
import com.pku.lesshst.weathershow.utils.CN2Pinyin;
import com.pku.lesshst.weathershow.utils.GetLocation;
import com.pku.lesshst.weathershow.utils.GetWeather;
import com.pku.lesshst.weathershow.utils.GetWeatherInfo;
import com.pku.lesshst.weathershow.utils.ParseJson;
import com.pku.lesshst.weathershow.utils.StringProcessing;
import com.pku.lesshst.weathershow.utils.TodayWeather;
import com.pku.lesshst.weathershow.utils.WeatherInfo;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements ScrollViewListener{
    public class InfoToShow {
        public WeatherInfo weatherInfo;
        public String locationName;
    }

    MyRefreshView refreshableView;
    RotateView rotateView;
    protected ImageView updateBtn;
    protected Button updateOneShengBtn;
    private InfoToShow infoToShow;
    private static final int GUIUPDATEIDENTIFIER = 1000;
    private static boolean UPDATE_CIRCLE_ANBLE = false;
    private Handler handler = new Handler(){

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MainActivity.GUIUPDATEIDENTIFIER:
                    Bundle b = msg.getData();
                    updateControls();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private ObservableScrollView scrollView = null;
    //PagerView
    private ViewPager viewPager;//viewpager
    private PagerTitleStrip pagerTitleStrip;//viewpager的标题
    ArrayList<View> viewList;
    ArrayList<String> citiesList;

    ArrayList<TemperatureView> tem_views = new ArrayList<TemperatureView>();
    ArrayList<LineView> plot_views = new ArrayList<LineView>();
    ArrayList<GridView> grid_views = new ArrayList<GridView>();
    ArrayList<PMView> pm_views = new ArrayList<PMView>();
    ArrayList<ProbabilityView> pro_views = new ArrayList<ProbabilityView>();
    ArrayList<SunRaiseDownView> sun_views = new ArrayList<SunRaiseDownView>();

    int currentIndex = 0;
    int conntOfCities = 6;
    PagerAdapter pagerAdapter;

    //晴，多云，小雨，大雨，中雨，阵雨，阴
    HashMap<String, Integer> type_imgs = new HashMap<String, Integer>();

    int[] img_day_temp_ids = new int[]{R.id.first_day_temp_show, R.id.second_day_temp_show, R.id.third_day_temp_show,
            R.id.forth_day_temp_show, R.id.fifth_day_temp_show};

    int[] img_day_cloud_ids = new int[]{R.id.first_day_cloud_show, R.id.second_day_cloud_show, R.id.third_day_cloud_show,
            R.id.forth_day_cloud_show, R.id.fifth_day_cloud_show};

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        LayoutInflater lf = getLayoutInflater().from(this);
        String [] citiesNames = new String[]{"大兴", "北京", "上海", "广州", "深圳", "杭州"};
        conntOfCities = citiesNames.length;
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组
        citiesList = new ArrayList<String>();// 每个页面的Title数据
        View view;
        for(int i = 0; i < citiesNames.length; i++){
            view = lf.inflate(R.layout.activity_refresh, null);
            viewList.add(view);
            citiesList.add(citiesNames[i]);
        }

        pagerAdapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(viewList.get(position));
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return citiesList.get(position);//直接用适配器来完成标题的显示，所以从上面可以看到，我们没有使用PagerTitleStrip。当然你可以使用。
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.removeView(viewList.get(position));
                container.addView(viewList.get(position));
                return viewList.get(position);
            }

        };
        viewPager.setAdapter(pagerAdapter);
        currentIndex = 0;
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        for(int i = 0; i < citiesList.size(); i++){
            currentIndex = i;
            initOneLayout();
        }
        currentIndex = 0;
    }
    private void insertOnePage(String cityName, int index){
        LayoutInflater lf = getLayoutInflater().from(this);
        View view = lf.inflate(R.layout.activity_refresh, null);
        viewList.add(index, view);
        pagerAdapter.notifyDataSetChanged();
        citiesList.add(index, cityName);
        conntOfCities += 1;
        initOneLayout();
    }

    private void initOneLayout(){
        //tem_view
        LinearLayout tem_layout = (LinearLayout) viewList.get(currentIndex).findViewById(R.id.tem_layout);
        tem_views.add(currentIndex, new TemperatureView(this));
        tem_layout.addView(tem_views.get(currentIndex), 1080, 900);

        //plot_view
        LinearLayout plot_layout = (LinearLayout) viewList.get(currentIndex).findViewById(R.id.line_layout);
        plot_views.add(currentIndex, new LineView(this));
        plot_layout.addView(plot_views.get(currentIndex), 1080, 600);

        Date dNow = new Date();
        long timeCount = (dNow.getTime() / 1000) + 60 * 60 * 24 * 2;
        dNow.setTime(timeCount * 1000);
        SimpleDateFormat ft = new SimpleDateFormat ("MM/dd");
        String forthDayName = ft.format(dNow);
        ((TextView)viewList.get(currentIndex).findViewById(R.id.forth_day_date_show)).setText(forthDayName);

        timeCount = (dNow.getTime() / 1000) + 60 * 60 * 24;
        dNow.setTime(timeCount * 1000);
        String fifthDayName = ft.format(dNow);
        ((TextView)viewList.get(currentIndex).findViewById(R.id.fifth_day_date_show)).setText(fifthDayName);

        //grid_view
        LinearLayout grid_view_layout = (LinearLayout) viewList.get(currentIndex).findViewById(R.id.grid_view_layout);
        grid_views.add(currentIndex, new GridView(this));
        grid_view_layout.addView(grid_views.get(currentIndex), 1080, 600);

        //pm_view
        LinearLayout pm_view_layout = (LinearLayout) viewList.get(currentIndex).findViewById(R.id.pm_view_layout);
        pm_views.add(currentIndex, new PMView(this));
        pm_view_layout.addView(pm_views.get(currentIndex), 1080, 700);

        //pro_view
        LinearLayout pro_view_layout = (LinearLayout) viewList.get(currentIndex).findViewById(R.id.probability_view_layout);
        pro_views.add(currentIndex, new ProbabilityView(this));
        pro_view_layout.addView(pro_views.get(currentIndex), 1080, 600);

        //sun_view
        LinearLayout sun_view_layout = (LinearLayout) viewList.get(currentIndex).findViewById(R.id.sun_raise_donw_view_layout);
        sun_views.add(currentIndex, new SunRaiseDownView(this));
        sun_view_layout.addView(sun_views.get(currentIndex), 1080, 600);

        //init all views and update data
        int temp = 10;
        String weather = "阴天";
        String air_quality = "空气良好";
        tem_views.get(currentIndex).setTemp(temp);
        tem_views.get(currentIndex).setWeather(weather);
        tem_views.get(currentIndex).setAir_quality(air_quality);

        int[] temps_day = new int[]{23, 34, 12, 34, 21};
        int[] temps_night = new int[]{12, 14, 10, 8, 11};
        plot_views.get(currentIndex).setTemps_day(temps_day);
        plot_views.get(currentIndex).setTemps_night(temps_night);
        plot_views.get(currentIndex).startAnimator();

        GridView.GridViewInfo gridViewInfo = new GridView.GridViewInfo();
        gridViewInfo.setHumidityValue("75");
        gridViewInfo.setVisibilityValue("6.4");
        gridViewInfo.setWindDirection("南风");
        gridViewInfo.setWindDirectionValue("三级");
        gridViewInfo.setUVRaysValue("最强");
        gridViewInfo.setPressureValue("1027.3");
        gridViewInfo.setBodyFeelingValue("4.5");
        grid_views.get(currentIndex).setGridViewInfo(gridViewInfo);
        grid_views.get(currentIndex).startAnimator();

        PMView.PMViewInfo pminfo = new PMView.PMViewInfo();
        pminfo.setAQI(70);
        pminfo.setPM2_5Value(54);
        pminfo.setDate("11月25日");
        pminfo.setTime("20:00");
        pm_views.get(currentIndex).setPMViewInfo(pminfo);
        pm_views.get(currentIndex).startAnimator();

        int probabilities[] = new int[]{12, 45, 76, 29};
        ProbabilityView.ProbabilityViewInfo probabilityViewInfo = new ProbabilityView.ProbabilityViewInfo();
        probabilityViewInfo.setProbabilities(probabilities);
        pro_views.get(currentIndex).setProbabilityInfo(probabilityViewInfo);
        pro_views.get(currentIndex).startAnimator();
    }

    boolean scrool_direction_down = true;

    int plot_view_down_start = -40;
    int plot_view_down_end = 1630;
    int plot_view_up_start = 1630;
    int plot_view_up_end = -40;

    int grid_view_down_start = 106;
    int grid_view_down_end = 2672;
    int grid_view_up_start = 2672;
    int grid_view_up_end = 106;

    int pm_view_down_start = 1300;
    int pm_view_down_end = 3200;
    int pm_view_up_start = 2600;
    int pm_view_up_end = 677;

    int pro_view_down_start = 2459;
    int pro_view_down_end = 4175;
    int pro_view_up_start = 4175;
    int pro_view_up_end = 1759;
//
    int sun_view_down_start = 2558;
    int sun_view_down_end = 5055;
    int sun_view_up_start = 5055;
    int sun_view_up_end = 2558;

    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if(y - oldy > 0)
            scrool_direction_down = true;
        else
            scrool_direction_down = false;

        updateView(plot_views.get(currentIndex), y, plot_view_down_start,
                plot_view_down_end, plot_view_up_start, plot_view_up_end);
        updateView(grid_views.get(currentIndex), y, grid_view_down_start,
                grid_view_down_end, grid_view_up_start, grid_view_up_end);
        updateView(pm_views.get(currentIndex), y, pm_view_down_start,
                pm_view_down_end, pm_view_up_start, pm_view_up_end);
        updateView(pro_views.get(currentIndex), y, pro_view_down_start,
                pro_view_down_end, pro_view_up_start, pro_view_up_end);
        updateView(sun_views.get(currentIndex), y, sun_view_down_start,
                sun_view_down_end, sun_view_up_start, sun_view_up_end);
    }
    private void updateView(ViewUpdate view, int y,
                                int down_start, int down_end, int up_start, int up_end){
        if(scrool_direction_down == true) {
            if (y > down_start && y < down_end && !view.isUpdate) {
                view.startAnimator();
                view.isUpdate = true;
            }else if( y > down_end){
                view.endAnimator();
                view.isUpdate = false;
            }
        }
        else {
            if(y < up_start && y > up_end && !view.isUpdate){
                view.startAnimator();
                view.isUpdate = true;
            }else if(y < up_end){
                view.endAnimator();
                view.isUpdate = false;
            }
        }
        Log.e("onScrollChanged", String.valueOf(y) + "--" + String.valueOf(view.isUpdate) + "--" + String.valueOf(scrool_direction_down) );
    }


    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
            String city_name;
            //Log.e("onPageScrolled:", "0--" + arg0 + "--1--" + arg1 + "--2--" + arg2);
            double diff = arg1 - 0.5;
            float alpha;
            if(diff > 0){
                city_name = citiesList.get(arg0 + 1);
                alpha = (float)(Math.abs(diff) / 0.5);
            }else{
                city_name = citiesList.get(arg0);
                alpha = (float)(Math.abs(diff) / 0.5);
            }
            setCurrentTitle_City(city_name, alpha);
        }

        public void onPageSelected(int arg0) {
            currentIndex = arg0;
            setCurrentView();
        }
    }

    private LineView lineView;
    private void setCurrentView(){

    }

    private void setCurrentTitle_City(String city_name, float alpha){
        TextView city_name_textview = (TextView)findViewById(R.id.city_name);
        city_name_textview.setText(city_name);
        city_name_textview.setAlpha(alpha);
    }
    public class BroadcastMain extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String jsonString = intent.getExtras().getString( "update" );
            Log.e("handler_auto_update", jsonString);
            Message msg = handler_auto_update.obtainMessage();
            msg.what = 01;
            handler_auto_update.sendMessage( msg );
        }
    }
    boolean updateAuto = false;
    Handler handler_auto_update = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 01:
                    updateAuto = true;
                    refreshableView.RefreshByHand();
                    break;
                default:
                    break;
            }

        };

    };
    private void notifyInToolBar(){
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(MainActivity.this);
        builder.setTicker("天气信息已经更新！");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        //        设置自定义RemoteView
        RemoteViews view=new RemoteViews(getPackageName(),R.layout.remote_view);

        builder.setContent(view);
        PendingIntent pi=PendingIntent.getActivity(MainActivity.this,1,new Intent(MainActivity.this,MainActivity.class),0);
        builder.setContentIntent(pi);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000});
        builder.setLights(Color.RED, 0, 1);
        builder.setOngoing(true);
        manager.notify(2, builder.build());
    }
    public void simpleNotification(){
        //        获取NotificationManager实例
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //        构造Notification.Builder 对象
        NotificationCompat.Builder builder=new NotificationCompat.Builder(MainActivity.this);

        //        设置Notification图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //builder.setLargeIcon(myIcon);
        //        设置Notification tickertext
        builder.setTicker("A new Message");
        //        设置通知的题目
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
        String time = ft.format(dNow);
        builder.setContentTitle(this.todayWeather.getCity() + "\t" + "更新时间为" + time);
        //        设置通知的内容
        builder.setContentText("今天温度为：" + this.todayWeather.getWendu() + "℃");
        builder.setContentInfo("Info");
        //        设置通知可以被自动取消
        builder.setAutoCancel(true);
        //        设置通知栏显示的Notification按时间排序
        builder.setWhen(System.currentTimeMillis());
        //        设置其他物理属性，包括通知提示音、震动、屏幕下方LED灯闪烁
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));//这里设置一个本地文件为提示音
//        builder.setVibrate(new long[]{1000,1000,1000,1000});
        builder.setLights(Color.BLUE,0,1);
        //        设置该通知点击后将要启动的Intent,这里需要注意PendingIntent的用法,构造方法中的四个参数(context,int requestCode,Intent,int flags);
        Intent intent=new Intent(MainActivity.this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(MainActivity.this,0,intent,0);
        builder.setContentIntent(pi);

        //        实例化Notification

        Notification notification=builder.build();//notify(int id,notification对象);id用来标示每个notification
        manager.notify(1,notification);


    }
    BroadcastMain broadcastMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.myAsset = getAssets();
        setContentView(R.layout.activity_main);
        scrollView = (ObservableScrollView) findViewById(R.id.scrollview);
        scrollView.setScrollViewListener(this);
        initBaseInfo();
        initViewPager();

        Intent intent = new Intent();
        intent.setClass(this, MyService.class);
        startService(intent);

        broadcastMain = new BroadcastMain();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyService.BROADCASTACTION);
        registerReceiver(broadcastMain, filter);
    }
    public static CityDB cityDB;
    private void initBaseInfo(){
        infoToShow = new InfoToShow();
//        getLocation();
        cityDB = openCityDB();

        refreshableView = (MyRefreshView) findViewById(R.id.refreshable_view);
        rotateView = (RotateView) findViewById(R.id.rotate_view);
        setOnFreshListener();

        setTypeImgs();
    }
    private void setTypeImgs(){
        type_imgs.put("晴", R.drawable.simple_weather_icon_01);
        type_imgs.put("多云", R.drawable.simple_weather_icon_06);
        type_imgs.put("小雨", R.drawable.simple_weather_icon_28);
        type_imgs.put("中雨", R.drawable.simple_weather_icon_22);
        type_imgs.put("大雨", R.drawable.simple_weather_icon_23);
        type_imgs.put("阵雨", R.drawable.simple_weather_icon_27);
        type_imgs.put("阴", R.drawable.simple_weather_icon_04);
        type_imgs.put("无", R.drawable.simple_weather_icon_05);
    }

    private void setOnFreshListener(){
        refreshableView.setOnRefreshListener(new MyRefreshView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    MainActivity.UPDATE_CIRCLE_ANBLE = true;
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            int count = 0;
                            boolean isBreak = false;
                            while (MainActivity.UPDATE_CIRCLE_ANBLE) {
                                if (count++ > 150) {
                                    isBreak = true;
                                    break;
                                }
                                rotateView.postInvalidate();
                                try {
                                    Thread.sleep(20);
                                } catch (Exception e) {
                                }
                            }
                            if (!isBreak) {
                                count = 0;
                                while (true) {
                                    if (count++ > 30) {
                                        break;
                                    }
                                    rotateView.postInvalidate();
                                    try {
                                        Thread.sleep(20);
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    });
                    thread.start();
                    updateLocationAndWeather();
                    refreshableView.finishRefreshing();
                    MainActivity.UPDATE_CIRCLE_ANBLE = false;
                    thread.join();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0);
    }

    private void updateLocationAndWeather(){
        Log.d("clickUpdate()", "update the weather!");
        try{
            Thread thread = new Thread(runnable);
            thread.start();
            thread.join();

            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            String t = format.format(new Date());
            Log.e("msg", t);
        }
        catch(Exception e){
            Log.d(e.toString(), "");
        }
    }
    private void updateControls() {

    }

    private String getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String today = null;
        if (day == 2) {
            today = "星期一";
        } else if (day == 3) {
            today = "星期二";
        } else if (day == 4) {
            today = "星期三";
        } else if (day == 5) {
            today = "星期四";
        } else if (day == 6) {
            today = "星期五";
        } else if (day == 7) {
            today = "星期六";
        } else if (day == 1) {
            today = "星期日";
        }

        return today;
    }
    public void OnClickOtherActivity(View view){
        try {
            Intent intent = new Intent(this, CitiesListActivity.class);
            Bundle bundle = new Bundle();
            ArrayList<String> cities = getAllCities();

            bundle.putStringArrayList("cities", cities);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Lesshst: ", e.toString());
        }
    }

    public void OnClickOtherActivity1(View view){
        try {
            getLocation();
            int i = 0;
            for(i = 0; i < citiesList.size(); i++){
                if(locationCityName.equals(citiesList.get(i)))
                    break;
            }
            viewPager.setCurrentItem(i, true);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Lesshst: ", e.toString());
        }
    }
    private ArrayList<String> getAllProvinces(){
        ArrayList<String> provinces = new ArrayList<String>();
        ArrayList<City> cities = cityDB.getAllCity();
        for(City city : cities){
            String provinceName = city.getProvince();
            if(!provinces.contains(provinceName))
                provinces.add(provinceName);
        }
        return provinces;
    }

    private ArrayList<String> getAllCities(){
        ArrayList<City> cities = cityDB.getAllCity();
        ArrayList<String> ret = new ArrayList<String>();
        for(City city : cities){
            ret.add(city.getCity());
        }
        return ret;
    }

    private String _quname = null;
    Runnable runGetLocationInfo = new Runnable(){
        @Override
        public void run(){
            try{
                Location location = mLocation;
                GetLocation loc = new GetLocation();
                _quname = loc.GetLocationFromLatLng(location.getLatitude(), location.getLongitude());
                infoToShow.locationName = StringProcessing.FilterStr(_quname);
                mAddressCityName = CN2Pinyin.converterToSpell(infoToShow.locationName);
                getWeatherInfo();
            }
            catch (Exception e) {
                return;
            }
        }
    };

    private String _weatherInfoStr;
    Runnable runnable = new Runnable(){
        @Override
        public void run(){
            getWeatherInfoFromXML();
        }
    };

    private void getWeatherInfo(){
        String weatherJson = GetWeather.getWeather(mAddressCityName);
        String latLongString = "";
        try{
            ParseJson js = new ParseJson(weatherJson);
            WeatherInfo weatherInfo = js.weatherInfo;
            infoToShow.weatherInfo = weatherInfo;
            for(WeatherInfo.DailyForcast day : weatherInfo.dailyWeatherList){
                System.out.println(day.date);
                Log.d(day.date, "date from baidu");
            }

            latLongString += weatherInfo.dailyWeatherList.get(0).toString();
            for(WeatherInfo.HourlyForcast hour : weatherInfo.hourlyWeatherList){
                System.out.println(hour.date);
                Log.d(hour.date, "date from baidu");
            }
            latLongString += weatherInfo.hourlyWeatherList.get(0).toString();

            Log.d("tep is: " + weatherInfo.nowWeather.tmp, "date from baidu");
            latLongString += weatherInfo.nowWeather.toString();
        }
        catch (JSONException e) {
            return;
        }
        Log.d(latLongString, "\ndate from baidu");
        _weatherInfoStr = latLongString;
    }

    private Handler mainHandler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case GetWeatherInfo.UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
//                case GetWeatherInfo.UPDATE_PM_VALUE:
//                    updateAdminAreaPMValue((TodayWeather)msg.obj);
//                    break;】}
                default:
                    break;
            }
        }
    };

    private void getWeatherInfoFromXML(){
        mAddressCityNameHan = citiesList.get(currentIndex);
        try {
            GetWeatherInfo getweatherInfo = new GetWeatherInfo();
            String cityCode = cityDB.getCity(mAddressCityNameHan).getCityNumber();
            getweatherInfo.queryWeatherCode(cityCode, mainHandler);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Lesshst, error: ", e.toString());
        }
    }

    private void updateAdminAreaPMValue(TodayWeather todayWeather){

        String show = "";
        //pm2.5
        TextView pm25_value = (TextView) findViewById(R.id.pm2_5_value);
        show = todayWeather.getPm25();
        pm25_value.setText("" + show);

        TextView pm25_desc = (TextView) findViewById(R.id.pm2_5_desc);
        show = todayWeather.getQuality();
        pm25_desc.setText("" + show);
    }
    private TodayWeather todayWeather;
    private void updateTodayWeather(TodayWeather todayWeather){
        this.todayWeather = todayWeather;
        if(updateAuto){
            simpleNotification();
            updateAuto = false;
        }
        View currentView = viewList.get(currentIndex);
        tem_views.get(currentIndex).setTemp(Integer.parseInt(todayWeather.getWendu()));
        tem_views.get(currentIndex).setWeather(todayWeather.getForcastWeathers()[1].getType());
        tem_views.get(currentIndex).setAir_quality(todayWeather.getQuality());
        tem_views.get(currentIndex).invalidate();

        int[] temps_day = todayWeather.getTempsHighDay();
        int[] temps_night = todayWeather.getTempsLowDay();
        plot_views.get(currentIndex).setTemps_day(temps_day);
        plot_views.get(currentIndex).setTemps_night(temps_night);
        plot_views.get(currentIndex).startAnimator();


        for(int index = 0; index < temps_night.length; index++){
            ((TextView)currentView.findViewById(img_day_temp_ids[index])).setText(String.valueOf(temps_day[index]) + "°/" + temps_night[index] + "°");
        }

        String[] types = todayWeather.getForcastDayTypes();

        for(int index = 0; index < types.length; index++) {
            ImageView imgView = ((ImageView) currentView.findViewById(img_day_cloud_ids[index]));
            if (type_imgs.containsKey(types[index]))
                imgView.setImageResource(type_imgs.get(types[index]));
            else
                imgView.setImageResource(type_imgs.get("无"));
        }

        grid_views.get(currentIndex).setGridViewInfo(todayWeather.getGridViewInfo());
        grid_views.get(currentIndex).startAnimator();

        pm_views.get(currentIndex).setPMViewInfo(todayWeather.getPMViewInfo());
        pm_views.get(currentIndex).startAnimator();

        pro_views.get(currentIndex).setProbabilityInfo(todayWeather.getProbabilityViewInfo());
        pro_views.get(currentIndex).startAnimator();

        sun_views.get(currentIndex).setSunRaiseDownViewInfo(todayWeather.getSunRaiseDownViewInfo());
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private String mAddressCityName = "";
    private String mAddressCityNameHan = "";
    private String mAdminArea = "";

    private Location mLocation;
    private String locationCityName = "";
    private void updateWithNewLocation(Location location) {

        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(lat, lng, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String addressName = address.getSubLocality();
                    String firstCityName = address.getAdminArea();
                    infoToShow.locationName = StringProcessing.FilterStr(addressName);
                    mAddressCityNameHan = infoToShow.locationName;
                    locationCityName = infoToShow.locationName;
//                    mAddressCityName = CN2Pinyin.converterToSpell(infoToShow.locationName);
                    mAdminArea = StringProcessing.FilterStr(firstCityName);
                    sb.append(address.getLocality()).append("/").append(address.getSubLocality()).append("\n");
                }
            }
            catch (Exception e)
            {
                Log.d(e.toString(), "error!");
            }
        }
    }

    private void getLocation()
    {
        try {
            LocationManager locationManager;
            String serviceName = Context.LOCATION_SERVICE;
            locationManager = (LocationManager) getSystemService(serviceName);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = LocationManager.NETWORK_PROVIDER;
            locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
            Location location = locationManager.getLastKnownLocation(provider);
            while (location == null) {
                location = locationManager.getLastKnownLocation(provider);
            }
            updateWithNewLocation(location);
            //getLocationInfo(location);
            locationManager.removeUpdates(locationListener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1){
            String result_value = data.getStringExtra("city");
            Log.d("Lesshst: ret value, city:", result_value);


            int i = 0;
            for(i = 0; i < conntOfCities; i++){
                if(result_value.equals(citiesList.get(i)))
                    break;
            }
            if(i == conntOfCities){
                currentIndex = 0;
                insertOnePage(result_value, currentIndex);
            }
            changeCity(result_value);
        }
    }

    public void changeCity(String cityName){
        mAddressCityNameHan = cityName;
        setCurrentTitle_City(mAddressCityNameHan, 1.0f);
        mAdminArea = cityDB.getCityProvince(mAddressCityNameHan).getProvince();
        try {
            refreshableView.RefreshByHand();
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Lesshst, error: ", e.toString());
        }
    }

    public CityDB openCityDB(){
        String dbDicPath = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases";

        String path = dbDicPath + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        File dbDic = new File(dbDicPath);
        if(db.exists())
            db.delete();
        Log.d("Lesshst, cityDBPath", path);
        if(!db.exists()){
            Log.i("", "db is not exists");
            try{
                if(!dbDic.exists()){
                    dbDic.mkdirs();
                }
                if(!db.exists())
                    db.createNewFile();

                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while((len = is.read(buffer)) != -1){
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            }
            catch (IOException e){
                e.printStackTrace();
                Log.d("Lesshst, error:", e.toString());
                System.exit(0);
            }
        }
        cityDB = new CityDB(this, path);
        Log.d("Lesshst: cityNumber", cityDB.getCity("石景山").getCityNumber());
        return cityDB;
    }
}