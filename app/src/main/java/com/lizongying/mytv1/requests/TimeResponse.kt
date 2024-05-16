package com.lizongying.mytv1.requests


data class TimeResponse(
    val data: Time
) {
    data class Time(
        val t: String
    )
}