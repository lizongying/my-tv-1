package com.lizongying.mytv1

import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import com.lizongying.mytv1.requests.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    const val TAG = "Utils"

    private var between: Long = 0

    fun getDateFormat(format: String): String {
        return SimpleDateFormat(
            format,
            Locale.CHINA
        ).format(Date(System.currentTimeMillis() - between))
    }

    fun getDateTimestamp(): Long {
        return (System.currentTimeMillis() - between) / 1000
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentTimeMillis = getTimestampFromServer()
                Log.i(TAG, "currentTimeMillis $currentTimeMillis")
                if (currentTimeMillis > 0) {
                    between = System.currentTimeMillis() - currentTimeMillis
                }
            } catch (e: Exception) {
                Log.e(TAG, "init", e)
            }
        }
    }

    private suspend fun getTimestampFromServer(): Long {
        return withContext(Dispatchers.IO) {
            try {
                val request = okhttp3.Request.Builder()
                    .url("https://ip.ddnspod.com/timestamp")
                    .build()

                HttpClient.okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext 0
                    response.body?.string()?.toLong() ?: 0
                }
            } catch (e: Exception) {
                Log.e(TAG, "getTimestampFromServer", e)
                0
            }
        }
    }

    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics
        ).toInt()
    }

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), Resources.getSystem().displayMetrics
        ).toInt()
    }

    fun formatUrl(url: String): String {
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file://") || url.startsWith(
                "socks://"
            ) || url.startsWith("socks5://")
        ) {
            return url
        }

        if (url.startsWith("//")) {
            return "http:$url"
        }

        return "http://$url"
    }
}