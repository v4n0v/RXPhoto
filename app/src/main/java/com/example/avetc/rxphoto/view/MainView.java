package com.example.avetc.rxphoto.view;


import android.graphics.Bitmap;
import android.support.annotation.IntDef;

import com.arellomobile.mvp.MvpView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



public interface MainView extends MvpView {
    int LOAD = 0;
    int CONVERT = 1;

    void openWaitingDialog();

    void closeWaitingDialog();

    @IntDef({LOAD, CONVERT})
    @Retention(RetentionPolicy.SOURCE)

    @interface Mode{}
    void toast(String msg);
    void setVisible(@Mode int mode);
    void setImage(Bitmap bitmap);
}
