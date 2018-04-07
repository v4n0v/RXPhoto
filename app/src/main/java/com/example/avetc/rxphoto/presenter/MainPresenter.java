package com.example.avetc.rxphoto.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.avetc.rxphoto.model.ImageModel;
import com.example.avetc.rxphoto.view.MainView;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private static final String TAG = "ImagePresenter";
    private ImageModel model;
    private Scheduler scheduler;
    private Context context;

    public MainPresenter(Scheduler scheduler, Context context) {
        this.scheduler = scheduler;
        this.context = context;
        model = new ImageModel();
    }

    public void saveImage(Bitmap bitmap) {
        model.saveImage(bitmap, context)
                .subscribeOn(Schedulers.computation())
                .observeOn(scheduler)
                .subscribe(boo -> {
                    getViewState().toast("SAVING PICTURE");
                    Log.d(TAG, "saveImage accept in " + Thread.currentThread().getName());
                });
    }

    public void convertImage(Bitmap bitmap) {
        model.convertImage(bitmap, context)
                .subscribeOn(Schedulers.newThread())
                .observeOn(scheduler)
                .subscribe(boo ->
                {
                    getViewState().toast("CONVERTING");
                    getViewState().setVisible(MainView.GET);
                    Log.d(TAG, "convertImage accept in  " + Thread.currentThread().getName());
                });
    }

    public void loadImage(Uri selectedImage) {
        model.loadImage(context, selectedImage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(scheduler)
                .subscribe((Bitmap bitmap) -> {
                    getViewState().toast("LOADING");
                    getViewState().setVisible(MainView.CONVERT);
                    getViewState().setImage(bitmap);
                    Log.d(TAG, "loadImage accept in  " + Thread.currentThread().getName());
                });
    }
}
