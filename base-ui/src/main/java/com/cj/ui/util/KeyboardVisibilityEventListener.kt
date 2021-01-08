package com.cj.ui.util

/**
 * 监听键盘开启关闭接口
 */
interface KeyboardVisibilityEventListener {
        fun onVisibilityChanged(isOpen: Boolean, heightDiff: Int): Boolean
    }