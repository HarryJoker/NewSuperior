package gov.android.com.superior.ui;

import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.first.orient.base.activity.BaseToolBarActivity;

import gov.android.com.superior.R;

public class LinkWebActivity extends BaseToolBarActivity {

    private String url;

    private String title;

    private WebView mWebView;

    @Override
    public void onInitParams() {
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(url)) {
            showError("读取政创空间失败");
            finish();
        }
    }

    @Override
    protected void onBarTitle(TextView barTitle) {
        if (!TextUtils.isEmpty(title)) {
            barTitle.setText(title);
        }
    }

    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebViewClient(mWebViewClient);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_space_detail;
    }

    @Override
    protected void onFindViews() {
        mWebView = findViewById(R.id.webview);
    }

    @Override
    public void onInitView() {
        initWebView();
    }

    @Override
    public void onInitPresenter() {

    }

    @Override
    public void onBusiness() {
        mWebView.loadUrl(url);
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    };
}
