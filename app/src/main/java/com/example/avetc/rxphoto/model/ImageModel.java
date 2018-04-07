package com.example.avetc.rxphoto.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.example.avetc.rxphoto.utils.FileManager;
import io.reactivex.Single;


public class ImageModel {
    private final String TAG = "ImageModel";

    public Single<Boolean> saveImage(Bitmap bitmap, Context context) {

        return Single.fromCallable(() ->
        {
            // проверяю есть ли картинка в памяти устройства
            Bitmap img = FileManager.loadBitmap(context, "super_image");
            if (img!=null) {
                // если нет, сохраняю в память, если есть использхую имеющуюся
                FileManager.deleteBitmap(context, "super_image");
            }
            FileManager.saveBitmap(bitmap, context, "super_image", Bitmap.CompressFormat.JPEG);
            Log.d(TAG, "Сохраняю в галлерею  ( " + Thread.currentThread().getName()+")");
            return true;
        });
    }

    public  Single<Boolean> convertImage(Bitmap bitmap, Context context){
        return Single.fromCallable(() ->
        {
            FileManager.saveBitmap(bitmap, context, "super_image", Bitmap.CompressFormat.PNG);
            Log.d(TAG, "Сконвертировано в галлерею  ( " + Thread.currentThread().getName()+")");
            return true;
        });
    }


    public Single<Bitmap> loadImage(Context context, Uri selectedImage) {
        Log.d(TAG, "Загружаю из галлереи (" + Thread.currentThread().getName()+")");
        return Single.fromCallable(()-> MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage));
    }
}
