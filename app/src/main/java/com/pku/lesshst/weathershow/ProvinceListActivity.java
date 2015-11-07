package com.pku.lesshst.weathershow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.pku.lesshst.weathershow.bean.CityDB;

import java.util.ArrayList;
import java.util.HashMap;
import com.pku.lesshst.weathershow.bean.City;

/**
 * Created by lzb on 2015/10/19.
 */
public class ProvinceListActivity extends Activity {

    public static ProvinceListActivity instance;
    private ListView list;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.province_list);

        Bundle bundle = getIntent().getExtras();
        ArrayList<String> provinces = bundle.getStringArrayList("provinces");
        list = (ListView) findViewById(R.id.province_list_view);

        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        for(String province : provinces)
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", province);
            mylist.add(map);
        }
        SimpleAdapter mSchedule = new SimpleAdapter(this,
                mylist,
                R.layout.my_listview_item,
                new String[] {"ItemTitle"},
                new int[] {R.id.ItemTitle});
        list.setAdapter(mSchedule);
        instance = this;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) list.getItemAtPosition(position);
                String provinceName = map.get("ItemTitle");

                try{
                    Message msg = new Message();
                    msg.what = 1023;
                    msg.obj = provinceName;
                    mainHandler.sendMessage(msg);
                }
                catch(Exception e){
                    e.printStackTrace();
                    Log.d("Lesshst: ", e.toString());
                }

            }
        });
    }

    private Handler mainHandler = new Handler(){

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1023:
                    startCitiesListActivity((String) msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private void startCitiesListActivity(String provinceName){
        intent = new Intent(this, CitiesListActivity.class);
        Bundle bundle = new Bundle();
        ArrayList<String> cities = getProvinceAllCities(provinceName);
        bundle.putStringArrayList("cities", cities);
        intent.putExtras(bundle);
        startActivityForResult(intent, 2);
    }
    private ArrayList<String> getProvinceAllCities(String provinceName){
        ArrayList<String> ret = new ArrayList<String>();
        CityDB db = MainActivity.cityDB;
        ArrayList<City> cities = db.getProvinceAllCities(provinceName);
        for(City city : cities){
            ret.add(city.getCity());
        }
        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 2){
            String result_value = data.getStringExtra("city");
            Log.d("Lesshst: ret value", result_value);
            Intent intent = getIntent();
            intent.putExtra("city", result_value);
            setResult(RESULT_OK, intent);
            this.finish();
        }
    }
}
