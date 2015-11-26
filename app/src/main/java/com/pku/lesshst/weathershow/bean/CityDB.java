package com.pku.lesshst.weathershow.bean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by lzb on 2015/10/19.
 */
public class CityDB {
    public static final String CITY_DB_NAME = "city.db";
    private static final String CITY_TABLE_NAME = "city";
    private SQLiteDatabase db;

    public CityDB(Context context, String path){
        db = context.openOrCreateDatabase(CITY_DB_NAME, Context.MODE_PRIVATE, null);
    }

    public City getCity(String cityName){
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + CITY_TABLE_NAME + " WHERE city = ?", new String[]{cityName});
            while (c.moveToNext()) {
                String province = c.getString(c.getColumnIndex("province"));
                String city = c.getString(c.getColumnIndex("city"));
                String number = c.getString(c.getColumnIndex("number"));
                String allPY = c.getString(c.getColumnIndex("allpy"));
                String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
                String firstPY = c.getString(c.getColumnIndex("firstpy"));
                return new City(province, city, number, allPY, allFirstPY, firstPY);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Lesshst, exception", e.toString());
        }
        return null;
    }

    public City getCityProvince(String cityName){
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + CITY_TABLE_NAME + " WHERE city = ?", new String[]{cityName});
            while (c.moveToNext()) {
                String province = c.getString(c.getColumnIndex("province"));
                String city = c.getString(c.getColumnIndex("city"));
                String number = c.getString(c.getColumnIndex("number"));
                String allPY = c.getString(c.getColumnIndex("allpy"));
                String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
                String firstPY = c.getString(c.getColumnIndex("firstpy"));
                return new City(province, city, number, allPY, allFirstPY, firstPY);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Lesshst, exception", e.toString());
        }
        return null;
    }

    public ArrayList<City> getAllCity(){
        ArrayList<City> ret = new ArrayList<City>();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + CITY_TABLE_NAME, null);
            while (c.moveToNext()) {
                String province = c.getString(c.getColumnIndex("province"));
                String city = c.getString(c.getColumnIndex("city"));
                String number = c.getString(c.getColumnIndex("number"));
                String allPY = c.getString(c.getColumnIndex("allpy"));
                String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
                String firstPY = c.getString(c.getColumnIndex("firstpy"));
                ret.add(new City(province, city, number, allPY, allFirstPY, firstPY));
            }
            return ret;
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Lesshst, exception", e.toString());
        }
        return null;
    }

    public ArrayList<City> getProvinceAllCities(String provinceName){
        ArrayList<City> ret = new ArrayList<City>();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + CITY_TABLE_NAME + " WHERE province = ?", new String[]{provinceName});
            while (c.moveToNext()) {
                String province = c.getString(c.getColumnIndex("province"));
                String city = c.getString(c.getColumnIndex("city"));
                String number = c.getString(c.getColumnIndex("number"));
                String allPY = c.getString(c.getColumnIndex("allpy"));
                String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
                String firstPY = c.getString(c.getColumnIndex("firstpy"));
                ret.add(new City(province, city, number, allPY, allFirstPY, firstPY));
            }
            return ret;
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Lesshst, exception", e.toString());
        }
        return null;
    }
    public String getCityFromQu(String qu){
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + "cityqu" + " WHERE qu = ?", new String[]{qu});
            while (c.moveToNext()) {
                return c.getString(c.getColumnIndex("city"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("Lesshst, exception", e.toString());
        }
        return null;
    }
}
