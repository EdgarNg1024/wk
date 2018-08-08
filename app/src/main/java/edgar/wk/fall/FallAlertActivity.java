package edgar.wk.fall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import edgar.wk.R;

public class FallAlertActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    protected WebView webView;

    @BindView(R.id.textView)
    protected TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_alert);
        ButterKnife.bind(this);
        String url = "www.baidu.com";
        webView.loadUrl(url);
    }
}
