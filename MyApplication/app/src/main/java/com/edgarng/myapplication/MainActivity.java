package com.edgarng.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private Button btn2;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        btn2 = findViewById(R.id.btn2);
        mWebView = findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

        });

        mWebView.addJavascriptInterface(new Object() {
            //注意4.4以后加注解，位置在这个方法名上面，鉴于很多这个的例子，瞎、、写注解位置，并需要下
            //载积分写了这个
            @JavascriptInterface
            public void invoke(String name, String t, final String callback) {
                if (name.equals("testFunc")) {  //其中t 为js带过来的数据
                    Toast.makeText(MainActivity.this, "======" + t, Toast.LENGTH_LONG).show();


                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            String strJson = "{\"code\":122, \"msg\":\"1231\", \"data\":null}";
                            //回调数据给js 其中callback 为android 掉js 的方法名称。
                            mWebView.loadUrl("javascript:" + callback + "('" + strJson + "')");


//                            mWebView.loadUrl("javascript:callJs()");
                        }
                    });

                }
//                Toast.makeText(MainActivity.this, name, Toast.LENGTH_LONG).show();
            }
        }, "MfsJSBridge");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("file:///android_asset/test.html");
            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:callJs()");
            }
        });
    }
}
