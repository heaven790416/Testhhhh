package com.sinyi.test.uploadimagetest;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapHelper {
	private static class Size {
		int sample;
		float scale;
	}

	private int rotation = 0;
	private Bitmap scaled;

	BitmapHelper(Resources resources, int resId, int targetSize, boolean needResize) throws IOException {
		Size size = getRoughSize(resources, resId, targetSize);
		roughScaleImage(resources, resId, size);

		if (needResize == true) {
			scaleImage(targetSize);
		}
	}

	BitmapHelper(File file, int targetSize, boolean needResize) throws IOException {
		rotation = getImageRotation(file.getPath());

		FileInputStream is = null;

		try {
			is = new FileInputStream(file);
			Size size = getRoughSize(is, targetSize);
			try {
				is = new FileInputStream(file);
				roughScaleImage(is, size);

				if (needResize == true) {
					scaleImage(targetSize);
				}
			} finally {
				is.close();
			}
		} finally {
			is.close();
		}
	}

	public Bitmap getScaledBitmap() {
		return scaled;
	}

	private void scaleImage(int targetSize) {
		int width = scaled.getWidth();
		int height = scaled.getHeight();

		float scale = (float) Math.sqrt((double) targetSize / (double) (width * height));

		Matrix matrix = new Matrix();
		matrix.preRotate(rotation);
		matrix.postScale(scale, scale);

		scaled = Bitmap.createBitmap(scaled, 0, 0, width, height, matrix, true);
	}

	private void roughScaleImage(FileInputStream is, Size size) {
		Matrix matrix = new Matrix();
		matrix.postScale(size.scale, size.scale);

		BitmapFactory.Options scaledOpts = getBitmapOptions(size.sample);
		try {
			scaled = BitmapFactory.decodeFileDescriptor(is.getFD(), null, scaledOpts);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void roughScaleImage(Resources resources, int resId, Size size) {
		Matrix matrix = new Matrix();
		matrix.postScale(size.scale, size.scale);

		BitmapFactory.Options scaledOpts = getBitmapOptions(size.sample);
		scaled = BitmapFactory.decodeResource(resources, resId, scaledOpts);
	}

	private Size getRoughSize(InputStream is, int targetSize) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, o);

		Size size = getRoughSize(o.outWidth, o.outHeight, targetSize);
		return size;
	}

	private Size getRoughSize(Resources resources, int resId, int targetSize) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, resId, o);

		Size size = getRoughSize(o.outWidth, o.outHeight, targetSize);
		return size;
	}

	private Size getRoughSize(int outWidth, int outHeight, int targetSize) {
		Size size = new Size();
		size.scale = (float) Math.sqrt(outWidth * outHeight / targetSize);
		size.sample = 1;

		int width = outWidth;
		int height = outHeight;

		int newHeight = (int) (outHeight / size.scale);
		int newWidth = (int) (outWidth / size.scale);

		while (true) {
			if (width / 2 < newWidth || height / 2 < newHeight) {
				break;
			}
			width /= 2;
			height /= 2;
			size.sample *= 2;
		}

		return size;
	}

	/* Helper Methods */
	/* Image Compression */
	public static BitmapFactory.Options getBitmapOptions(int inSampleSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inDither = false; // Disable Dithering Mode
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inSampleSize = inSampleSize;
			// options.inPreferQualityOverSpeed = false;
			options.inTempStorage = new byte[16 * 1024];
			try {
				BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return options;
	}

	public static int getImageRotation(String imageFilePath) {
		int rotation = 0;

		try {
			ExifInterface exif = new ExifInterface(imageFilePath);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotation = 90;
				break;

			case ExifInterface.ORIENTATION_ROTATE_180:
				rotation = 180;
				break;

			case ExifInterface.ORIENTATION_ROTATE_270:
				rotation = 270;
				break;
			}
		} catch (Exception e) {

		}

		return rotation;
	}

	public static void saveBitmap(Bitmap bitmap, String filePath, Bitmap.CompressFormat format) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
			bitmap.compress(format, 85, fileOutputStream);
		} catch (Exception e) {
			Log.e("BitmapHelper", "SAVE BITMAP EXCEPTION: " + e.toString());
		}
	}

	public static Bitmap getBitmap(String filePath, int targetSizeOrInSampleSize) {
		return getBitmap(filePath, targetSizeOrInSampleSize, true);
	}

	public static Bitmap getBitmap(String filePath, int targetSizeOrInSampleSize, boolean needResize) {
		try {
			if (targetSizeOrInSampleSize < 10) {
				// this is inSamepleSizeMode
				return BitmapFactory.decodeFile(filePath, getBitmapOptions(targetSizeOrInSampleSize));
			} else {
				// this is targetSize Mode
				return (new BitmapHelper(new File(filePath), targetSizeOrInSampleSize, needResize)).getScaledBitmap();
			}
		} catch (Exception e) {
			Log.e("BitmapHelper", "GET BITMAP EXCEPTION: " + e.toString());
			return null;
		}
	}

	public static Bitmap generateThumbnail(Bitmap originalBitmap) {
		try {
			Bitmap downScaledBitmap = ThumbnailUtils.extractThumbnail(originalBitmap, 150, 150);
			return downScaledBitmap;
		} catch (Exception e) {

			return null;
		}
	}

	public static Bitmap getRotatedBitmap(Bitmap originalBitmap, int rotation) {
		if (rotation == 0) {
			return originalBitmap;
		}

		Matrix matrix = new Matrix();
		matrix.preRotate(rotation);

		Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);

		return rotatedBitmap;
	}

	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// "RECREATE" THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}

	public static Bitmap resizeToSquare(Bitmap sourceBitmap) {
		int width = sourceBitmap.getWidth();
		int height = sourceBitmap.getHeight();

		int targetWidth = 0;
		int targetHeight = 0;
		int begin_X = 0;
		int begin_Y = 0;

		float aspectRatio = 1.0f;

		if ((float) width / (float) height >= aspectRatio) {
			targetHeight = height;
			targetWidth = (int) ((float) targetHeight * aspectRatio);
			begin_X = (width - targetWidth) / 2;
		} else {
			targetWidth = width;
			targetHeight = (int) ((float) targetWidth / aspectRatio);
			begin_Y = (height - targetHeight) / 2;
		}

		return Bitmap.createBitmap(sourceBitmap, begin_X, begin_Y, targetWidth, targetHeight);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPx) {
		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, (float) roundPx, (float) roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
		} catch (Exception e) {
			return null;
		}
	}

	// public static Bitmap getBitmapResource(Resources resources, int
	// resourceID) {
	// try {
	// String key = String.valueOf(resourceID);
	//
	// Bitmap b = Singleton.getCachedBitmap(key);
	//
	// if(b==null) {
	// b = BitmapFactory.decodeResource(resources, resourceID);
	// Singleton.putCachedBitmap(key, b);
	// }
	//
	// return b;
	// } catch(Exception e) {
	// return null;
	// }
	// }
}