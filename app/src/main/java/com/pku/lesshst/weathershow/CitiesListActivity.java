package com.pku.lesshst.weathershow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.pku.lesshst.weathershow.bean.City;
import com.pku.lesshst.weathershow.bean.CityDB;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lzb on 2015/10/19.
 */
public class CitiesListActivity extends Activity  implements View.OnClickListener{
    public CitiesListActivity instance;
    private ListView listView;
    SimpleAdapter mSchedule;
    private ImageView mBackBtn;
    private EditText mEditText;
    private ArrayList<HashMap<String, String>> listData;
    private ArrayList<HashMap<String, String>> listDataBackup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.province_list);

        mEditText = (EditText)findViewById(R.id.search_edit);
        mEditText.addTextChangedListener(mTextWatcher); //给EditText设置监听！

        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        ArrayList<String> provinces = bundle.getStringArrayList("cities");

        listView = (ListView) findViewById(R.id.province_list_view);

        listData = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemTitle", "大兴");
        map.put("dangqian", "当前城市");
        listData.add(map);
        for(String province : provinces)
        {
            map = new HashMap<String, String>();
            map.put("ItemTitle", province);
            map.put("dangqian", "");
            listData.add(map);
        }
        listDataBackup = new ArrayList<HashMap<String, String>>();
        for(HashMap<String, String> hash : listData){
            map = new HashMap<String, String>();
            map.put("ItemTitle", hash.get("ItemTitle"));
            map.put("dangqian", hash.get("dangqian"));
            listDataBackup.add(map);
        }
        mSchedule = new SimpleAdapter(this,
                listData,
                R.layout.my_listview_item,
                new String[] {"ItemTitle", "dangqian"},
                new int[] {R.id.ItemTitle, R.id.dangqian});

        listView.setAdapter(mSchedule);
        instance = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
                String cityName = map.get("ItemTitle");

                Intent intent = getIntent();
                intent.putExtra("city", cityName);
                setResult(RESULT_OK, intent);
                instance.finish();
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

    //点击“返回”键，关闭当前Activity
    @Override
    public void onClick(View view){
        if(view.getId() == R.id.title_back){
            finish(); //结束当前Activity!!!
        }
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        /**
         *  ==========文字变化前=========
         * @param s      改变之前的内容
         * @param start 开始的位置
         * @param count 被改变的旧内容数
         * @param after 改变后的内容数量
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        /**
         *  ==========文字变化时=========
         * @param s          改变之后的内容
         * @param start
         * @param before    被改变的内容的数量
         * @param count
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //必须得加上这句！！！
            listData.clear();
            String input = s.toString();
            for(HashMap<String, String> hash : listDataBackup) {
                if(-1 != hash.get("ItemTitle").toString().indexOf(input)){
                    listData.add(hash);
                }
            }
            mSchedule.notifyDataSetChanged();
        }
        // ==========文字变化后=========
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

}
