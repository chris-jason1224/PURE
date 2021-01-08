package com.cj.foundation.provider.compressor;

import java.io.File;

/**
 * Created by mayikang on 2018/9/12.
 */

/**
 * 压缩结果回调接口
 */
public interface CompressCallback extends IBaseCallback{

    void onSuccess(File compressedFile);

    void onFailed(Throwable throwable);

}