package com.cinejoy.tv;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private static final String HOME_URL = "https://cinejoy.pk";

    private WebView webView;
    private LinearLayout sidebar;
    private FrameLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootLayout = new FrameLayout(this);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.BLACK);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setUserAgentString(
                s.getUserAgentString() + " CinejoyTV/1.0 AndroidTV"
        );

        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request) {

                Uri u = request.getUrl();
                String scheme = u.getScheme();

                if ("http".equalsIgnoreCase(scheme)
                        || "https".equalsIgnoreCase(scheme)) {
                    view.loadUrl(u.toString());
                    return true;
                }

                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        rootLayout.addView(webView);

        createSidebar();

        setContentView(rootLayout);

        webView.requestFocus();
        webView.loadUrl(HOME_URL);
    }

    private void createSidebar() {

        sidebar = new LinearLayout(this);
        sidebar.setOrientation(LinearLayout.VERTICAL);
        sidebar.setGravity(Gravity.CENTER);
        sidebar.setPadding(12, 20, 12, 20);
        sidebar.setBackgroundColor(Color.rgb(35, 35, 35));

        FrameLayout.LayoutParams sidebarParams =
                new FrameLayout.LayoutParams(
                        150,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        sidebarParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;

        sidebar.setLayoutParams(sidebarParams);

        addSidebarButton("Home", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(HOME_URL);
                closeSidebar();
            }
        });

        addSidebarButton("Back", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });

        addSidebarButton("Reload", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });

        addSidebarButton("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSidebar();
            }
        });

        sidebar.setVisibility(View.GONE);

        rootLayout.addView(sidebar);
    }

    private void addSidebarButton(
            String text,
            View.OnClickListener listener) {

        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(14);
        button.setFocusable(true);
        button.setOnClickListener(listener);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        65
                );

        params.setMargins(0, 8, 0, 8);

        sidebar.addView(button, params);
    }

    private void openSidebar() {
        sidebar.setVisibility(View.VISIBLE);
        sidebar.getChildAt(0).requestFocus();
    }

    private void closeSidebar() {
        sidebar.setVisibility(View.GONE);
        webView.requestFocus();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            int keyCode = event.getKeyCode();

            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                    && sidebar.getVisibility() != View.VISIBLE) {

                openSidebar();
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                    && sidebar.getVisibility() == View.VISIBLE) {

                closeSidebar();
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_BACK
                    && webView != null
                    && webView.canGoBack()) {

                webView.goBack();
                return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {

        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }

        super.onDestroy();
    }
}
