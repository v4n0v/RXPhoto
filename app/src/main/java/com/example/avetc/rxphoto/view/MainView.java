package com.example.avetc.rxphoto.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.IntDef;

import com.arellomobile.mvp.MvpView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



public interface MainView extends MvpView {
    int GET = 0;
    int CONVERT = 1;




    @IntDef({GET, CONVERT})
    @Retention(RetentionPolicy.SOURCE)

    @interface Mode{}
    void toast(String msg);
    void setVisible(@Mode int mode);
    void setImage(Bitmap bitmap);
}
