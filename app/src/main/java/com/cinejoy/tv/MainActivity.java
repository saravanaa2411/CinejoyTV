package com.cinejoy.tv;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
    private WebChromeClient.CustomViewCallback customViewCallback;

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

    // =========================================================
    // WEBVIEW
    // =========================================================

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

        CookieManager cookieManager =
                CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(
                webView,
                true
        );

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request) {

                Uri uri = request.getUrl();
                String scheme = uri.getScheme();

                if ("http".equalsIgnoreCase(scheme)
                        || "https".equalsIgnoreCase(scheme)) {

                    view.loadUrl(uri.toString());
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

                // Avoid duplicate fullscreen views
                if (customView != null) {
                    callback.onCustomViewHidden();
                    return;
                }

                customView = view;
                customViewCallback = callback;

                // Hide floating sidebar during video
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

                exitFullscreen();
            }
        });

        // WebView occupies the complete screen.
        // Sidebar floats above the WebView.

        FrameLayout.LayoutParams webParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

        rootLayout.addView(webView, webParams);
    }

    // =========================================================
    // FLOATING SIDEBAR
    // =========================================================

    private void createSidebar() {

        sidebar = new LinearLayout(this);

        sidebar.setOrientation(LinearLayout.VERTICAL);
        sidebar.setGravity(Gravity.CENTER);
        sidebar.setPadding(8, 16, 8, 16);

        // Rounded translucent background
        GradientDrawable sidebarBackground =
                new GradientDrawable();

        sidebarBackground.setColor(
                Color.argb(225, 25, 25, 30)
        );

        sidebarBackground.setCornerRadius(35);

        sidebar.setBackground(sidebarBackground);
        sidebar.setElevation(15);

        FrameLayout.LayoutParams sidebarParams =
                new FrameLayout.LayoutParams(
                        135,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );

        sidebarParams.gravity =
                Gravity.START | Gravity.CENTER_VERTICAL;

        sidebarParams.setMargins(16, 0, 0, 0);

        sidebar.setLayoutParams(sidebarParams);

        // HOME BUTTON
        addSidebarButton(
                "HOME",
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        webView.loadUrl(HOME_URL);
                        webView.requestFocus();
                    }
                }
        );

        // BACK BUTTON
        addSidebarButton(
                "BACK",
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (webView.canGoBack()) {
                            webView.goBack();
                        }

                        webView.requestFocus();
                    }
                }
        );

        // RELOAD BUTTON
        addSidebarButton(
                "RELOAD",
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        webView.reload();
                        webView.requestFocus();
                    }
                }
        );

        // Sidebar remains visible during browsing
        sidebar.setVisibility(View.VISIBLE);

        // Add sidebar last so it floats above WebView
        rootLayout.addView(sidebar);
    }

    // =========================================================
    // SIDEBAR BUTTON DESIGN
    // =========================================================

    private void addSidebarButton(
            String text,
            View.OnClickListener listener) {

        Button button = new Button(this);

        button.setText(text);
        button.setTextSize(12);
        button.setTextColor(Color.WHITE);

        button.setAllCaps(false);
        button.setGravity(Gravity.CENTER);

        button.setFocusable(true);
        button.setFocusableInTouchMode(true);

        button.setBackgroundColor(Color.TRANSPARENT);

        button.setOnClickListener(listener);

        // Focus effect
        button.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(
                            View v,
                            boolean hasFocus) {

                        GradientDrawable background =
                                new GradientDrawable();

                        background.setCornerRadius(18);

                        if (hasFocus) {

                            background.setColor(
                                    Color.rgb(80, 80, 90)
                            );

                        } else {

                            background.setColor(
                                    Color.TRANSPARENT
                            );
                        }

                        v.setBackground(background);
                    }
                }
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        58
                );

        params.setMargins(0, 6, 0, 6);

        sidebar.addView(button, params);
    }

    // =========================================================
    // EXIT FULLSCREEN VIDEO
    // =========================================================

    private void exitFullscreen() {

        if (customView == null) {
            return;
        }

        rootLayout.removeView(customView);

        customView = null;

        if (customViewCallback != null) {

            customViewCallback.onCustomViewHidden();
            customViewCallback = null;
        }

        // Show sidebar again after video exits
        sidebar.setVisibility(View.VISIBLE);

        webView.requestFocus();
    }

    // =========================================================
    // REMOTE CONTROL
    // =========================================================

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            int keyCode = event.getKeyCode();

            // BACK exits fullscreen first
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && customView != null) {

                exitFullscreen();
                return true;
            }

            // Normal WebView navigation
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && webView != null
                    && webView.canGoBack()) {

                webView.goBack();
                return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    // =========================================================
    // ACTIVITY DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        if (webView != null) {

            webView.stopLoading();
            webView.destroy();
        }

        super.onDestroy();
    }
}
