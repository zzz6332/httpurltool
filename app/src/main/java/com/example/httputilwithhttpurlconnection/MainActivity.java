package com.example.httputilwithhttpurlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.httputilwithhttpurlconnection.httpurlconnection.HttpUtil;
import com.example.httputilwithhttpurlconnection.httpurlconnection.httpCallBackListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    private StringBuffer stringBuffer = new StringBuffer();
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    tv.setText(stringBuffer);
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        List<String> test_list = new ArrayList<>();
        test_list.add("https://free-api.heweather.net/s6/air/now?location=重庆&key=69eb00f8b34e4c3cb3969e9a94416c70");
        test_list.add("https://free-api.heweather.net/s6/weather/now?location=重庆&key=69eb00f8b34e4c3cb3969e9a94416c70");
        test_list.add("http://gank.io/api/data/Android/10/55");
        List<String> need_list = new ArrayList();
        need_list.add("desc");
        need_list.add("url");
        need_list.add("tmp");
        HttpUtil.sendHttpWithUrlConnection(test_list.get(2), need_list, new httpCallBackListener() {
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
