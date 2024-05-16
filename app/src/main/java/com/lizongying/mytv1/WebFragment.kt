package com.lizongying.mytv1

import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.lizongying.mytv1.models.TVModel


class WebFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

    private lateinit var webView: WebView

    private var tvModel: TVModel? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mainActivity = activity as MainActivity
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        webView = WebView(requireContext())
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.settings.userAgentString =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"

        webView.isClickable = false
        webView.isFocusable = false
        webView.isFocusableInTouchMode = false
        WebView.setWebContentsDebuggingEnabled(true)

        webView.setOnTouchListener { v, event ->
            if (event != null) {
                (activity as MainActivity).gestureDetector.onTouchEvent(event)
            }
            true
        }

        (activity as MainActivity).ready(TAG)
        return webView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = requireContext()
        super.onViewCreated(view, savedInstanceState)

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                if (consoleMessage != null) {
                    Log.e(
                        "WebViewConsole",
                        "Message: ${consoleMessage.message()}, Source: ${consoleMessage.sourceId()}, Line: ${consoleMessage.lineNumber()}"
                    )

                    if (consoleMessage.message() == "success") {
                        Log.e(TAG, "success")
                        tvModel?.setErrInfo("web ok")
                    }
                }
                return super.onConsoleMessage(consoleMessage)
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                webView: WebView?,
                handler: SslErrorHandler,
                error: SslError?
            ) {
                handler.proceed()
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val uri = request?.url
                if (request?.isForMainFrame == false && (uri?.path?.endsWith(".jpg") == true || uri?.path?.endsWith(
                        ".png"
                    ) == true || uri?.path?.endsWith(
                        ".gif"
                    ) == true || uri?.path?.endsWith(
                        ".csss"
                    ) == true)
                ) {
                    return WebResourceResponse("text/plain", "utf-8", null)
                }

                if (uri?.host?.endsWith("cctvpic.com") == true && uri.path?.endsWith(
                        ".css"
                    ) == true
                ) {
                    return WebResourceResponse("text/plain", "utf-8", null)
                }

                Log.i(TAG, "${request?.method} ${uri.toString()} ${request?.requestHeaders}")
                return null
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val uri = Uri.parse(url)
                Log.e(TAG, "uri ${uri.host}")
                when (uri.host) {
                    "tv.cctv.com" -> webView.evaluateJavascript(context.resources.openRawResource(R.raw.cctv)
                        .bufferedReader()
                        .use { it.readText() }) { value ->
                        if (value == "success") {
                            Log.e(TAG, "success")
                        }
                    }

                    "www.gdtv.cn" -> {
                        webView.evaluateJavascript(context.resources.openRawResource(R.raw.gdtv)
                            .bufferedReader()
                            .use { it.readText() }) { value ->
                            if (value == "success") {
                                Log.e(TAG, "success")
                            }
                        }
                    }

                    "www.setv.sh.cn" -> {
                        webView.evaluateJavascript(context.resources.openRawResource(R.raw.setv)
                            .bufferedReader()
                            .use { it.readText() }) { value ->
                            if (value == "success") {
                                Log.e(TAG, "success")
                            }
                        }
                    }

                    "live.kankanews.com" -> {
                        webView.evaluateJavascript(context.resources.openRawResource(R.raw.setv)
                            .bufferedReader()
                            .use { it.readText() }) { value ->
                            if (value == "success") {
                                Log.e(TAG, "success")
                            }
                        }
                    }

                    "www.yangshipin.cn" -> {
                        webView.evaluateJavascript(context.resources.openRawResource(R.raw.ysp)
                            .bufferedReader()
                            .use { it.readText() }) { value ->
                            if (value == "success") {
                                Log.e(TAG, "success")
                            }
                        }
                    }
                }
            }
        }
//        var url =  "https://www.yangshipin.cn/#/tv/home?pid=600002513"
    }

    fun play(tvModel: TVModel) {
//        if (tvModel.tv.type == Type.HLS) {
//            "暂不支持此格式".showToast(Toast.LENGTH_LONG)
//            return
//        }

        this.tvModel = tvModel
        val url = tvModel.videoUrl.value as String
        Log.i(TAG, "play ${tvModel.tv.title} $url")
        val uri = Uri.parse(url)
        Log.e(TAG, "uri ${uri.host}")
        when (uri.host) {
            "tv.cctv.com" -> webView.evaluateJavascript(
                "localStorage.setItem('cctv_live_resolution', '720');",
                null
            )

            "www.gdtv.cn" -> {}
        }
        webView.loadUrl(url)
    }

    companion object {
        private const val TAG = "WebFragment"
    }
}