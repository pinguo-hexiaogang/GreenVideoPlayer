package com.xiaogang.greenplayer.utils

/**
 * 把ms时间总量转换成时间格式,分:秒；00：00
 */
fun Long?.toMiniteSeconds(): String {
    this ?: return "00:00"
    val minite = this / 1000 / 60
    val seconds = this / 1000 % 60
    return "$minite:$seconds"
}