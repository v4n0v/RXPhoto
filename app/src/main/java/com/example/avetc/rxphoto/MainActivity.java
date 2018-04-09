package com.example.avetc.rxphoto;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.example.avetc.rxphoto.presenter.MainPresenter;
import com.example.avetc.rxphoto.view.MainView;
import com.jakewharton.rxbinding2.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;

public class MainActivity extends MvpAppCompatActivity implements MainView {
    public static final int NUMBER_OF_REQUEST = 23401;
    static final int GALLERY_REQUEST = 1;
    private static final String TAG = "ImageActivity";
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

        // сохраняю картинку в галерею, чтоб была наверняка
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

    // обработака выбора изображения из галереи
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    Log.d(TAG, "URI = " +selectedImage);
                    presenter.loadImage(selectedImage);
                }
        }
    }

      AlertDialog dlg;
    @Override
    public void openWaitingDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater li = LayoutInflater.from(this);
        builder.setTitle("Выполняется конвертация");
        final View additionView = li.inflate(R.layout.loading_layout, null);

        builder.setView(additionView);
        builder.setCancelable(true);
        builder.setPositiveButton("Отмена", (dialog, which) -> presenter.cancelConvertation());

        dlg=builder.create();
        dlg.show();
    }

    @Override
    public void closeWaitingDialog() {
        dlg.hide();
    }

    // вывод тоста
    @Override
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // определяю видимость кнопок
    @Override
    public void setVisible(int mode) {
        switch (mode) {
            case CONVERT:
                convertButton.setVisibility(View.VISIBLE);
                break;
            case LOAD:
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
