package dz.edu.matiere3am;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.graphics.Color;
import android.graphics.Insets;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebResourceError;
import android.webkit.CookieManager;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class MainActivity extends Activity {
    private WebView browser;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(7, 43, 77));
        getWindow().setNavigationBarColor(Color.rgb(7, 43, 77));
        browser = new WebView(this);
        browser.setBackgroundColor(Color.rgb(245, 248, 253));
        setContentView(browser);
        // Keep content clear of Android 15+ system bars with targetSdk 35.
        if (Build.VERSION.SDK_INT >= 35) {
            browser.setOnApplyWindowInsetsListener((v, insets) -> {
                Insets bars = insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }
        WebSettings settings = browser.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setBlockNetworkLoads(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setDefaultTextEncodingName("UTF-8");
        CookieManager.getInstance().setAcceptCookie(false);
        browser.setWebChromeClient(new WebChromeClient());
        browser.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true; // App does not navigate to external addresses.
            }
            @Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (!"https".equals(request.getUrl().getScheme()) ||
                    !"appassets.androidplatform.net".equals(request.getUrl().getHost())) {
                    return new WebResourceResponse("text/plain", "UTF-8", 403, "Forbidden",
                        java.util.Collections.emptyMap(), new java.io.ByteArrayInputStream(new byte[0]));
                }
                return null;
            }
        });
        try (InputStream in = getAssets().open("index.html");
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int count;
            while ((count = in.read(buf)) != -1) out.write(buf, 0, count);
            String html = new String(out.toByteArray(), StandardCharsets.UTF_8);
            browser.loadDataWithBaseURL("https://appassets.androidplatform.net/", html,
                "text/html", "UTF-8", null);
        } catch (Exception e) {
            browser.loadDataWithBaseURL("https://appassets.androidplatform.net/",
                "<html lang='ar' dir='rtl'><meta charset='utf-8'><h3>تعذر فتح التطبيق</h3></html>",
                "text/html", "UTF-8", null);
        }
    }
    @Override public void onBackPressed() {
        if (browser != null && browser.canGoBack()) browser.goBack();
        else super.onBackPressed();
    }
    @Override protected void onDestroy() {
        if (browser != null) {browser.destroy();browser = null;}
        super.onDestroy();
    }
}
