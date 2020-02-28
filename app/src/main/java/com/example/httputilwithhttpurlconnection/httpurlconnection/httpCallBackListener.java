package com.example.httputilwithhttpurlconnection.httpurlconnection;

import java.util.List;
import java.util.Map;

public interface httpCallBackListener {
    void OnFinish(List<String> list);
    void OnError();
}
