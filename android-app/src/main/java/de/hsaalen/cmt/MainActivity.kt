package de.hsaalen.cmt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import androidx.webkit.WebViewClientCompat

/**
 * Main activity that provides a web view for providing the web content.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Simplify getting the web view element.
     */
    private val webView
        get() = findViewById<WebView>(R.id.webView)

    /**
     * Path to assets. Note that the appassets.androidplatform.net host is the
     * Android default for local assets access.
     */
    private val endpointWebAsserts = "https://appassets.androidplatform.net/assets/www/index.html"

    fun isLocalWebAsset(url: String): Boolean {
        return url.startsWith(endpointWebAsserts)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set top status-bar color of the window
        window.statusBarColor = Theme.LIGHT.primaryColor.argb

        // Assert loader to load local web content
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", AssetsPathHandler(this))
            .build()


        // Webclient providing correct handlers for web content
        webView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ) = if (isLocalWebAsset(request.url.toString())) {
                false
            } else {
                val i = Intent(Intent.ACTION_VIEW, request.url)
                startActivity(i)
                true
            }
        }

        // Open local web page
        try {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(endpointWebAsserts)
        } catch (ex: Throwable) {
            Log.e("APP-DEBUG", ex.stackTraceToString())
        }
    }

}
