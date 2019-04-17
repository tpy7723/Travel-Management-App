package com.example.pks01.bottom_navi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ShowBigImage extends AppCompatActivity {

    String data;
    ImageView imageView;
    Bitmap myBitmap, sideInversionImg;
    Matrix rotateMatrix, sideInversion, updownInversion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_image);

        imageView = (ImageView) findViewById(R.id.bigimage);

        Intent intent = getIntent();
        data = (String) intent.getStringExtra("OBJECT"); // 인텐트 값을 받아옴

        //좌우반전 이미지 효과 및 Bitmap 만들기
        sideInversion = new Matrix();
        sideInversion.setScale(-1, 1);

        //상하반전 이미지 효과 및 Bitmap 만들기
        updownInversion = new Matrix();
        updownInversion.setScale(1, -1);

        rotateMatrix = new Matrix();
        rotateMatrix.postRotate(90); //-360~360 // 회전 매트릭스

        myBitmap = BitmapFactory.decodeFile(data);
        sideInversionImg = Bitmap.createBitmap(myBitmap, 0, 0,
                myBitmap.getWidth(), myBitmap.getHeight(), rotateMatrix, false);
        imageView.setImageBitmap(sideInversionImg);

    }

    public void rotate_btn(View view) {
        sideInversionImg = Bitmap.createBitmap(sideInversionImg, 0, 0,
                sideInversionImg.getWidth(), sideInversionImg.getHeight(), rotateMatrix, false);
        imageView.setImageBitmap(sideInversionImg);
    }

    public void side_btn(View view) {
        sideInversionImg = Bitmap.createBitmap(sideInversionImg, 0, 0,
                sideInversionImg.getWidth(), sideInversionImg.getHeight(), sideInversion, false);
        imageView.setImageBitmap(sideInversionImg);
    }

    public void updown_btn(View view) {
        sideInversionImg = Bitmap.createBitmap(sideInversionImg, 0, 0,
                sideInversionImg.getWidth(), sideInversionImg.getHeight(), updownInversion, false);
        imageView.setImageBitmap(sideInversionImg);
    }
}
