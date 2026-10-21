package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    public static final long MAX_UPLOAD_BYTES = 1024L * 1024L;
    public static final int MAX_DIMENSION = 1280;
    public static final int JPEG_QUALITY = 80;

    private ImageUtils() {}

    public static File newPhotoFile(Context context) {
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (directory == null) {
            directory = context.getCacheDir();
        }
        if (!directory.exists() && !directory.mkdirs()) {
            directory = context.getCacheDir();
        }
        return new File(directory, "triptrail_" + System.currentTimeMillis() + ".jpg");
    }

    public static File compress(File source, int maxDimension, int quality) {
        if (source == null || !source.exists()) {
            return source;
        }

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(source.getAbsolutePath(), bounds);
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            return source;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight, maxDimension);
        Bitmap bitmap = BitmapFactory.decodeFile(source.getAbsolutePath(), options);
        if (bitmap == null) {
            return source;
        }

        bitmap = applyExifRotation(source, bitmap);
        bitmap = scaleDown(bitmap, maxDimension, quality);

        File target = new File(source.getParentFile(),
                "small_" + source.getName());
        try (FileOutputStream output = new FileOutputStream(target)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output);
        } catch (IOException e) {
            return source;
        } finally {
            bitmap.recycle();
        }

        if (target.length() > MAX_UPLOAD_BYTES && quality > 40) {
            return compress(target, maxDimension, quality - 20);
        }
        return target.length() > 0 ? target : source;
    }

    private static int calculateSampleSize(int width, int height, int maxDimension) {
        int sample = 1;
        int largest = Math.max(width, height);
        while (largest / sample > maxDimension * 2) {
            sample *= 2;
        }
        return sample;
    }

    private static Bitmap scaleDown(Bitmap bitmap, int maxDimension, int quality) {
        int largest = Math.max(bitmap.getWidth(), bitmap.getHeight());
        if (largest <= maxDimension) {
            return bitmap;
        }
        float ratio = (float) maxDimension / (float) largest;
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap,
                Math.round(bitmap.getWidth() * ratio),
                Math.round(bitmap.getHeight() * ratio), true);
        if (scaled != bitmap) {
            bitmap.recycle();
        }
        return scaled;
    }

    private static Bitmap applyExifRotation(File source, Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(source.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            int degrees;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degrees = 270;
                    break;
                default:
                    return bitmap;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (rotated != bitmap) {
                bitmap.recycle();
            }
            return rotated;
        } catch (IOException | RuntimeException e) {
            return bitmap;
        }
    }

    public static long sizeInKb(File file) {
        return file == null ? 0 : file.length() / 1024;
    }
}
