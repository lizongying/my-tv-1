package com.lizongying.mytv1

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.lizongying.mytv1.data.Global.gson
import com.lizongying.mytv1.data.ReleaseResponse
import com.lizongying.mytv1.requests.HttpClient
import com.lizongying.mytv1.requests.HttpClient.HOST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class UpdateManager(
    private var context: Context,
    private var versionCode: Long
) :
    ConfirmationFragment.ConfirmationListener {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

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

        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    downloadAndInstall(context, downloadUrl, apkFileName, versionName!!)
                }
                showNotification(context)
            } catch (e: Exception) {
                Log.i(TAG, "downloadAndInstallApk", e)
            }
        }
    }

    private fun showNotification(context: Context) {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = NotificationCompat.Builder(context, "download_channel").apply {
            setSmallIcon(android.R.drawable.stat_sys_download)
            setContentTitle("Downloading Update")
            setContentText("Download in progress")
            priority = NotificationCompat.PRIORITY_LOW
            setOngoing(true)
            setProgress(100, 0, false)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "download_channel",
                "Download Updates",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun downloadAndInstall(
        context: Context,
        downloadUrl: String,
        apkFileName: String,
        versionName: String
    ) {
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.mkdirs()
        Log.i(TAG, "save dir ${Environment.DIRECTORY_DOWNLOADS}")
        val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
            setTitle("${context.resources.getString(R.string.app_name)} $versionName Downloading")
            setDescription("Downloading the latest version of the app")
            setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, apkFileName)

            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            setAllowedOverRoaming(false)
            setMimeType("application/vnd.android.package-archive")
        }

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        var downloading = true
        while (downloading) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = manager.query(query)
            if (cursor.moveToFirst()) {
                val bytesDownloaded =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytesTotal =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                if (bytesTotal > 0) {
                    val progress = (bytesDownloaded * 100L / bytesTotal).toInt()
                    notificationBuilder.setProgress(100, progress, false)
                    notificationManager.notify(1, notificationBuilder.build())
                }

                when (cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> downloading = false
                    DownloadManager.STATUS_FAILED -> {
                        downloading = false
                        throw Exception("Download failed")
                    }
                }
            }
            cursor.close()
        }

        notificationBuilder.setContentText("Download complete")
            .setProgress(0, 0, false)
            .setOngoing(false)
        notificationManager.notify(1, notificationBuilder.build())

        val apkFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            apkFileName
        )

        if (apkFile.exists()) {
            val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context, context.packageName + ".fileprovider", apkFile)
                    .apply {
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
            } else {
                Uri.parse("file://${apkFile.absolutePath}")
//                Uri.fromFile(apkFile)
            }
//            val apkUri = Uri.parse("file://$apkFile")
            Log.i(TAG, "apkUri $apkUri")
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(installIntent)
        } else {
            Log.e(TAG, "APK file does not exist!")
        }
    }

    override fun onConfirm() {
        Log.i(TAG, "onConfirm $release")
        release?.let { startDownload(context, it) }
    }

    override fun onCancel() {
    }


    companion object {
        private const val TAG = "UpdateManager"
    }
}