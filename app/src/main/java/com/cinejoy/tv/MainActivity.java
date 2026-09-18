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
    private View customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootLayout = new FrameLayout(this);

        createWebView();
        createSidebar();

        setContentView(rootLayout);

        webView.requestFocus();
        webView.loadUrl(HOME_URL);
    }

    private void createWebView() {

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

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onShowCustomView(
                    View view,
                    CustomViewCallback callback) {

                customView = view;

                // Hide sidebar during fullscreen video
                sidebar.setVisibility(View.GONE);

                FrameLayout.LayoutParams params =
                        new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                        );

                rootLayout.addView(customView, params);
                customView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
            }

            @Override
            public void onHideCustomView() {

                if (customView != null) {

                    rootLayout.removeView(customView);
                    customView = null;

                    // Show sidebar again after exiting video
                    sidebar.setVisibility(View.VISIBLE);
                    webView.requestFocus();
                }
            }
        });

        // Reserve space for the sidebar
        FrameLayout.LayoutParams webParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        webParams.leftMargin = 170;

        rootLayout.addView(webView, webParams);
    }

    private void createSidebar() {

        sidebar = new LinearLayout(this);

        sidebar.setOrientation(LinearLayout.VERTICAL);
        sidebar.setGravity(Gravity.CENTER);
        sidebar.setPadding(12, 20, 12, 20);
        sidebar.setBackgroundColor(Color.rgb(35, 35, 35));

        FrameLayout.LayoutParams sidebarParams =
                new FrameLayout.LayoutParams(
                        170,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        sidebarParams.gravity =
                Gravity.START | Gravity.CENTER_VERTICAL;

        sidebar.setLayoutParams(sidebarParams);

        addSidebarButton("Home", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                webView.loadUrl(HOME_URL);
                webView.requestFocus();
            }
        });

        addSidebarButton("Back", new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (webView.canGoBack()) {
                    webView.goBack();
                }

                webView.requestFocus();
            }
        });

        addSidebarButton("Reload", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                webView.reload();
                webView.requestFocus();
            }
        });

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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            int keyCode = event.getKeyCode();

            // BACK exits fullscreen video first
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && customView != null) {

                if (webView.getWebChromeClient() != null) {
                    webView.evaluateJavascript(
                            "document.exitFullscreen && document.exitFullscreen();",
                            null
                    );
                }

                return true;
            }

            // Normal browser history
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
