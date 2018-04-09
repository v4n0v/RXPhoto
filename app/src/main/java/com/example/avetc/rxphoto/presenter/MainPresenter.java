package com.example.avetc.rxphoto.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.avetc.rxphoto.model.ImageModel;
import com.example.avetc.rxphoto.view.MainView;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.sleep;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private static final String TAG = "ImagePresenter";
    private ImageModel model;
    private Scheduler scheduler;
    private Context context;

    public MainPresenter(Scheduler scheduler, Context context) {
        this.scheduler = scheduler;
        this.context = context;
        model = new ImageModel(context);
    }


    public void saveImage(Bitmap bitmap) {
        model.saveImage(bitmap, "super_image", Bitmap.CompressFormat.JPEG)
                .subscribeOn(Schedulers.newThread())
                .observeOn(scheduler)
                .subscribe(boo -> {
                    getViewState().toast("SAVED");
                    Log.d(TAG, "saveImage accept in " + Thread.currentThread().getName());
                });
    }

    public void convertImage(Bitmap bitmap) {
        model.convertImage1(bitmap, "super_image", Bitmap.CompressFormat.PNG)
                .subscribeOn(Schedulers.newThread())
                .observeOn(scheduler)
                .subscribe(new CompletableObserver() {
                               @Override
                               public void onSubscribe(Disposable d) {
                                   getViewState().openWaitingDialog();
                                   Log.d(TAG, "onSubscribe");
                               }

                               @Override
                               public void onComplete() {
                                   Log.d(TAG, "onComplete");
                                   getViewState().closeWaitingDialog();
                                   getViewState().toast("CONVERSION COMPLETE");
                               }

                               @Override
                               public void onError(Throwable e) {
                                   Log.d(TAG, "onError "+e.getMessage());
                                   getViewState().closeWaitingDialog();
                                   getViewState().toast("CONVERSION ERROR");
                               }
                           }
                );
    }

    public void loadImage(Uri selectedImage) {
        model.loadImage(selectedImage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(scheduler)
                .subscribe((Bitmap bitmap) -> {
                    if (bitmap != null) {
                        getViewState().toast("LOADED");
                        getViewState().setVisible(MainView.CONVERT);
                        getViewState().setImage(bitmap);
                        Log.d(TAG, "loadImage accept in  " + Thread.currentThread().getName());
                    }
                });
    }


    public void cancelConvertation() {
        model.cancel();
    }
}
