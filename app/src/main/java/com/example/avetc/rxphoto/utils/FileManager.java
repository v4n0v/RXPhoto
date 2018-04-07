package com.example.avetc.rxphoto.utils;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;


public class FileManager {

   static final String TAG = "FileManager";
    private static final int QUALITY = 85;


    public static Bitmap loadBitmap(Context context, String name) {
        File file = new File(context.getApplicationContext()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES), name.toLowerCase() + ".jpg");
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    public static void saveBitmap(Bitmap bitmap, Context context, String name, Bitmap.CompressFormat format) {

        // определяю расширение файла
        try {
            String env = ".";
            switch (format) {
                case PNG:
                    env += "png";
                    break;
                case JPEG:
                    env += "jpg";
                    break;
                default:
                    format = Bitmap.CompressFormat.JPEG;
                    env += "jpg";
                    break;
            }

            File file = new File(context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), name.toLowerCase() + env);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(format, QUALITY, out);
            out.flush();
            out.close();
            Log.d(TAG, "saveBitmap: "+file.getName());
            // регистрация в фотоальбоме
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, file.getName(), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //    Toast.makeText(context, "Saved in storage as " + name.toLowerCase() + ".jpg", Toast.LENGTH_SHORT).show();
    }

    public static void deleteBitmap(Context context, String name) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), name.toLowerCase() + ".jpg");
        if (file.exists()) {
            file.delete();
            //      Toast.makeText(context, "Image deleted from storage", Toast.LENGTH_SHORT).show();
        }

    }
}
