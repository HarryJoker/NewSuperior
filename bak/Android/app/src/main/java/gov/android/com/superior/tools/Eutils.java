package gov.android.com.superior.tools;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** 
 * @name: Eutils 
 *
 * @date 2014-5-1 上午10:50:20 
 * 
 * @author Huare(王华)
 *
 * @copyright Huare wanghua870626@gmail.com Copyright.
 *
 * @desc: 
 * 
 */
public class Eutils {

	public static Bundle map2Bundle(Map<String, ?> map) {
		if (map == null) return null;
		Bundle bundle = new Bundle();
		for (Entry<String, ?> entry : map.entrySet()) {
			Object object = entry.getValue();
			bundle.putString(entry.getKey(), object == null ? "" : object + "");
		}
		return bundle;
	}
	
	public static Map<String, Object> bundle2Map(Bundle bundle) {
		if (bundle == null) return null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (String key : bundle.keySet()) {
			Object object = bundle.get(key);
			map.put(key, object == null ? "" : object);
		}
		return map;
	}

	public static String listToString(List list, char separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i < list.size() - 1) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	/**
	 * 从URI获取本地路径
	 *
	 * @param activity
	 * @param contentUri
	 * @return
	 */
	public static  String getAbsoluteImagePath(Activity activity, Uri contentUri) {

		//如果是对媒体文件，在android开机的时候回去扫描，然后把路径添加到数据库中。
		//由打印的contentUri可以看到：2种结构。正常的是：content://那么这种就要去数据库读取path。
		//另外一种是Uri是 file:///那么这种是 Uri.fromFile(File file);得到的
		System.out.println(contentUri);

		String[] projection = { MediaStore.Images.Media.DATA };
		String urlpath;
		CursorLoader loader = new CursorLoader(activity,contentUri, projection, null, null, null);
		Cursor cursor = loader.loadInBackground();
		try {
			int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			urlpath =cursor.getString(column_index);
			//如果是正常的查询到数据库。然后返回结构
			return urlpath;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		//如果是文件。Uri.fromFile(File file)生成的uri。那么下面这个方法可以得到结果
		urlpath = contentUri.getPath();
		return urlpath;
	}
}
