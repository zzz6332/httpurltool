package com.example.httputilwithhttpurlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.util.Log;
import android.widget.TextView;

import com.example.httputilwithhttpurlconnection.httpurlconnection.HttpUtil;
import com.example.httputilwithhttpurlconnection.httpurlconnection.httpCallBackListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    private StringBuffer stringBuffer = new StringBuffer();
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    textView.setText(stringBuffer);
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv);
        List<String> test_list = new ArrayList<>();
        test_list.add("https://free-api.heweather.net/s6/air/now?location=重庆&key=69eb00f8b34e4c3cb3969e9a94416c70");
        test_list.add("https://free-api.heweather.net/s6/weather/now?location=重庆&key=69eb00f8b34e4c3cb3969e9a94416c70");
        test_list.add("http://gank.io/api/data/Android/10/55");
        List<String> need_list = new ArrayList();
        need_list.add("desc");
        need_list.add("no2");
        HttpUtil.sendHttpWithUrlConnection(test_list.get(0),need_list, new httpCallBackListener() {
            @Override
            public void OnFinish(List<String> list) {
                for (int i = 0; i < list.size(); i++) {
                    stringBuffer.append(list.get(i));
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void OnError() {

            }
        });
    }
}
