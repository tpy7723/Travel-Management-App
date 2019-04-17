package com.example.pks01.bottom_navi;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nineoldandroids.view.ViewHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class RegisterActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    GoogleMap mMap;
    ImageView image;
    String image_=null;
    String category= null;
    EditText mTitle = null;
    EditText mSnippet =null;
    static Uri imageUri = null;
    PickImageHelper ViewHelper = null;
    ArrayList<String> list;
    double click_longi = 0;
    double click_lati = 0;
    FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); // 맵 준비 기다림

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // ImageButton
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHelper.selectImage(RegisterActivity.this);
            }
        });


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        ViewHelper = new PickImageHelper(timeStamp,this);
        mTitle = (EditText) findViewById(R.id.editText_title);
        mSnippet = (EditText) findViewById(R.id.editText_content);

        Spinner s = (Spinner)findViewById(R.id.spinner1);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = null;
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            imageUri = ViewHelper.getPickImageResultUri(this, data);

            if(imageUri.getPath().contains("/external/")){
                image_ = getPathFromUri(imageUri);
            }else{
                image_ = imageUri.getPath(); // 이미지 절대경로
            }

            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(90); //-360~360 // 회전 매트릭스

            Bitmap bitmap;
            Bitmap sideInversionImg = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                sideInversionImg = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageButton myImage = (ImageButton) findViewById(R.id.imageButton);
            myImage.setImageBitmap(sideInversionImg);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) { // 맵 클릭 시
        // 현재 위도와 경도에서 화면 포인트를 알려준다
        Point screenPt = mMap.getProjection().toScreenLocation(latLng);
        // 현재 화면에 찍힌 포인트로 부터 위도와 경도를 알려준다.
        final LatLng latLng2 = mMap.getProjection().fromScreenLocation(screenPt);

        click_longi = latLng2.longitude;
        click_lati = latLng2.latitude;

        mMap.clear(); // 모든 것 삭제

        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.flag);
        Bitmap myBitmap_resized = Bitmap.createScaledBitmap(myBitmap, 130, 130, true);

        LatLng location = new LatLng(click_lati, click_longi);
        mMap.addMarker(new MarkerOptions() // 마커 생성 함수
                        .position(location) // 마커 위치
                        .title(mTitle.getText().toString()) // 마커 제목
                        .draggable(true) // 꾹 누르면 이동 가능
                        .snippet(mSnippet.getText().toString()) // 부가 설명 추가
                        .icon(BitmapDescriptorFactory.fromBitmap(myBitmap_resized))
                        .alpha(0.8f) // 투명도 1은 불투명 0은 투명
        );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // 구글맵이 준비가되면 호출되는 콜백
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // 맵 타입 변경
        mMap.setOnMapClickListener(RegisterActivity.this); // 맵을 클릭시 반응하는 setOnMapClickListener 적용
        try {
            mMap.setMyLocationEnabled(true); // 내 위치 활성화
            mMap.getUiSettings().setMyLocationButtonEnabled(true); // 내 위치 버튼을 띄움
        } catch (SecurityException e) {
            Log.d("exception", e.getMessage());
        }

        try {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng myLocation = new LatLng(location.getLatitude(),
                                        location.getLongitude());
                                // 해당 위치로 이동하고 5로 줌 함
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 5));
                            }
                        }
                    });
        } catch (SecurityException e) {
            Log.d("GPSActivity", e.getMessage());
        }
    }

    public void btn_post(View view) {
        if(mTitle.getText().toString().getBytes().length <=0
                || mSnippet.getText().toString().getBytes().length <=0
                || click_lati == 0
                || click_longi == 0
                || category == null
                || imageUri == null) {  // 모든 내용입력

            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage("모든 내용을 입력해주세요")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            Dialog dialog = builder.create();
            dialog.show();

        }else{
            DB_Item db_item = new DB_Item();

            try{
                db_item.setTitle(mTitle.getText().toString());
                db_item.setSnippet(mSnippet.getText().toString());
                db_item.setLongitude(click_longi);
                db_item.setLatitude(click_lati);
                db_item.setImageStr(image_);
                db_item.setCategory(category);
                MainActivity.dbHelper.addDB_Item(db_item); // db에 아이템 추가
            }catch (Exception e){
                Log.e("error", e.getMessage());
            }

            First_Fragment.db_itemData = MainActivity.dbHelper.getAllDB_ItemData();
            First_Fragment.adapter = new ListViewAdapter(First_Fragment.db_itemData, RegisterActivity.this);
            First_Fragment.listview.setAdapter(First_Fragment.adapter); // 초기화면 초기화

            finish();
        }
    }

    public String getPathFromUri(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        cursor.close();

        return path;
    }
}
