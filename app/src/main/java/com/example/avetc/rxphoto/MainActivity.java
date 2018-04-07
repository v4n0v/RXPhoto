package com.example.avetc.rxphoto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.avetc.rxphoto.presenter.MainPresenter;
import com.example.avetc.rxphoto.view.MainView;
import com.jakewharton.rxbinding2.view.RxView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends MvpAppCompatActivity implements MainView {
    public static final int NUMBER_OF_REQUEST = 23401;
    static final int GALLERY_REQUEST = 1;
    @BindView(R.id.convert_button)
    Button convertButton;

    @BindView(R.id.get_button)
    Button getButton;

    @BindView(R.id.image)
    ImageView imageView;

    @InjectPresenter
    MainPresenter presenter;

    Bitmap loadedBitmap;
    @ProvidePresenter
    public MainPresenter provideMainPresenter() {
        return new MainPresenter(AndroidSchedulers.mainThread(), getApplicationContext());
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo1);

        // запрос разрешения на запись\чтение
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int canRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int canWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (canRead != PackageManager.PERMISSION_GRANTED || canWrite != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, NUMBER_OF_REQUEST);
            }
        }

        // сохраняю картинку в галрею, чтоб была наверняка
        presenter.saveImage(bitmap);

        // загрузка картинки из галереи
        RxView.clicks(getButton)
                .subscribe(aVoid -> {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                });

        // ковнвертация картинки и схранение
        RxView.clicks(convertButton)
                .subscribe(aVoid -> {
                    if (bitmap!=null) {
                        presenter.convertImage(loadedBitmap);
                    } else {
                        toast("Bitmap is not loaded");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    presenter.loadImage(selectedImage);
                }
        }
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setVisible(int mode) {
        switch (mode) {
            case CONVERT:
                convertButton.setVisibility(View.VISIBLE);
                break;
            case GET:
                convertButton.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void setImage(Bitmap bitmap) {

        imageView.setImageBitmap(bitmap);
        loadedBitmap=bitmap;
    }
}
