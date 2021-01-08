package com.cj.foundation.log.config

//日志配置项
data class LogConfig @JvmOverloads constructor(
    val autoClean: Boolean = false,//是否启动自动清理日志
    val keepDays: Int = -1,//日志保存周期，到期后自动删除
    val maxSize: Int = -1//日志文件最大为多少M，超过后自动删除
)

