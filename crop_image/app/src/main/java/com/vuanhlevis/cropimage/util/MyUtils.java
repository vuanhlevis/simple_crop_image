package com.vuanhlevis.cropimage.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;

public class MyUtils {

    public static void rotateImage(String oldPathFile, String newPathFile, boolean isDeleteOrigin) {
        if (oldPathFile.equals(newPathFile)) {
            isDeleteOrigin = false;
        }

        try {
            ExifInterface exif = new ExifInterface(oldPathFile);
            String TAG_ORIENTATION = exif.getAttribute("Orientation");
            Matrix matrix = new Matrix();
            if (Integer.parseInt(TAG_ORIENTATION) == 6) {
                matrix.postRotate(90.0F);
            } else if (Integer.parseInt(TAG_ORIENTATION) == 3) {
                matrix.postRotate(180.0F);
            } else if (Integer.parseInt(TAG_ORIENTATION) == 8) {
                matrix.postRotate(270.0F);
            }

            if (!TAG_ORIENTATION.equals("0")) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(oldPathFile, options);
                Bitmap realImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                File file = new File(newPathFile);
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                realImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                realImage.recycle();
            }

            if (isDeleteOrigin) {
                (new File(oldPathFile)).delete();
            }

        } catch (Exception var11) {
            var11.printStackTrace();
        }

    }
}
