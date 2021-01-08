package com.cj.editor

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.webkit.*
import android.webkit.WebSettings.ZoomDensity
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cj.foundation.base.BaseActivity
import com.cj.foundation.log.LOG
import com.cj.foundation.provider.compressor.CompressCallback
import com.cj.foundation.provider.compressor.IFunCompressorProvider
import com.cj.foundation.util.JSONUtils
import com.cj.foundation.util.SPUtil
import com.cj.foundation.util.uri.UriUtil
import com.cj.ui.util.ScreenUtil
import com.cj.ui.widget.dialog2.MessageDialogView
import kotlinx.android.synthetic.main.activity_editor.*
import org.apache.commons.lang3.StringEscapeUtils
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

/**
 * 富文本编辑器
 */
@Route(path = "/fun_editor/EditorActivity")
class EditorActivity : BaseActivity() {
    private val RQ_Image = 15
    private val RQ_Auth = 16
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private var jsBridge: JSBridge = JSBridge()
    private var selection: Selection? = null
    private val compressor: IFunCompressorProvider by lazy {
        ARouter.getInstance().build("/fun_compressor/provider")
            .navigation() as IFunCompressorProvider
    }

    //必备动态权限
    private var MUST_PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE
    )

    override fun resourceLayout(): Int {
        return R.layout.activity_editor
    }

    override fun initView() {

        if (!EasyPermissions.hasPermissions(this@EditorActivity, *MUST_PERMISSIONS)) {
            EasyPermissions.requestPermissions(this@EditorActivity, "", RQ_Auth, *MUST_PERMISSIONS)
        }
        iv_back.setOnClickListener(this)
        iv_save.setOnClickListener(this)
        iv_save.isEnabled = false

        initWeb()
    }

    override fun initData() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                dealBack()
            }
            R.id.iv_save -> {
                getDeltaContent()
            }
        }
    }

    private fun initWeb() {
        WebView.setWebContentsDebuggingEnabled(true)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.setAppCacheMaxSize(1024 * 1024 * 8)//存储的最大容量
        web.settings.setAppCachePath(cacheDir?.absolutePath)
        web.settings.setAppCacheEnabled(true)
        web.settings.blockNetworkImage = false// 解决图片不显示
        web.settings.defaultTextEncodingName = "utf-8"//设置编码格式
        web.settings.builtInZoomControls = false//设置是否支持缩放
        web.settings.loadWithOverviewMode = true// 页面支持缩放
        web.settings.useWideViewPort = false  //将图片调整到适合webview的大小
        web.settings.setSupportZoom(false)  //不支持缩放   
        web.settings.cacheMode = WebSettings.LOAD_NO_CACHE//不使用缓存
        web.settings.allowFileAccess = true  //设置可以访问文件
        web.settings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        web.settings.loadsImagesAutomatically = true  //支持自动加载图片


        val screenDensity = resources.displayMetrics.densityDpi

        var zoomDensity = ZoomDensity.MEDIUM
        zoomDensity = when (screenDensity) {
            DisplayMetrics.DENSITY_LOW -> ZoomDensity.CLOSE
            DisplayMetrics.DENSITY_MEDIUM -> ZoomDensity.MEDIUM
            DisplayMetrics.DENSITY_HIGH, DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_XXHIGH -> ZoomDensity.FAR
            else -> ZoomDensity.FAR
        }
        web.settings.defaultZoom = zoomDensity

        //注入方法
        web.addJavascriptInterface(jsBridge, "Android_Method")

        web.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {

                if (fileChooserParams == null) {
                    return false
                }

                getSelection()

                if (selection == null || selection?.index == null) {
                    MessageDialogView(this@EditorActivity, "温馨提示", "请先选择图片插入位置")
                    return false
                }

                fileChooserCallback = filePathCallback
                val imageList = "image/png,image/gif,image/jpeg,image/bmp,image/x-icon"
                //选择文件类型
                var types = fileChooserParams?.acceptTypes
                for (type in types) {
                    //选择图片
                    if (imageList.contains(type)) {
                        val intentToPickPic = Intent(Intent.ACTION_PICK, null)
                        intentToPickPic.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*"
                        )
                        startActivityForResult(intentToPickPic, RQ_Image)
                        return true
                    }
                }

                return false
            }
        }

        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                LOG.getInstance().e("shouldOverrideUrlLoading = ${request?.url}")
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                LOG.getInstance().e("onReceivedError = ${error.toString()}")
            }
        }

        web.loadUrl("file:///android_asset/index.html")
        //web.loadUrl("http://10.13.6.3:8080/#/")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RQ_Image) {
            //直接回调null给编辑器
            fileChooserCallback!!.onReceiveValue(arrayOf())
        }

        if (resultCode == RESULT_OK) {
            var uri: Uri? = data?.data
            if (uri != null && uri.path!!.isNotEmpty() && fileChooserCallback != null) {
                //直接返回图片给编辑器，但是无法控制删除按键
                //fileChooserCallback!!.onReceiveValue(arrayOf(uri))
                //return

                var file: File? = UriUtil.uri2File(this@EditorActivity, uri) ?: return

                //压缩图片
                compressor.compress(file!!.absolutePath, object : CompressCallback {

                    override fun onStart() {
                        Toast.makeText(this@EditorActivity, "图片压缩中...", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFinish() {

                    }

                    override fun onSuccess(compressedFile: File?) {
                        Toast.makeText(this@EditorActivity, "图片压缩完成", Toast.LENGTH_SHORT).show()

                        var options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(compressedFile!!.absolutePath, options)

                        //获取图片宽高
                        var imgWidth_px = options.outWidth
                        var imgHeight_px = options.outHeight
                        //屏幕宽度px单位
                        var screenWidh_px = ScreenUtil.getScreenWidth(this@EditorActivity)
                        var displayWidth: Int =
                            ScreenUtil.px2dip(this@EditorActivity, imgWidth_px.toFloat())
                        var displayHeight: Int =
                            ScreenUtil.px2dip(this@EditorActivity, imgHeight_px.toFloat())

                        //当图片原始宽高超宽
                        if (imgWidth_px >= (screenWidh_px - 90)) {
                            var newWidth = screenWidh_px - 90
                            var newHeight = newWidth * imgHeight_px / imgWidth_px
                            displayWidth =
                                ScreenUtil.px2dip(this@EditorActivity, newWidth.toFloat())
                            displayHeight =
                                ScreenUtil.px2dip(this@EditorActivity, newHeight.toFloat())
                        }

                        //插入图片
                        insertImg(
                            "${displayWidth}px", "${displayHeight}px", compressedFile.absolutePath
                        )

                    }

                    override fun onFailed(throwable: Throwable?) {
                        throwable?.printStackTrace()
                    }

                })

            }
        }

    }

    //拦截返回按键
    override fun onBackPressed() {
        dealBack()
    }

    private fun dealBack() {
        if (web != null) {
            if (web.canGoBack()) {
                web.goBackOrForward(-1)
            } else {
                finish()
            }
        }
    }

    /**
     * android注入js
     */
    inner class JSBridge {

        //编辑器挂载好之后回调
        @JavascriptInterface
        fun onEditorCreate() {
            runOnUiThread {
                Toast.makeText(this@EditorActivity, "编辑器初始化完成", Toast.LENGTH_SHORT).show()
            }
            //恢复编辑记录
            var content = SPUtil.getString("remark-html", "")
            if (!TextUtils.isEmpty(content)) {
                setDeltaContent(content!!)
            }
        }

        //编辑器销毁后回掉
        @JavascriptInterface
        fun onEditorDestroy() {
            runOnUiThread {
                Toast.makeText(this@EditorActivity, "编辑器已销毁", Toast.LENGTH_SHORT).show()
            }
        }

        //编辑器内容变化时回调方法
        @JavascriptInterface
        fun onEditorChange(content: String) {
            runOnUiThread(object : Runnable {
                override fun run() {
                    //LOG.getInstance().w("editor change = $content")
                    //未输入内容时，编辑器只包含一个换行符
                    if (TextUtils.isEmpty(content) || TextUtils.equals(content, "<p><br></p>")) {
                        iv_save.isEnabled = false
                        return
                    }
                    iv_save.isEnabled = true
                }
            })
        }

        //编辑器选区变化回调方法
        @JavascriptInterface
        fun onSelectionChange(range: String?, oldRange: String?, source: String?) {
            var range = JSONUtils.jsonString2JavaObject(range, Selection::class.java)
            var oldRange = JSONUtils.jsonString2JavaObject(oldRange, Selection::class.java)
            LOG.getInstance().e("onSelectionChange == $range  -  $oldRange  -  $source")
            //更新当前selection
            selection = range
        }

        //图片删除完成回调方法
        @JavascriptInterface
        fun onImgDelete(result: Boolean) {
            Toast.makeText(this@EditorActivity, "图片删除成功", Toast.LENGTH_SHORT).show()
        }

        @Deprecated("暂时未启用")
        //编辑器焦点变化时回调
        @JavascriptInterface
        fun onFocusChange(focus: Boolean) {
            LOG.getInstance().e("editor has focus = $focus")
        }

    }

    //保存输入的内容
    private fun getDeltaContent() {
        runOnUiThread {
            //获取Delta信息
            web.evaluateJavascript("javascript:getContent()") { value ->
                LOG.getInstance().e("delta = ${StringEscapeUtils.unescapeEcmaScript(value)}")
                SPUtil.saveString("remark-html", value)
                MessageDialogView(this@EditorActivity, "温馨提示", "备忘录保存成功").show()
            }
        }
    }

    //给编辑器设置内容
    private fun setDeltaContent(content: String) {
        runOnUiThread {
            val js = "javascript:setContent(${content})"
            web.evaluateJavascript(js, null)
        }
    }

    //获取输入的内容，并转换成html
    private fun getPlainHtml() {
        web.evaluateJavascript("javascript:getPlainHtml()") { value ->
            //html标签被转义过
            LOG.getInstance().e("getPlainHtml = ${StringEscapeUtils.unescapeEcmaScript(value)}")
        }
    }

    //插入图片
    private fun insertImg(width: String, height: String, path: String) {
        if (selection == null) {
            MessageDialogView(this@EditorActivity, "温馨提示", "请先选择图片插入位置")
            return
        }
        var base64 = UriUtil.imageToBase64(path)
        if (TextUtils.isEmpty(base64)) {
            Toast.makeText(this@EditorActivity, "图片信息异常", Toast.LENGTH_SHORT).show()
            return
        }
        //去除base64中换行符
        base64 = base64!!.replace("\n", "")

        //java调用js方法，传递多个参数，每个参数需要用单引号括起来
        var js = "javascript:insertImg('${selection!!.index}','${width}','${height}','${base64}')"
        web.evaluateJavascript(js, null)
    }

    //主动获取编辑器选区
    private fun getSelection() {
        web.evaluateJavascript("javascript:getSelection()", object : ValueCallback<String> {
            override fun onReceiveValue(value: String?) {
                var newSelection: Selection? = JSONUtils.jsonString2JavaObject(
                    value,
                    Selection::class.java
                )
                LOG.getInstance()
                    .e("getSelection result : selectionStr = $value selection obj = $newSelection")
                selection = newSelection
            }
        })
    }

}