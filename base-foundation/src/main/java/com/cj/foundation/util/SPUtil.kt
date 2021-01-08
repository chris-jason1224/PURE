package com.cj.foundation.util

import android.content.Context
import com.tencent.mmkv.MMKV

/**
 * SharedPreferences替代
 */
object SPUtil {

    @Volatile
    private var context: Context? = null

    private lateinit var mmkv: MMKV

    /**
     * called first
     */
    fun init(context: Context) {

        if (this.context != null) {
            throw RuntimeException("Class SPUtil can be initialize once !!")
        }

        this.context = context
        MMKV.initialize(this.context)
        mmkv = MMKV.mmkvWithID("PURE_MMKV_MUL_P", MMKV.MULTI_PROCESS_MODE);
    }


    /**
     * 支持以下 Java 语言基础类型：boolean、int、long、float、double、byte[]
     * 支持以下 Java 类和容器：String、Set<String>
     */
    fun saveBoolean(key: String?, value: Boolean) {
        mmkv.encode(key, value)
    }

    fun saveInt(key: String?, value: Int) {
        mmkv.encode(key, value)
    }

    fun saveLong(key: String?, value: Long) {
        mmkv.encode(key, value)
    }

    fun saveFloat(key: String?, value: Float) {
        mmkv.encode(key, value)
    }

    fun saveDouble(key: String?, value: Double) {
        mmkv.encode(key, value)
    }

    fun saveBytes(key: String?, value: ByteArray?) {
        mmkv.encode(key, value)
    }

    fun saveString(key: String?, value: String?) {
        mmkv.encode(key, value)
    }

    fun saveStringSet(
        key: String?,
        value: Set<String?>?
    ) {
        mmkv.encode(key, value)
    }


    fun getBoolean(key: String?, defaultVal: Boolean): Boolean {
        return mmkv.decodeBool(key, defaultVal)
    }

    fun getInt(key: String?, defaultVal: Int): Int {
        return mmkv.decodeInt(key, defaultVal)
    }

    fun getLong(key: String?, defaultVal: Long): Long {
        return mmkv.decodeLong(key, defaultVal)
    }

    fun getFloat(key: String?, defaultVal: Float): Float {
        return mmkv.decodeFloat(key, defaultVal)
    }

    fun getDouble(key: String?, defaultVal: Double): Double {
        return mmkv.decodeDouble(key, defaultVal)
    }

    fun getBytes(key: String?): ByteArray? {
        return mmkv.decodeBytes(key)
    }

    fun getString(key: String?, defaultVal: String?): String? {
        return mmkv.decodeString(key, defaultVal)
    }

    fun getStringSet(key: String?): Set<String?>? {
        return mmkv.decodeStringSet(key)
    }


    fun removeOne(key: String?) {
        mmkv.remove(key)
    }


}