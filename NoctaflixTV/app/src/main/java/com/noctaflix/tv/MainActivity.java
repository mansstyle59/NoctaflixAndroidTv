package com.noctaflix.tv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView webView;
    private ProgressBar progressBar;
    private static final String HOME_URL = "https://noctaflix.lol";

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen layout
        RelativeLayout layout = new RelativeLayout(this);
        layout.setBackgroundColor(0xFF0D0D0D);

        // WebView setup
        webView = new WebView(this);
        webView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);

        // ProgressBar
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        RelativeLayout.LayoutParams pbParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, 8);
        pbParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        progressBar.setLayoutParams(pbParams);
        progressBar.setMax(100);
        progressBar.setProgressTintList(
                android.content.res.ColorStateList.valueOf(0xFFE50914)); // Netflix red

        layout.addView(webView);
        layout.addView(progressBar);
        setContentView(layout);

        configureWebView();
        webView.loadUrl(HOME_URL);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {
        WebSettings settings = webView.getSettings();

        // JavaScript & Media
        settings.setJavaScriptEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        // Performance
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        // Rendering
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setTextZoom(100);

        // User Agent – TV browser
        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + " NoctaflixTV/1.0 AndroidTV");

        // Hardware acceleration
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("intent://") || url.startsWith("market://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                // Stay in WebView for same domain
                if (url.contains("noctaflix")) {
                    view.loadUrl(url);
                    return true;
                }
                // Open external links in browser
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                                        WebResourceError error) {
                if (request.isForMainFrame()) {
                    String errorPage = "<html><body style='background:#0D0D0D;color:#fff;"
                            + "display:flex;flex-direction:column;align-items:center;"
                            + "justify-content:center;height:100vh;font-family:sans-serif;margin:0'>"
                            + "<h1 style='color:#E50914;font-size:48px'>⚠</h1>"
                            + "<h2>Connexion impossible</h2>"
                            + "<p>Vérifiez votre connexion internet</p>"
                            + "<button onclick='location.reload()' style='background:#E50914;"
                            + "color:#fff;border:none;padding:12px 32px;font-size:18px;"
                            + "border-radius:4px;cursor:pointer;margin-top:16px'>Réessayer</button>"
                            + "</body></html>";
                    view.loadData(errorPage, "text/html", "UTF-8");
                }
            }
        });

        // WebChromeClient for video & progress
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                // optional: update app bar title
            }

            // Allow video autoplay
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        // CookieManager
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
    }

    // ─── TV Remote Control Navigation ───────────────────────────────────────

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_BUTTON_B:
                if (webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_BUTTON_X:
            case KeyEvent.KEYCODE_MENU:
                webView.loadUrl(HOME_URL);
                return true;

            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_BUTTON_A:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                // Pass through to WebView (click on focused element)
                return super.onKeyDown(keyCode, event);

            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                webView.evaluateJavascript(
                        "document.querySelector('video')?.currentTime += 10", null);
                return true;

            case KeyEvent.KEYCODE_MEDIA_REWIND:
                webView.evaluateJavascript(
                        "document.querySelector('video')?.currentTime -= 10", null);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // ─── Lifecycle ───────────────────────────────────────────────────────────

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        webView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.stopLoading();
        webView.destroy();
    }
}
