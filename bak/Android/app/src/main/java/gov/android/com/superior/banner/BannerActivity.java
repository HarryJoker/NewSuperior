package gov.android.com.superior.banner;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import gov.android.com.superior.BaseActivity;
import gov.android.com.superior.R;

public class BannerActivity extends BaseActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");

        setTitle(title == null ? "" : title);

        if (url != null) webView.loadUrl(url);
    }
}
