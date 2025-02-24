package com.lizongying.mytv1.data

import java.io.Serializable

data class TV(
    var id: Int = 0,
    var name: String = "",
    var title: String = "",
    var script: String? = null,
    var logo: String = "",
    var uris: List<String>,
    var headers: Map<String, String>? = null,
    var group: String = "",
    var type: Type = Type.WEB,
) : Serializable {

    override fun toString(): String {
        return "TV{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", script='" + script + '\'' +
                ", logo='" + logo + '\'' +
                ", uris='" + uris + '\'' +
                ", headers='" + headers + '\'' +
                ", group='" + group + '\'' +
                ", type='" + type + '\'' +
                '}'
    }
}