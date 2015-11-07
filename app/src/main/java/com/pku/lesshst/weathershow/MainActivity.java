package com.pku.lesshst.weathershow;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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

public class MainActivity extends Activity {
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
                    String show = b.getString("天气更新时间");
                    TextView myLocationText = (TextView) findViewById(R.id.cityName);
                    myLocationText.setText(show);
                    updateControls();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_refresh);
        init();
    }
    public static CityDB cityDB;
    private void init(){
        refreshableView = (MyRefreshView) findViewById(R.id.refreshable_view);
        rotateView = (RotateView) findViewById(R.id.rotate_view);
        infoToShow = new InfoToShow();
        getLocation();
        setOnFreshListener();
        cityDB = openCityDB();
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
                case GetWeatherInfo.UPDATE_PM_VALUE:
                    updateAdminAreaPMValue((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private void getWeatherInfoFromXML(){
        try {
            GetWeatherInfo getweatherInfo = new GetWeatherInfo();
            City city = cityDB.getCity(mAdminArea);
            String cityCode;
            if(city != null){
                cityCode = city.getCityNumber();
                getweatherInfo.queryWeatherCode(cityCode, mainHandler);
            }

            cityCode = cityDB.getCity(mAddressCityNameHan).getCityNumber();
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
        Log.d("updateControls", "开始更新controls的数据");
        TextView city_name = (TextView) findViewById(R.id.city_name);
        city_name.setText(todayWeather.getCity());

        String show;

        TextView date_time = (TextView) findViewById(R.id.date_time);
        show = "今天" + todayWeather.getUpdatetime() + "发布";
        date_time.setText(show);

        TextView humidity_value = (TextView) findViewById(R.id.humidity_value);
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

        TextView temperature = (TextView) findViewById(R.id.temperature);
        show = todayWeather.getLow() + "/" +
                todayWeather.getHigh();
        temperature.setText(show);

        TextView today_week = (TextView) findViewById(R.id.today_week);
        show = todayWeather.getDate();
        today_week.setText(show);

        TextView climate = (TextView) findViewById(R.id.climate);
        show = "" + todayWeather.getNightType() + "/" +
                todayWeather.getDayType();
        climate.setText(show);

        TextView wind = (TextView) findViewById(R.id.wind);
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
