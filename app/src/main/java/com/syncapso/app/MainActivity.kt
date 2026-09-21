package com.syncapso.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    // Set this to the HTTPS URL where the included backend is deployed.
    // Example: https://api.your-domain.com
    private val aiBackendUrl = "https://syncapso.onrender.com"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.webViewClient = WebViewClient()

        webView.addJavascriptInterface(AppBridge(), "AndroidApp")
        webView.loadUrl("file:///android_asset/syncapso.html")

        webView.settings.userAgentString =
            webView.settings.userAgentString + " SYNCAPSO-Android"
    }

    inner class AppBridge {
        @JavascriptInterface
        fun getAiBackendUrl(): String = aiBackendUrl

        @JavascriptInterface
        fun showMessage(message: String) {
            runOnUiThread {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}
