package com.first.orient.base.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author: harryjoker
 * Created on: 2020-01-09 13:13
 * Description:
 */
public class BitmapUtils {

    /**
     *  将bitmap写入sdcard：默认为AppName的文件夹
     * @param context
     * @param bitmap
     * @return
     */
    public static File saveImageToExternal(Context context, Bitmap bitmap) {
        if (context == null || bitmap == null) return null;
        try {
            File file = null;
            String fileName = System.currentTimeMillis() + ".png";
            File root = new File(Environment.getExternalStorageDirectory(), context.getString(context.getApplicationInfo().labelRes));
            File dir = new File(root, "images");
            if (dir.isDirectory() || dir.mkdirs()) {
                file = new File(dir, fileName);
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将图片写进相册
     * @param bitmap
     */
    public static boolean saveImageToGallery(final Context context, Bitmap bitmap) {

        File file = saveImageToExternal(context, bitmap);
        if (file == null || !file.exists()) return false;

        try {
            //其次把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), "");

            // 通知图库更新
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                mediaScanIntent.setData(uri);
                                context.sendBroadcast(mediaScanIntent);
                            }
                        });
            } else {
                String relationDir = file.getParent();
                File file1 = new File(relationDir);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}
