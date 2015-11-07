package com.pku.lesshst.weathershow.utils;

import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created by lzb on 2015/9/24.
 */
public class GetWeather {

    public static String getWeather(String addressName)
    {
        String httpUrl = "http://apis.baidu.com/heweather/weather/free";
        String httpArg = "city=" + addressName;
        String jsonResult = request(httpUrl, httpArg);
        return jsonResult;
    }
    /**
     * @param urlAll
     *            :请求接口
     * @param httpArg
     *            :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {

            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", "9efeb71f9007d397d479c9d96ad29817");
            try{
                connection.connect();
            }
            catch (Exception e)
            {
                String str = e.toString();
                Log.d(str, "s");
            }
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String getLocalIpAddress(){

        try{
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&& InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {

                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }catch (SocketException e) {
            // TODO: handle exception
//            Utils.log("WifiPreference IpAddress---error-" + e.toString());
            e.printStackTrace();
        }

        return null;
    }
}
