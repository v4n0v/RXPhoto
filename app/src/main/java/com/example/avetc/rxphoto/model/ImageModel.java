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
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;


public class ImageModel {
    private static final int QUALITY = 85;
    private final String TAG = "ImageModel";

    private Context context;

    public ImageModel(Context context) {
        this.context = context;
    }

    // папка куда сохранять, в данном случае - корень SD-карты
    private final String folderToSave = Environment.getExternalStorageDirectory().toString() + "/Pictures/";


    public Single<Boolean> saveImage(Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        return Single.fromCallable(() -> save(bitmap, name, format));
    }

    public Observable<Bitmap> loadImage(Uri path) {
        return Observable.fromCallable(() -> load(path));
    }

    //    public Completable convertImage(Bitmap bitmap, String name, Bitmap.CompressFormat format) {
    public Single<Boolean> convertImage(Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        return Single.fromCallable(() -> save(bitmap, name, format));

    }

    public Completable convertImage1(Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                Log.d(TAG, "conversion start");
                if (save(bitmap, name, format)) {
                    Thread.sleep(2000);
                    Log.d(TAG, "conversion complete");
                    e.onComplete();
                } else {
                    Log.d(TAG, "conversion error");
                    e.onError(new RuntimeException("Conversion error"));
                }
            }
        });
    }


    public void cancel() {

    }

    private Bitmap load(Uri path) {
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


    //    private boolean save(Bitmap bitmap, String name, Bitmap.CompressFormat format) {
    private boolean save(Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        File file = new File(context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), name + "." + format.name().toLowerCase());
//        File file = new File(folderToSave, name + "." + format.name());
        if (!file.exists()) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(format, QUALITY, out);
                out.flush();
                out.close();
                Log.e(TAG, "saveBitmap: " + file.getAbsolutePath() + " in " + Thread.currentThread().getName());
                // регистрация в фотоальбоме
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "File was already created");
        }
        return true;
    }


}
