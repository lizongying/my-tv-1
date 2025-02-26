package com.lizongying.mytv1.models

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lizongying.mytv1.R
import com.lizongying.mytv1.SP
import com.lizongying.mytv1.Utils.getDateFormat
import com.lizongying.mytv1.data.Global.gson
import com.lizongying.mytv1.data.Global.typeTvList
import com.lizongying.mytv1.data.TV
import com.lizongying.mytv1.showToast
import io.github.lizongying.Gua
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object TVList {
    private const val TAG = "TVList"
    const val CACHE_FILE_NAME = "channels.txt"
    val DEFAULT_CHANNELS_FILE = R.raw.channels
    private lateinit var appDirectory: File
    private lateinit var serverUrl: String
    private lateinit var list: List<TV>
    var listModel: List<TVModel> = listOf()
    val groupModel = TVGroupModel()

    private var timeFormat = if (SP.displaySeconds) "HH:mm:ss" else "HH:mm"

    fun setDisplaySeconds(displaySeconds: Boolean) {
        timeFormat = if (displaySeconds) "HH:mm:ss" else "HH:mm"
        SP.displaySeconds = displaySeconds
    }

    fun getTime(): String {
        return getDateFormat(timeFormat)
    }

    private val _position = MutableLiveData<Int>()
    val position: LiveData<Int>
        get() = _position

    fun init(context: Context) {
        _position.value = 0

        groupModel.addTVListModel(TVListModel("我的收藏", 0))
        groupModel.addTVListModel(TVListModel("全部频道", 1))

        appDirectory = context.filesDir
        val file = File(appDirectory, CACHE_FILE_NAME)
        val str = if (file.exists()) {
            Log.i(TAG, "read $file")
            file.readText()
        } else {
            Log.i(TAG, "read resource")
            context.resources.openRawResource(DEFAULT_CHANNELS_FILE).bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
        }

        try {
            str2List(str)
        } catch (e: Exception) {
            Log.e("", "error $e")
            file.deleteOnExit()
            Toast.makeText(context, "读取频道失败，请在菜单中进行设置", Toast.LENGTH_LONG).show()
        }

        if (SP.configAutoLoad && !SP.configUrl.isNullOrEmpty()) {
            SP.configUrl?.let {
                update(it)
            }
        }
    }

    private fun update() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.i(TAG, "request $serverUrl")
                val client = okhttp3.OkHttpClient()
                val request = okhttp3.Request.Builder().url(serverUrl).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val file = File(appDirectory, CACHE_FILE_NAME)
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    response.body?.let {
                        val str = it.string()
                        withContext(Dispatchers.Main) {
                            if (str2List(str)) {
                                file.writeText(str)
                                SP.configUrl = serverUrl
                                "频道导入成功".showToast()
                            } else {
                                "频道导入错误".showToast()
                            }
                        }
                    }
                } else {
                    Log.e("", "request status ${response.code}")
                    "频道状态错误".showToast()
                }
            } catch (e: Exception) {
                Log.e("", "request error $e")
                "频道请求错误".showToast()
            }
        }
    }

    private fun update(serverUrl: String) {
        this.serverUrl = serverUrl
        update()
    }

    fun parseUri(uri: Uri) {
        if (uri.scheme == "file") {
            val file = uri.toFile()
            Log.i(TAG, "file $file")
            val str = if (file.exists()) {
                Log.i(TAG, "read $file")
                file.readText()
            } else {
                "文件不存在".showToast(Toast.LENGTH_LONG)
                return
            }

            try {
                if (str2List(str)) {
                    SP.configUrl = uri.toString()
                    "频道导入成功".showToast(Toast.LENGTH_LONG)
                } else {
                    "频道导入失败".showToast(Toast.LENGTH_LONG)
                }
            } catch (e: Exception) {
                Log.e("", "error $e")
                file.deleteOnExit()
                "读取频道失败".showToast(Toast.LENGTH_LONG)
            }
        } else {
            update(uri.toString())
        }
    }

    fun str2List(str: String): Boolean {
        var string = str
        val g = Gua()
        if (g.verify(str)) {
            string = g.decode(str)
        }
        if (string.isBlank()) {
            return false
        }
        when (string[0]) {
            '[' -> {
                try {
                    list = gson.fromJson(string, typeTvList)
                    Log.i(TAG, "导入频道 ${list.size}")
                } catch (e: Exception) {
                    Log.i(TAG, "parse error $string")
                    Log.i(TAG, e.message, e)
                    return false
                }
            }
        }

        groupModel.clear()

        val map: MutableMap<String, MutableList<TVModel>> = mutableMapOf()
        for (v in list) {
            if (v.group !in map) {
                map[v.group] = mutableListOf()
            }
            map[v.group]?.add(TVModel(v))
        }

        val listModelNew: MutableList<TVModel> = mutableListOf()
        var groupIndex = 2
        var id = 0
        for ((k, v) in map) {
            val tvListModel = TVListModel(k, groupIndex)
            for ((listIndex, v1) in v.withIndex()) {
                v1.tv.id = id
                v1.groupIndex = groupIndex
                v1.listIndex = listIndex
                tvListModel.addTVModel(v1)
                listModelNew.add(v1)
                id++
            }
            groupModel.addTVListModel(tvListModel)
            groupIndex++
        }

        listModel = listModelNew

        // 全部频道
        groupModel.getTVListModel(1)?.setTVListModel(listModel)

        Log.i(TAG, "groupModel ${groupModel.size()}")
        groupModel.setChange()

        return true
    }

    fun getTVModel(): TVModel? {
        return getTVModel(position.value!!)
    }

    fun getTVModel(idx: Int): TVModel? {
        if (idx >= size()) {
            return null
        }
        return listModel[idx]
    }

    fun setPosition(position: Int): Boolean {
        Log.i(TAG, "setPosition $position/${size()}")
        if (position >= size()) {
            return false
        }

        if (_position.value != position) {
            _position.value = position
        }

        val tvModel = getTVModel(position)

        // set a new position or retry when position same
        tvModel!!.setReady()

        groupModel.setPosition(tvModel.groupIndex)

        SP.positionGroup = tvModel.groupIndex
        SP.position = position
        return true
    }

    fun size(): Int {
        return listModel.size
    }
}