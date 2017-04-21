package com.example.umshaik.androidgoodpractices;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView localWebView = (WebView) findViewById(R.id.webview_lin);
        localWebView.setWebChromeClient(new WebChromeClient());
        localWebView.loadUrl("http://linde.noguska.com/index.php?44198641B898!18a76351&task=weather&andriod=1&cords=1");

    }
}
