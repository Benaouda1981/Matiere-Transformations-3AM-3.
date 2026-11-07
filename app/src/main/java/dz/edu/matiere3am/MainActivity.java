
package dz.edu.matiere3am;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.graphics.Color;
import android.graphics.Insets;
import android.view.WindowInsets;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.CookieManager;

public final class MainActivity extends Activity {

    private WebView browser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(
            Color.rgb(7, 43, 77)
        );
        getWindow().setNavigationBarColor(
            Color.rgb(7, 43, 77)
        );

        browser = new WebView(this);
        browser.setBackgroundColor(
            Color.rgb(245, 248, 253)
        );

        setContentView(browser);

        if (Build.VERSION.SDK_INT >= 35) {
            browser.setOnApplyWindowInsetsListener(
                (view, insets) -> {
                    Insets bars = insets.getInsets(
                        WindowInsets.Type.systemBars()
                        | WindowInsets.Type.displayCutout()
                    );

                    view.setPadding(
                        bars.left,
                        bars.top,
                        bars.right,
                        bars.bottom
                    );

                    return insets;
                }
            );
        }

        WebSettings settings = browser.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        // Allow loading the local HTML file
        settings.setAllowFileAccess(true);

        settings.setAllowContentAccess(false);
        settings.setBlockNetworkLoads(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setDefaultTextEncodingName("UTF-8");

        if (Build.VERSION.SDK_INT >= 16) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }

        CookieManager.getInstance()
            .setAcceptCookie(false);

        browser.setWebChromeClient(
            new WebChromeClient()
        );

        browser.setWebViewClient(
            new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request
                ) {
                    String url = request.getUrl().toString();

                    // Keep navigation inside the application
                    return !url.startsWith(
                        "file:///android_asset/"
                    );
                }
            }
        );

        // Open the actual bundled HTML file
        browser.loadUrl(
            "file:///android_asset/index.html"
        );
    }

    @Override
    public void onBackPressed() {
        if (browser != null && browser.canGoBack()) {
            browser.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (browser != null) {
            browser.destroy();
            browser = null;
        }

        super.onDestroy();
    }
}
