
package gov.android.com.superior.tools;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {
    
	public static final String APP_DIRECTORY_NAME = ".superior";
	public static final String ATTACHMENT_DIRTORY_NAME  = "attachment";
	public static final String APK_DIRCTORY_NAME = "apk";
	
	/**
	 * 通过目录名取得目录file
	 * 
	 * @param name
	 * @return
	 */
	public static File getDirctoryByName(String name) {
		if (name == null) return null;
		File dir = new File(Environment.getExternalStorageDirectory(), APP_DIRECTORY_NAME);
		if (dir == null) return null;
		//根缓存目录
		if (!dir.exists()) dir.mkdirs();
		//文件目录
		dir = new File(dir, name);
		if (dir == null) return null;
		if (!dir.exists()) dir.mkdirs();
		return dir;
	}
	
	public static File getNewAttachmentFile() {
		return new File(getDirctoryByName(ATTACHMENT_DIRTORY_NAME), System.currentTimeMillis() + ".jpg");
	}
	
	public static String getNewAttachmentFilePath() {
		return getNewAttachmentFile().getAbsolutePath();
	}
	
    /**
	 * 将bitmap保存到sdcard上
	 * 
	 * @param bmp
	 * @param filePath			文件名
	 */
	public static File bitmap2File(Bitmap bmp, String filePath) {
		File bitmapFile = new File(filePath);
		if (bitmapFile != null && bitmapFile.exists()) bitmapFile.delete();
		try {
			bitmapFile.createNewFile();
			System.out.println("destFile:" + " create New File-------------" + bitmapFile.exists());
			FileOutputStream fos = new FileOutputStream(bitmapFile);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			bmp.recycle();
			bmp = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("file: "+ bitmapFile.length());
		return bitmapFile;
	}
	
	/**
	 * 压缩图片文件
	 * @param file 图片文件
	 * @param destFilePath  压缩后的文件
	 * @return
	 */
	public static File getCompressBitmapFile(File file, String destFilePath) {
		if (file == null || !file.exists() || destFilePath == null) return null;
		File destFile = bitmap2File(getCompressBitmap(file.getAbsolutePath()), destFilePath);
		return destFile;
	}
    
  /**
   * 按比例大小压缩图片（根据路径获取图片并压缩）
   * 
   * @param srcPath 图片路径
   * @return
   */
  	public static Bitmap getCompressBitmap(String srcPath) {
  		BitmapFactory.Options newOpts = new BitmapFactory.Options();
  		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
  		newOpts.inJustDecodeBounds = true;
  		Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
  		
  		newOpts.inJustDecodeBounds = false;
  		int w = newOpts.outWidth;
  		int h = newOpts.outHeight;
  		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
  		float hh = 800f;//这里设置高度为800f
  		float ww = 480f;//这里设置宽度为480f
  		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
  		int be = 1;//be=1表示不缩放
  		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
  			be = (int) (newOpts.outWidth / ww);
  		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
  			be = (int) (newOpts.outHeight / hh);
  		}
  		if (be <= 0)
  			be = 1;
  		be = be >= 4 ? 4 : be;
  		System.out.println("inSampleSize:" + be);
  		newOpts.inSampleSize = be;//设置缩放比例
  		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
  		newOpts.inJustDecodeBounds = false;
  		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
  		return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
  	}

  	/**
  	 * 压缩图片
  	 * @param image
  	 * @return
  	 */
  	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		if (baos.size() / 1024 < 300) {
			baos.reset();
			return image;
		}
		int options = 100;
//		while (baos.toByteArray().length / 1024 > 100) {		//循环判断如果压缩后图片是否大于100kb,大于继续压缩		
//			baos.reset();//重置baos即清空baos
//			image.compress(Bitmap.CompressFormat.JPEG, options - 10, baos);//这里压缩options%，把压缩后的数据存放到baos中
//			options -= 10;//每次都减少10
//			
//			System.out.println("options:" + options);
//		}
		System.out.println("size:" + baos.size());
		System.out.println("options:" + (150.0f / (baos.size() * 1.0f / 1024) * 100 ));
		//压缩比例
		options = (int)(150.0f / (baos.size() * 1.0f / 1024) * 100); 
		System.out.println("options:" + options);
		baos.reset();
		image.compress(Bitmap.CompressFormat.JPEG, options, baos);
		System.out.println("baos size:" + baos.size());
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		try {
			baos.reset();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

  	public static int exifOrientationToDegrees(String fileName) {
		int rotation = 0;
		try {
			ExifInterface exif = new ExifInterface(fileName);
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (exifOrientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotation = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotation = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotation = 270;
					break;
				default:
					break;
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("exifOrientationToDegrees:" + rotation);
		return rotation;
	}  	
  	
  	public static File rotateBitmapFile(int degress, File bitmapFile) {
  		if (bitmapFile == null || !bitmapFile.exists()) return null;
  		if (degress == 0) return bitmapFile;
  		Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath());
  		if (bitmap == null) return null;
        //旋转图片 动作   
        Matrix matrix = new Matrix();;  
        matrix.postRotate(degress);  
        System.out.println("angle2=" + degress);  
        // 创建新的图片   
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
        bitmapFile = bitmap2File(resizedBitmap, bitmapFile.getAbsolutePath());  
        if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
        if (resizedBitmap != null && !resizedBitmap.isRecycled()) resizedBitmap.recycle();
        return bitmapFile;
    }
  	
	public static Bitmap rotateBitmap(int degress, Bitmap bitmap) {
  		if (bitmap == null || degress <= 0) return bitmap;
        //旋转图片 动作   
        Matrix matrix = new Matrix();;  
        matrix.postRotate(degress);  
        System.out.println("angle2=" + degress);  
        // 创建新的图片   
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
        if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
        return resizedBitmap;
    }
  	
  	public static File rotateCompressBitmapFile(File bitmapFile, String destFilePath) {
  		int degress = exifOrientationToDegrees(bitmapFile.getAbsolutePath());
  		if (degress > 0) {
  			return getCompressBitmapFile(bitmapFile, destFilePath);
  		} else {
  			Bitmap bitmap = getCompressBitmap(bitmapFile.getAbsolutePath());
  			Bitmap rotateBitmap = rotateBitmap(degress, bitmap);
  			File destFile = bitmap2File(rotateBitmap, destFilePath);
  			if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
  			if (rotateBitmap != null && !rotateBitmap.isRecycled()) rotateBitmap.recycle();
  			bitmap = null;
  			rotateBitmap = null;
  			return destFile;
  		}
  	}
  	
	public static File transUri(Activity context, Uri uri) {
		if (uri == null || context == null) return null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = context.managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		return img_path == null || img_path.length() == 0 ? null : new File(img_path);

	}
}
