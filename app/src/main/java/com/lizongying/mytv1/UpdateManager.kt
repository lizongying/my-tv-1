package com.lizongying.mytv1

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.lizongying.mytv1.data.Global.gson
import com.lizongying.mytv1.data.ReleaseResponse
import com.lizongying.mytv1.requests.HttpClient
import com.lizongying.mytv1.requests.HttpClient.HOST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class UpdateManager(
    private var context: Context,
    private var versionCode: Long
) :
    ConfirmationFragment.ConfirmationListener {

    private var release: ReleaseResponse? = null

    private suspend fun getRelease(): ReleaseResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val request = okhttp3.Request.Builder()
                    .url(HOST + "main/version.json")
                    .build()

                HttpClient.okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null

                    response.body?.let {
                        return@withContext gson.fromJson(it.string(), ReleaseResponse::class.java)
                    }
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "getRelease", e)
                null
            }
        }
    }

    fun checkAndUpdate() {
        Log.i(TAG, "checkAndUpdate")
        CoroutineScope(Dispatchers.Main).launch {
            var text = "版本获取失败"
            var update = false
            try {
                release = getRelease()

                Log.i(TAG, "versionCode $versionCode ${release?.version_code}")
                if (release?.version_code != null) {
                    if (release?.version_code!! >= versionCode) {
                        text = "最新版本：${release?.version_name}"
                        update = true
                    } else {
                        text = "已是最新版本，不需要更新"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error occurred: ${e.message}", e)
            }
            updateUI(text, update)
        }
    }

    private fun updateUI(text: String, update: Boolean) {
        val dialog = ConfirmationFragment(this@UpdateManager, text, update)
        dialog.show((context as FragmentActivity).supportFragmentManager, TAG)
    }

    private fun startDownload(context: Context, release: ReleaseResponse) {
        val versionName = release.version_name
        val apkName = "my-tv-1"
        val apkFileName = "$apkName-${release.version_name}.apk"
        val downloadUrl =
            "${HttpClient.DOWNLOAD_HOST}${release.version_name}/$apkName-${release.version_name}.apk"
        Log.i(TAG, "downloadUrl: $downloadUrl")
        val downloadRequest = OneTimeWorkRequestBuilder<UpdateWorker>()
            .setInputData(
                workDataOf(
                    "VERSION_NAME" to versionName,
                    "APK_FILENAME" to apkFileName,
                    "DOWNLOAD_URL" to downloadUrl
                )
            )
            .build()

        WorkManager.getInstance(context).enqueue(downloadRequest)
    }

    companion object {
        private const val TAG = "UpdateManager"
    }

    override fun onConfirm() {
        Log.i(TAG, "onConfirm $release")
        release?.let { startDownload(context, it) }
    }

    override fun onCancel() {
    }
}