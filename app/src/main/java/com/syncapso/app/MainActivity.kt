package com.syncapso.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private val aiBackendUrl = "https://syncapso.onrender.com"

    private val storage by lazy {
        getSharedPreferences("syncapso_storage", MODE_PRIVATE)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true

        webView.webViewClient = WebViewClient()

        webView.addJavascriptInterface(AppBridge(), "AndroidApp")

        webView.settings.userAgentString =
            webView.settings.userAgentString + " SYNCAPSO-Android"

        webView.loadUrl("file:///android_asset/syncapso.html")
    }

    inner class AppBridge {

        @JavascriptInterface
        fun getAiBackendUrl(): String = aiBackendUrl

        @JavascriptInterface
        fun getStorage(key: String): String {
            return storage.getString(key, "") ?: ""
        }

        @JavascriptInterface
        fun setStorage(key: String, value: String) {
            storage.edit().putString(key, value).commit()
        }

        @JavascriptInterface
        fun removeStorage(key: String) {
            storage.edit().remove(key).commit()
        }

        @JavascriptInterface
        fun showMessage(message: String) {
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onPause() {
        webView.evaluateJavascript(
            "window.syncapsoPersist && window.syncapsoPersist();",
            null
        )
        super.onPause()
    }

    override fun onDestroy() {
        webView.evaluateJavascript(
            "window.syncapsoPersist && window.syncapsoPersist();",
            null
        )
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
