package com.lizongying.mytv1.data

import java.io.Serializable

data class TV(
    var id: Int = 0,
    var name: String = "",
    var title: String = "",
    var started: String? = null,
    var script: String? = null,
    var finished: String? = null,
    var logo: String = "",
    var uris: List<String>,
    var headers: Map<String, String>? = null,
    var group: String = "",
    var block: List<String>,
) : Serializable {

    override fun toString(): String {
        return "TV{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", started='" + started + '\'' +
                ", script='" + script + '\'' +
                ", finished='" + finished + '\'' +
                ", logo='" + logo + '\'' +
                ", uris='" + uris + '\'' +
                ", headers='" + headers + '\'' +
                ", group='" + group + '\'' +
                '}'
    }
}