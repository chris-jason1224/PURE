package com.cj.foundation.provider.compressor

import androidx.annotation.WorkerThread
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * fun-compressor对外提供图片压缩能力的约束
 */
interface IFunCompressorProvider:IProvider {

    /******单个压缩 */
    fun compress(filePath: String?, callback: CompressCallback?)

    /******批量压缩 */
    fun batchCompress(
        filePaths: List<String?>?,
        callback: BatchCompressCallback?
    )

    /**同步生成缩略图 */
    @WorkerThread
    fun genThumbnail(imagePath: String?): ByteArray?

}