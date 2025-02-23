package com.lizongying.mytv1


import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.lizongying.mytv1.data.Global.gson
import com.lizongying.mytv1.data.ReqSettings
import com.lizongying.mytv1.data.RespSettings
import com.lizongying.mytv1.models.TVList
import com.lizongying.mytv1.models.TVList.CACHE_FILE_NAME
import com.lizongying.mytv1.models.TVList.DEFAULT_CHANNELS_FILE
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets


class SimpleServer(private val context: Context) : NanoHTTPD(PORT) {
    private val handler = Handler(Looper.getMainLooper())

    init {
        try {
            start()
        } catch (e: IOException) {
            Log.e(TAG, "init", e)
        }
    }

    override fun serve(session: IHTTPSession): Response {
        return when (session.uri) {
            "/api/settings" -> handleSettings()
            "/api/default-channel" -> handleDefaultChannel(session)
            "/api/import-text" -> handleImportText(session)
            "/api/import-uri" -> handleImportUri(session)
            else -> handleStaticContent(session)
        }
    }

    private fun handleSettings(): Response {
        val response: String
        try {
            val file = File(context.filesDir, CACHE_FILE_NAME)
            var str = if (file.exists()) {
                file.readText()
            } else {
                ""
            }
            if (str.isEmpty()) {
                str = context.resources.openRawResource(DEFAULT_CHANNELS_FILE).bufferedReader()
                    .use { it.readText() }
            }

            val respSettings = RespSettings(
                channelUri = SP.configUrl ?: "",
                channelText = str,
                channelDefault = SP.channel,
            )
            response = gson.toJson(respSettings) ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "handleSettings", e)
            return newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR,
                MIME_PLAINTEXT,
                e.message
            )
        }
        return newFixedLengthResponse(Response.Status.OK, "application/json", response)
    }

    private fun handleDefaultChannel(session: IHTTPSession): Response {
        val response = ""
        try {
            readBody(session)?.let {
                handler.post {
                    val req = gson.fromJson(it, ReqSettings::class.java)
                    if (req.channel != null && req.channel > -1) {
                        SP.channel = req.channel
                    } else {
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "handleDefaultChannel", e)
            return newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR,
                MIME_PLAINTEXT,
                e.message
            )
        }
        return newFixedLengthResponse(Response.Status.OK, "text/plain", response)
    }

    private fun handleImportText(session: IHTTPSession): Response {
        val response = ""
        try {
            readBody(session)?.let {
                handler.post {
                    if (TVList.str2List(it)) {
                        File(context.filesDir, TVList.CACHE_FILE_NAME).writeText(it)
                        "频道导入成功".showToast()
                    } else {
                        "频道导入错误".showToast()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "handleImportText", e)
            return newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR,
                MIME_PLAINTEXT,
                e.message
            )
        }
        return newFixedLengthResponse(Response.Status.OK, "text/plain", response)
    }

    private fun handleImportUri(session: IHTTPSession): Response {
        val response = ""
        try {
            readBody(session)?.let {
                val req = gson.fromJson(it, ReqSettings::class.java)
                val uri = Uri.parse(req.uri)
                Log.i(TAG, "uri $uri")
                handler.post {
                    TVList.parseUri(uri)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "handleImportUri", e)
            return newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR,
                MIME_PLAINTEXT,
                e.message
            )
        }
        return newFixedLengthResponse(Response.Status.OK, "text/plain", response)
    }

    private fun readBody(session: IHTTPSession): String? {
        val map = HashMap<String, String>()
        session.parseBody(map)
        return map["postData"]
    }

    private fun handleStaticContent(session: IHTTPSession): Response {
        val html = loadHtmlFromResource(R.raw.index)
        return newFixedLengthResponse(Response.Status.OK, "text/html", html)
    }

    private fun loadHtmlFromResource(resourceId: Int): String {
        val inputStream = context.resources.openRawResource(resourceId)
        return inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
    }

    companion object {
        const val TAG = "SimpleServer"
        const val PORT = 34568
    }
}