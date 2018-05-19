package com.example.zhujia.dx_shop.Tools;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LondonEye on 2016/12/23.
 */

public class ImageService {
	//	图片质量参数
	static final float IMAGE_MAX_HEIGHT = 800f;// 这里设置高度为800f
	static final float IMAGE_MAX_WIDTH = 480f;// 这里设置宽度为480f
	static final float IMAGE_MAX_SIZE = 100;// 图片的最大Size，单位为KB
	//定义图片处理的消息类型
	public static final int HANDLE_MSG_UPLOAD_IMAGE = 9;
	public static final int HANDLE_MSG_LOAD_IMAGE = 10;
	//	图片类型
	public static final int IMAGE_TYPE_AVATAR = 7; //头像

	/***
	 * 对Bitmap进行压缩、编码、上传服务器
	 *
	 * @param ctx
	 * @param handler
	 * @param sp
	 * @param imgType
	 * @param bitmap
	 * @throws UnsupportedEncodingException
	 */
	public static void uploadImage(final Context ctx, Handler handler, SharedPreferences sp, int imgType, Bitmap bitmap) throws UnsupportedEncodingException {



	}


	/**
	 * 从服务器载入图片
	 *
	 * @param imgid
	 * @param ctx
	 * @param handler
	 * @param sp
	 */
	public static void loadImageFromAPI(String imgid, final Context ctx, Handler handler, SharedPreferences sp) {


	}

	/***
	 * 从本地载入图片，载入之前需要先判断图片的大小避免OOM错误，处理成需要的大小后返回
	 *
	 * @param srcPath 图片路径
	 * @return 处理过的图片的Bitmap
	 */
	public static Bitmap loadImgFromLocal(String srcPath) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, options);// 此时返回bm为空

		options.inJustDecodeBounds = false;
		int w = options.outWidth;
		int h = options.outHeight;
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > IMAGE_MAX_WIDTH) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (options.outWidth / IMAGE_MAX_WIDTH);
		} else if (w < h && h > IMAGE_MAX_HEIGHT) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (options.outHeight / IMAGE_MAX_HEIGHT);
		}
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);
		return bitmap;
//		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/***
	 * 从本地载入图片
	 *
	 * @param is
	 * @return
	 */
	public static Bitmap loadImgFromLocal(InputStream is) throws IOException {



		BitmapFactory.Options options = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, options);


		options.inJustDecodeBounds = false;
		int w = options.outWidth;
		int h = options.outHeight;
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > IMAGE_MAX_WIDTH) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (options.outWidth / IMAGE_MAX_WIDTH);
		} else if (w < h && h > IMAGE_MAX_HEIGHT) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (options.outHeight / IMAGE_MAX_HEIGHT);
		}
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
		return bitmap;
//		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/***
	 * 按质量压缩图片
	 *
	 * @param image
	 * @return
	 */

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;

		// 循环判断如果压缩后图片是否大于所需大小，大于则继续压缩
		while (baos.toByteArray().length / 1024 > IMAGE_MAX_SIZE) {
			baos.reset(); // 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}


	public static Bitmap loadImgFromLocal(Context context, Uri uri) {

		if (Build.VERSION.SDK_INT >=19) {
			return loadImgFromLocal(getImageAbsolutePath(context, uri));
		} else {
			try {

				return loadImgFromLocal(DXApp.getInstance().getContentResolver().openInputStream(uri));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	public static Bitmap loadImgFromLocal1(Context context, Uri uri) {

		if (Build.VERSION.SDK_INT >=19) {
			return loadImgFromLocal(getImageAbsolutePath(context, uri));
		} else {
			try {

				return loadImgFromLocal(DXApp.getInstance().getContentResolver().openInputStream(uri));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 压缩图片（质量压缩）
	 * @param bitmap
	 */
	public static File compressImage1(Bitmap bitmap, String filename) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 500) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
			baos.reset();//重置baos即清空baos
			options -= 10;//每次都减少10
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			long length = baos.toByteArray().length;
		}

		File file = new File(Environment.getExternalStorageDirectory(),filename+".png");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			try {
				fos.write(baos.toByteArray());
				fos.flush();
				fos.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		recycleBitmap(bitmap);
		return file;
	}

	public static File compressImage2(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 500) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
			baos.reset();//重置baos即清空baos
			options -= 10;//每次都减少10
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			long length = baos.toByteArray().length;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date(System.currentTimeMillis());
		String filename = format.format(date);
		File file = new File(Environment.getExternalStorageDirectory(),filename+".png");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			try {
				fos.write(baos.toByteArray());
				fos.flush();
				fos.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		recycleBitmap(bitmap);
		return file;
	}


	public static void recycleBitmap(Bitmap... bitmaps) {
		if (bitmaps==null) {
			return;
		}
		for (Bitmap bm : bitmaps) {
			if (null != bm && !bm.isRecycled()) {
				bm.recycle();
			}
		}
	}

	public static String getImageAbsolutePath(Context context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[]{split[1]};
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = {column};
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}
