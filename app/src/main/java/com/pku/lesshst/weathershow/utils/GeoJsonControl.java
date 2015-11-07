package com.pku.lesshst.weathershow.utils;

/**
 * Created by lzb on 2015/10/11.
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeoJsonControl {

    private String _json;
    public GeoJsonControl(String json){
        _json = json;
    }

    public String getLocationCity() throws JSONException{
        JSONObject json = new JSONObject(_json);
        JSONArray jsonArray = json.getJSONArray("addrList");
        int length = jsonArray.length();
        JSONObject first = (JSONObject)jsonArray.get(0);
        String ss = first.get("admName").toString();
        String[] strs = ss.split(",");
        String qu = strs[strs.length - 1];
        return qu;
    }
}

