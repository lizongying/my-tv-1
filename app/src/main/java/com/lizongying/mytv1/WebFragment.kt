package com.lizongying.mytv1

import android.graphics.Bitmap
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
import com.lizongying.mytv1.databinding.PlayerBinding
import com.lizongying.mytv1.models.TVModel


class WebFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

    private lateinit var webView: WebView

    private var tvModel: TVModel? = null

    private var _binding: PlayerBinding? = null
    private val binding get() = _binding!!

    private val scriptMap = mapOf(
        "live.kankanews.com" to R.raw.ahtv1,
        "www.cbg.cn" to R.raw.ahtv1,
        "www.sxrtv.com" to R.raw.sxrtv1,
        "www.xjtvs.com.cn" to R.raw.xjtv1,
        "www.yb983.com" to R.raw.ahtv1,
        "www.yntv.cn" to R.raw.ahtv1,
        "www.nmtv.cn" to R.raw.nmgtv1,
        "live.snrtv.com" to R.raw.ahtv1,
        "www.btzx.com.cn" to R.raw.ahtv1,
        "static.hntv.tv" to R.raw.ahtv1,
        "www.hljtv.com" to R.raw.ahtv1,
        "www.qhtb.cn" to R.raw.ahtv1,
        "www.qhbtv.com" to R.raw.ahtv1,
        "v.iqilu.com" to R.raw.ahtv1,
        "www.jlntv.cn" to R.raw.ahtv1,
        "www.cztv.com" to R.raw.ahtv1,
        "www.gzstv.com" to R.raw.ahtv1,
        "www.jxntv.cn" to R.raw.jxtv1,
        "www.hnntv.cn" to R.raw.ahtv1,
        "live.mgtv.com" to R.raw.ahtv1,
        "www.hebtv.com" to R.raw.ahtv1,
        "tc.hnntv.cn" to R.raw.ahtv1,
        "live.fjtv.net" to R.raw.ahtv1,
        "tv.gxtv.cn" to R.raw.ahtv1,
        "www.nxtv.com.cn" to R.raw.ahtv1,
        "www.ahtv.cn" to R.raw.ahtv1,
        "news.hbtv.com.cn" to R.raw.ahtv1,
        "www.sztv.com.cn" to R.raw.ahtv1,
        "www.setv.sh.cn" to R.raw.ahtv1,
        "www.gdtv.cn" to R.raw.ahtv1,
        "tv.cctv.com" to R.raw.ahtv1,
        "www.yangshipin.cn" to R.raw.ahtv1,
        "www.brtn.cn" to R.raw.xjtv1,
        "www.kangbatv.com" to R.raw.ahtv1,
        "live.jstv.com" to R.raw.xjtv1,
    )

    private var finished = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mainActivity = activity as MainActivity
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerBinding.inflate(inflater, container, false)

        webView = binding.webView

        val application = requireActivity().applicationContext as MyTVApplication

        webView.layoutParams.width = application.shouldWidthPx()
        webView.layoutParams.height = application.shouldHeightPx()

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.settings.userAgentString =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"

//        webView.settings.pluginState= WebSettings.PluginState.ON
//        webView.settings.cacheMode= WebSettings.LOAD_NO_CACHE

        webView.isClickable = false
        webView.isFocusable = false
        webView.isFocusableInTouchMode = false

