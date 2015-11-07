package com.pku.lesshst.weathershow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.pku.lesshst.weathershow.bean.City;
import com.pku.lesshst.weathershow.bean.CityDB;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lzb on 2015/10/19.
 */
public class CitiesListActivity extends Activity {
    public CitiesListActivity instance;
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.province_list);

        Bundle bundle = getIntent().getExtras();
        ArrayList<String> provinces = bundle.getStringArrayList("cities");

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
                String cityName = map.get("ItemTitle");

                Intent intent = getIntent();
                intent.putExtra("city", cityName);
                setResult(RESULT_OK, intent);
                instance.finish();
//                Intent intent = new Intent(this, ProvinceListActivity.class);
//                Bundle bundle = new Bundle();
//                ArrayList<String> cities = getProvinceAllCities(provinceName);
//                bundle.putStringArrayList("provinces", cities);
//                intent.putExtras(bundle);
//                startActivityForResult(intent, 1);
            }
        });
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
}
