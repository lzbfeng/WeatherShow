package com.pku.lesshst.weathershow.utils;

/**
 * Created by lzb on 2015/10/19.
 */
public class StringProcessing {
    public static String FilterStr(String str){
        String filterStr = "省市区县";

        for(int i = 0; i < filterStr.length(); i++){
            String s = filterStr.substring(i, i + 1);
            if(str.endsWith(s)) {
                str = str.substring(0, filterStr.length() - 2);
                break;
            }
        }
        return str;
    }
}
