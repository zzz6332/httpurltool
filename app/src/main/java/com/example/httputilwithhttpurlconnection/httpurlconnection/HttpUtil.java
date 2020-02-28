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
    private static int parse_count = 0;
    private static int count = 0;
    private static List<String> object_list = new ArrayList<>();
    private static List<String> type_list = new ArrayList<>();
    private static List<String> key_value_list = new ArrayList<>();
    private static List<String> need_list = new ArrayList<>();
    private static List<String> user_value_list = new ArrayList<>();
    private static String json_parse;

    public static void sendHttpWithUrlConnection(final String address, final List<String> mneed_list, final httpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                need_list = mneed_list;
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
                    if (need_list != null) {
                        getValue();
                        listener.OnFinish(user_value_list);
                    }else {
                        listener.OnFinish(key_value_list);
                    }
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
                    key_value_list.add("\"" + object_list.get(i) + "\"" + ": " + jsonObject.get(object_list.get(i)) + ",\n");
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
        for (int i = index; i < data.length(); i++) {
            if (data.substring(i, i + 1).equals(":")) {
                if (data.substring(i + 1, i + 2).equals("[")) {
                    find_array = true; //该类型为数组
                }
                break;
            }
        }
        if (!find_array) { //如果类型不是数组
            for (int i = index; i < data.length(); i++) {
                if (data.substring(i, i + 1).equals(":")) {
                    if (data.substring(i + 1, i + 2).equals("{")) {
                        find_object = true;
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

    public static void getValue() {
        String data = key_value_list.toString();
        StringBuffer date_buffer = new StringBuffer(data);
        for (int i = 0; i < need_list.size(); i++) {
            boolean empty = false;
            String need = need_list.get(i);
            if (data.indexOf(need) == -1 || need_list.get(i).equals("") || need_list.get(i).equals(" ")){
                user_value_list.add("\"" + need_list.get(i) + "\": null \n");
                empty = true;
            }
            while (data.indexOf(need) != -1 && !empty) {
                int index = data.indexOf(need);
                boolean find = false;
                for (int j = index; j < data.length(); j++) {
                    if (data.substring(j, j + 1).equals(":")) {
                        for (int k = j; k < data.length(); k++) {
                            if (data.substring(k, k + 1).equals(",")) {
                                String value = data.substring(j + 1, k);
                                find = true;
                                user_value_list.add("\"" + need_list.get(i) + "\"" + ": " + value + "\n");
                                date_buffer.delete(index, j);
                                data = date_buffer.toString();
                                break;
                            }
                        }
                    }
                    if (find) {
                        find = false;
                        break;
                    }
                }
            }


        }
    }
}