//        WebView.setWebContentsDebuggingEnabled(true)
//
//        webView.setOnTouchListener { v, event ->
//            if (event != null) {
//                (activity as MainActivity).gestureDetector.onTouchEvent(event)
//            }
//            true
//        }

        (activity as MainActivity).ready(TAG)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = requireContext()
        super.onViewCreated(view, savedInstanceState)

        webView.webChromeClient = object : WebChromeClient() {
            override fun getDefaultVideoPoster(): Bitmap {
                return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                if (consoleMessage != null) {
//                    Log.e(
//                        "WebViewConsole",
//                        "Message: ${consoleMessage.message()}, Source: ${consoleMessage.sourceId()}, Line: ${consoleMessage.lineNumber()}"
//                    )

                    if (consoleMessage.message() == "success") {
                        Log.i(TAG, "${tvModel?.tv?.title} success")
                        tvModel?.tv?.finished?.let {
                            webView.evaluateJavascript(it) { res ->
                                Log.i(TAG, "${tvModel?.tv?.title} finished: $res")
                            }
                        }
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

                tvModel?.tv?.block?.let {
                    for (i in it) {
                        if (uri?.path?.contains(i) == true) {
                            Log.i(TAG, "block path ${uri.path}")
                            return WebResourceResponse("text/plain", "utf-8", null)
                        }
                    }
                }

                if (request?.isForMainFrame == false && (uri?.path?.endsWith(".jpg") == true || uri?.path?.endsWith(
                        ".jpeg"
                    ) == true || uri?.path?.endsWith(
                        ".png"
                    ) == true || uri?.path?.endsWith(
                        ".gif"
                    ) == true || uri?.path?.endsWith(
                        ".webp"
                    ) == true || uri?.path?.endsWith(
                        ".svg"
                    ) == true)
                ) {
                    return WebResourceResponse("text/plain", "utf-8", null)
                }

                return null

                if (uri?.path?.endsWith(
                        ".css"
                    ) == true
                ) {
                    return null
                }

                Log.d(TAG, "${request?.method} ${uri.toString()} ${request?.requestHeaders}")
                return null
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.i(TAG, "onPageStarted $url")
//                webView.evaluateJavascript(
//                    context.resources.openRawResource(R.raw.prev)
//                        .bufferedReader()
//                        .use { it.readText().replace("{channel}", "$url") }, null
//                )

                super.onPageStarted(view, url, favicon)

                val jsCode = """
(() => {
    const style = document.createElement('style');
    style.type = 'text/css';
    style.innerHTML = `
body {
    position: 'fixed';
    left: '100%';
    background-color: '#000';
}
img {
    display: none;
}
* {
    font-size: 0 !important;
    color: black !important;
    background-color: black !important;
    border-color: black !important;
    outline-color: black !important;
    text-shadow: none !important;
    box-shadow: none !important;
    fill: black !important;
    stroke: black !important;
    width: 0;
}
    `;
    document.head.appendChild(style);
})();
        """.trimIndent()
                webView.evaluateJavascript(jsCode, null)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (webView.progress < 100) {
                    super.onPageFinished(view, url)
                    return
                }

                finished++
                if (finished < 1) {
                    super.onPageFinished(view, url)
                    return
                }

                Log.i(TAG, "onPageFinished $finished $url")
                tvModel?.tv?.started?.let {
                    webView.evaluateJavascript(it, null)
                    Log.i(TAG, "started")
                }

                super.onPageFinished(view, url)

                tvModel?.tv?.script?.let {
                    webView.evaluateJavascript(it, null)
                    Log.i(TAG, "script")
                }

                val uri = Uri.parse(url)
                var script = scriptMap[uri.host]
                if (script == null) {
                    script = R.raw.ahtv1
                }
                var s = context.resources.openRawResource(script)
                    .bufferedReader()
                    .use { it.readText() }

                tvModel?.tv?.id?.let {
                    s = s.replace("{id}", "$it")
                }

                tvModel?.tv?.selector?.let {
                    s = s.replace("{selector}", it)
                }

                tvModel?.tv?.index?.let {
                    s = s.replace("{index}", "$it")
                }

                Log.d(TAG, "s: $s")

                webView.evaluateJavascript(s, null)
                Log.i(TAG, "default")
            }
        }
    }

    fun play(tvModel: TVModel) {
        finished = 0
        this.tvModel = tvModel
        var url = tvModel.videoUrl.value as String
        Log.i(TAG, "play ${tvModel.tv.id} ${tvModel.tv.title} $url")
//        url = "https://www.nmtv.cn/liveTv"
        webView.loadUrl(url)
    }

    companion object {
        private const val TAG = "WebFragment"
    }
}