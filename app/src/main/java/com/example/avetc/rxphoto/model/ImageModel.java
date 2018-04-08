package com.example.avetc.rxphoto.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import io.reactivex.Observable;
import io.reactivex.Single;


public class ImageModel {
    private static final int QUALITY = 85;
    private final String TAG = "ImageModel";

    private Context context;
    public ImageModel(Context context) {
        this.context = context;
    }


    public Single<Boolean> saveImage(Bitmap bitmap,String name, Bitmap.CompressFormat format) {
        return Single.fromCallable(() -> save(bitmap, name, format));
    }

    public Observable<Bitmap> loadImage(Uri path) {
        return Observable.fromCallable(() -> load(path));
    }

    public Single<Boolean> convertImage(Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        return Single.fromCallable(() -> save(bitmap, name, format));
    }

    private  Bitmap load(Uri path) {
        Log.e(TAG, "load: " + path + "\nin: " + Thread.currentThread().getName());
        //создаем файл
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    private boolean save(Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        File file = new File(context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), name + "." + format.name());
        Log.d(TAG, "save: file= '" + file + "'");

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(format, QUALITY, out);
            out.flush();
            out.close();
            Log.e(TAG, "saveBitmap: " + file.getName() + " in " + Thread.currentThread().getName());
            // регистрация в фотоальбоме
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, file.getName(), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean convert(Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        return true;
    }



}
