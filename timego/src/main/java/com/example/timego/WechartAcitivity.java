package com.example.timego;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Administrator on 2017/5/20.
 */

public class WechartAcitivity extends AppCompatActivity {
    private WebView webWechart;//日报微信

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_daily_wechat);



    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            webWechart = (WebView) findViewById(R.id.dailypaper_wechat);
            webWechart.getSettings().setJavaScriptEnabled(true);
            webWechart.getSettings().setBlockNetworkImage(false);
            webWechart.loadUrl(DataSlideMenu.getDailyWechart());
            webWechart.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // TODO Auto-generated method stub
                    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }
            });
        }
    }
}
