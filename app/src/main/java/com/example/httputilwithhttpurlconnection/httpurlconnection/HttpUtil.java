package com.example.httputilwithhttpurlconnection.httpurlconnection;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HttpUtil {
    public static final String tag = "HttpUtillllllll";
    private static int key_number = 1;
    private static int left_number = 1;
    private static int parse_count = 0;
    private static int count = 0;
    private static boolean isFirst = true;
    private static boolean isAdded = false;
    private static int count_left = 0;
    private static int index_left = 0;
    private static List<String> object_list = new ArrayList<>();
    private static List<String> type_list = new ArrayList<>();
    private static Map<String, Object> map = new IdentityHashMap<String, Object>();
    private static List<String> key_value_list = new ArrayList<>();
    private static String json_parse;

    public static void sendHttpWithUrlConnection(final String address, final httpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    parse(response.toString());
                    listener.OnFinish(key_value_list);
                } catch (Exception e) {
                    listener.OnError();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void parse(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            Iterator<String> keys = jsonObject.keys();
            json_parse = jsonObject.toString();
            addKey(keys);
            parseWithType(jsonObject);//
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void parseWithType(JSONObject jsonObject) {
        int number = parse_count;
        for (int i = number; i < type_list.size(); i++) {
            try {
                if (type_list.get(i).equals("键值对")) {
                    key_value_list.add("\"" + object_list.get(i) + "\"" + ": " + jsonObject.get(object_list.get(i)) + "\n");
                }
                if (type_list.get(i).equals("对象")) {  //如果是json对象
                    JSONObject jsonObject1 = jsonObject.getJSONObject(object_list.get(i));
                    parse_count = type_list.size();
                    parseSencondWithObject(jsonObject1.toString());
                } else if (type_list.get(i).equals("数组")) {
                    JSONArray jsonObject1 = jsonObject.getJSONArray(object_list.get(i));
                    parse_count = type_list.size();
                    parseSencondWithArray(jsonObject1.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
            parse_count = type_list.size();
        }

    }

    public static void parseSencondWithObject(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            Iterator<String> keys = jsonObject.keys();
            addKey(keys); //
            parseWithType(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseSencondWithArray(String data) {
        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                Log.d(tag, "i:" + i + "array长度:" + array.length());
                JSONObject jsonObject = array.getJSONObject(i);
                Iterator iterator = jsonObject.keys();
                addKey(iterator);
                parseWithType(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void isArrayOrObjectOrValue(String data) { //将类型添加到type_list
        boolean find_object = false;
        boolean find_array = false;
        int index = data.lastIndexOf(object_list.get(count - 1));
        for (int i = index; i < data.length() ; i++) {
            if (data.substring(i,i+1).equals(":")){
                if (data.substring(i+1,i+2).equals("[")){
                    find_array= true; //该类型为数组
                }
                break;
            }
        }
        if (!find_array){ //如果类型不是数组
            for (int i = index; i < data.length() ; i++) {
                if (data.substring(i,i+1).equals(":")){
                    if (data.substring(i+1,i+2).equals("{")){
                        find_object= true;
                    }
                    break;
                }
            }
        }
        if (find_array) {
            type_list.add("数组");
        } else if (find_object) {
            type_list.add("对象");
        } else {
            type_list.add("键值对");
        }
    }

    public static void addKey(Iterator<String> keys) { //添加key名字
        while (keys.hasNext()) {
            count++;
            object_list.add(keys.next());
            isArrayOrObjectOrValue(json_parse);
        }
    }
}