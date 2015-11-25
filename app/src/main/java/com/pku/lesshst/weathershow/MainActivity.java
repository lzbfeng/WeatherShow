package com.pku.lesshst.weathershow;
//update this file at time: 20151125.15.28
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    int currentIndex = 0;
    int conntOfCities = 5;
    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        LayoutInflater lf = getLayoutInflater().from(this);
        String [] citiesNames = new String[]{"北京", "上海", "广州", "深圳", "杭州"};
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组
        citiesList = new ArrayList<String>();// 每个页面的Title数据
        View view;
        for(int i = 0; i < citiesNames.length; i++){
            view = lf.inflate(R.layout.activity_refresh, null);
            viewList.add(view);
            citiesList.add(citiesNames[i]);
        }

        PagerAdapter pagerAdapter = new PagerAdapter() {
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
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        currentIndex = 0;
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        for(int i = 0; i < citiesList.size(); i++){
            currentIndex = i;
            initAllLayouts();
        }
        currentIndex = 0;
    }

    private void initAllLayouts(){
        LinearLayout scroll_layout = (LinearLayout) viewList.get(currentIndex).findViewById(R.id.line_layout);
        plot_views[currentIndex] = new LineView(this);
        scroll_layout.addView(plot_views[currentIndex], 1080, 400);
        plot_views[currentIndex].startAnimator();

        LinearLayout grid_view_layout = (LinearLayout) viewList.get(currentIndex).findViewById(R.id.grid_view_layout);
        grid_views[currentIndex] = new GridView(this);
        grid_view_layout.addView(grid_views[currentIndex], 1080, 600);

        LinearLayout pm_view_layout = (LinearLayout) viewList.get(currentIndex).findViewById(R.id.pm_view_layout);
        pm_views[currentIndex] = new PMView(this);
        pm_view_layout.addView(pm_views[currentIndex], 1080, 700);

        LinearLayout pro_view_layout = (LinearLayout) viewList.get(currentIndex).findViewById(R.id.probability_view_layout);
        pro_views[currentIndex] = new ProbabilityView(this);
        pro_view_layout.addView(pro_views[currentIndex], 1080, 600);

        LinearLayout sun_view_layout = (LinearLayout) viewList.get(currentIndex).findViewById(R.id.sun_raise_donw_view_layout);
        sun_views[currentIndex] = new SunRaiseDownView(this);
        sun_view_layout.addView(sun_views[currentIndex], 1080, 600);
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

    int pm_view_down_start = 905;
    int pm_view_down_end = 3600;
    int pm_view_up_start = 3600;
    int pm_view_up_end = 905;

    int pro_view_down_start = 1700;
    int pro_view_down_end = 4175;
    int pro_view_up_start = 4175;
    int pro_view_up_end = 1700;
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

        updateView(plot_views[currentIndex], y, plot_view_down_start,
                plot_view_down_end, plot_view_up_start, plot_view_up_end);
        updateView(grid_views[currentIndex], y, grid_view_down_start,
                grid_view_down_end, grid_view_up_start, grid_view_up_end);
        updateView(pm_views[currentIndex], y, pm_view_down_start,
                pm_view_down_end, pm_view_up_start, pm_view_up_end);
        updateView(pro_views[currentIndex], y, pro_view_down_start,
                pro_view_down_end, pro_view_up_start, pro_view_up_end);
        updateView(sun_views[currentIndex], y, sun_view_down_start,
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
    LineView plot_views[] = new LineView[conntOfCities];
    GridView grid_views[] = new GridView[conntOfCities];
    PMView pm_views[] = new PMView[conntOfCities];
    ProbabilityView pro_views[] = new ProbabilityView[conntOfCities];
    SunRaiseDownView sun_views[] = new SunRaiseDownView[conntOfCities];

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
            String city_name;
            Log.e("onPageScrolled:", "0--" + arg0 + "--1--" + arg1 + "--2--" + arg2);
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
//        Button btn = (Button)viewList.get(currentIndex).findViewById(R.id.btn_line_laout_animator);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    private void setCurrentTitle_City(String city_name, float alpha){
        TextView city_name_textview = (TextView)findViewById(R.id.city_name);
        city_name_textview.setText(city_name);
        city_name_textview.setAlpha(alpha);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = (ObservableScrollView) findViewById(R.id.scrollview);
        scrollView.setScrollViewListener(this);
        initBaseInfo();
        initViewPager();
    }
    public static CityDB cityDB;
    private void initBaseInfo(){
        infoToShow = new InfoToShow();
        getLocation();
        cityDB = openCityDB();

        refreshableView = (MyRefreshView) findViewById(R.id.refreshable_view);
        rotateView = (RotateView) findViewById(R.id.rotate_view);
        setOnFreshListener();
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
                            if(!isBreak) {
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
        Log.d("updateControls", "开始更新controls的数据");
        TextView city_name = (TextView) findViewById(R.id.city_name);
        city_name.setText(infoToShow.locationName);

        WeatherInfo weatherInfo = infoToShow.weatherInfo;

        String show;
        TextView date_time = (TextView) findViewById(R.id.date_time);
        show = weatherInfo.hourlyWeatherList.get(0).date.split(" ")[1];
        date_time.setText("今天" + show + "发布");

        TextView humidity_value = (TextView) findViewById(R.id.humidity_value);
        show = weatherInfo.hourlyWeatherList.get(0).hum;
        humidity_value.setText("湿度：" + show + "%");

        TextView temperature = (TextView) findViewById(R.id.temperature);
        show = weatherInfo.dailyWeatherList.get(1).tmp_min + "℃~" +
                weatherInfo.dailyWeatherList.get(1).tmp_max + "℃";
        temperature.setText(show);

        TextView today_week = (TextView) findViewById(R.id.today_week);
        show = "" + getDayOfWeek();
        today_week.setText(show);

        TextView climate = (TextView) findViewById(R.id.climate);
        show = "" + weatherInfo.dailyWeatherList.get(1).txt_d + "/" +
                weatherInfo.dailyWeatherList.get(1).txt_n;
        climate.setText(show);

        TextView wind = (TextView) findViewById(R.id.wind);
        show = "" + weatherInfo.dailyWeatherList.get(1).wind_sc;
        wind.setText(show);
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
            Intent intent = new Intent(this, ProvinceListActivity.class);
            Bundle bundle = new Bundle();
            ArrayList<String> provinces = getAllProvinces();
            bundle.putStringArrayList("provinces", provinces);
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
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
//                    break;
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

    private void updateTodayWeather(TodayWeather todayWeather){
        View currentView = viewList.get(currentIndex);

        String show;

        TextView date_time = (TextView) currentView.findViewById(R.id.date_time);
        show = "今天" + todayWeather.getUpdatetime() + "发布";
        date_time.setText(show);

        TextView humidity_value = (TextView) currentView.findViewById(R.id.humidity_value);
        show = todayWeather.getShidu();
        humidity_value.setText("湿度：" + show);

//        //pm2.5
//        TextView pm25_value = (TextView) findViewById(R.id.pm2_5_value);
//        show = todayWeather.getPm25();
//        pm25_value.setText("value: " + show);
//
//        TextView pm25_desc = (TextView) findViewById(R.id.pm2_5_desc);
//        show = todayWeather.getQuality();
//        pm25_desc.setText("desc: " + show);

        TextView temperature = (TextView) currentView.findViewById(R.id.temperature);
        show = todayWeather.getLow() + "/" +
                todayWeather.getHigh();
        temperature.setText(show);

        TextView today_week = (TextView) currentView.findViewById(R.id.today_week);
        show = todayWeather.getDate();
        today_week.setText(show);

        TextView climate = (TextView) currentView.findViewById(R.id.climate);
        show = "" + todayWeather.getNightType() + "/" +
                todayWeather.getDayType();
        climate.setText(show);

        TextView wind = (TextView) currentView.findViewById(R.id.wind);
        show = "" + todayWeather.getFengxiang();
        wind.setText(show);
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            //updateWithNewLocation(null);
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
            changeCity(result_value);

        }

    }

    public void changeCity(String cityName){
        mAddressCityNameHan = cityName;
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