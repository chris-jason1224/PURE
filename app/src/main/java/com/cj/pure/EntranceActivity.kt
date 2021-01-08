package com.cj.pure

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.cj.foundation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_entrance.*

class EntranceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun resourceLayout(): Int {
       return R.layout.activity_entrance
    }

    override fun initView() {
        go_remark.setOnClickListener(this)
        go_test.setOnClickListener(this)
    }

    override fun initData() {

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.go_remark ->{
                ARouter.getInstance().build("/fun_editor/EditorActivity").navigation()
            }

            R.id.go_test -> {
                ARouter.getInstance().build("/biz_test/GlideActivity").navigation()
            }
        }
    }
}