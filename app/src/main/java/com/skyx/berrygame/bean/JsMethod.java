package com.skyx.berrygame.bean;

import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.skyx.berrygame.WebActivity;

public class JsMethod {

    public JsMethodInterface mJsMethodInterface;

    public JsMethod(JsMethodInterface jsMethodInterface) {
        this.mJsMethodInterface = jsMethodInterface;

    }

    @JavascriptInterface
    public void postMessage(String params) {
        //解析数据再回调--直接回调
        Log.d("BerryGame[postMessage]", params);
        mJsMethodInterface.postMessageCallBack(params);
    }
}
