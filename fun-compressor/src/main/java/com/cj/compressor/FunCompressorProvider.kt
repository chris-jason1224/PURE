package com.cj.compressor

import android.content.Context
import androidx.annotation.WorkerThread
import com.alibaba.android.arouter.facade.annotation.Route
import com.cj.compressor.core.EasyCompressor
import com.cj.foundation.provider.compressor.BatchCompressCallback
import com.cj.foundation.provider.compressor.CompressCallback
import com.cj.foundation.provider.compressor.IFunCompressorProvider

/**
 * fun-compressor对外提供图片压缩能力的实现类
 */
@Route(path = "/fun_compressor/provider")
class FunCompressorProvider : IFunCompressorProvider {

    override fun compress(filePath: String?, callback: CompressCallback?) {
        EasyCompressor.getInstance(null).compress(filePath!!,callback!!)
    }

    override fun batchCompress(filePaths: List<String?>?, callback: BatchCompressCallback?) {
        EasyCompressor.getInstance(null).batchCompress(filePaths!!,callback!!)
    }

    @WorkerThread
    override fun genThumbnail(imagePath: String?): ByteArray? {
       return EasyCompressor.getInstance(null).genThumbnail(imagePath!!)
    }

    override fun init(context: Context?) {
    }
}