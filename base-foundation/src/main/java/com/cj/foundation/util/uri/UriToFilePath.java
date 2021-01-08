package com.cj.foundation.util.uri;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: xiaohaibin.
 * @time: 2019/11/5
 * @mail:xhb_199409@163.com
 * @github:https://github.com/xiaohaibin
 * @describe: Uri工具类
 */
public class UriToFilePath {

    private UriToFilePath() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Uri to file.
     *
     * @param uri The uri.
     * @return file
     */
    public static File uri2File(Context context,@NonNull final Uri uri) {

        String authority = uri.getAuthority();
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            String path = uri.getPath();
            if (path != null) {
                return new File(path);
            }
            Log.e("UriUtils", uri.toString() + " parse failed. -> 0");
            return null;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return new File(Environment.getExternalStorageDirectory() + "/" + split[1]);
                }
                Log.e("UriUtils", uri.toString() + " parse failed. -> 1");
                return null;
            } else if ("com.android.providers.downloads.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                if (docId.startsWith("raw:")) {
                    final String path = docId.replaceFirst("raw:", "");
                    return new File(path);
                }

                if (docId.startsWith("msf:")) {
                    String[] contentUriPrefixesToTry = new String[]{
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads"
                    };

                    for (String contentUriPrefix : contentUriPrefixesToTry) {
                        Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(docId.split(":")[1]));
                        try {
                            File f = getFileFromUri(context,contentUri, 2);
                            if (f != null && !TextUtils.isEmpty(f.getAbsolutePath())) {
                                return f;
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    return null;
                }

                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId)
                );
                return getFileFromUri(context,contentUri, 2);
            } else if ("com.android.providers.media.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    Log.d("UriUtils", uri.toString() + " parse failed. -> 3");
                    return null;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getFileFromUri(context,contentUri, selection, selectionArgs, 4);
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                return getFileFromUri(context,uri, 5);
            } else {
                Log.e("UriUtils", uri.toString() + " parse failed. -> 6");
                return null;
            }
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            return getFileFromUri(context,uri, 7);
        } else {
            Log.e("UriUtils", uri.toString() + " parse failed. -> 8");
            return null;
        }
    }

    private static File getFileFromUri(Context context,final Uri uri, final int code) {
        try {
            return getFileFromUri(context,uri, null, null, code);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static File getFileFromUri(Context context,final Uri uri,
                                       final String selection,
                                       final String[] selectionArgs,
                                       final int code) {
        final Cursor cursor = context.getContentResolver().query(
                uri, new String[]{"_data"}, selection, selectionArgs, null);
        if (cursor == null) {
            Log.e("UriUtils", uri.toString() + " parse failed(cursor is null). -> " + code);
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndexOrThrow("_data");
                if (columnIndex > -1) {
                    return new File(cursor.getString(columnIndex));
                } else {
                    Log.e("UriUtils", uri.toString() + " parse failed(columnIndex: " + columnIndex + " is wrong). -> " + code);
                    return null;
                }
            } else {
                Log.e("UriUtils", uri.toString() + " parse failed(moveToFirst return false). -> " + code);
                return null;
            }
        } catch (Exception e) {
            Log.e("UriUtils", uri.toString() + " parse failed. -> " + code);
            //垃圾华为，要特殊处理
            if (isHuaweiUri(uri) && !TextUtils.isEmpty(uri.getPath())) {
                String rootPre = File.separator + "root";
                String path = uri.getPath().startsWith(rootPre) ? uri.getPath().replace(rootPre, "") : uri.getPath();
                Log.i("UriUtils", "===》huawei：path=" + path);
                return new File(path);
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isHuaweiUri(Uri uri) {
        return "com.huawei.hidisk.fileprovider".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean copyStream(InputStream is, String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return false;
                }
            }
            FileOutputStream fos = new FileOutputStream(file);
            write(is, fos);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    public static void write(InputStream is, OutputStream os) {
        byte[] buffer = new byte[8192];
        try {
            while (is.available() > 0) {
                int n = is.read(buffer);
                os.write(buffer, 0, n);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                throw e;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
