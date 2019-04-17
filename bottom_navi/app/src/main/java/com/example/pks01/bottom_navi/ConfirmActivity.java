package com.example.pks01.bottom_navi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfirmActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    Integer zoom;
    DB_Item data;
    GoogleMap mMap;
    LatLng myLocation;
    ImageView imageView;
    FusedLocationProviderClient mFusedLocationProviderClient;
    TextView text_title, text_snippet, text_address, text_category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);


        Intent intent = getIntent();
        data = (DB_Item) intent.getSerializableExtra("OBJECT"); // 클릭한 데이터를 인텐트를 통해 받아옴

        zoom = 5;

        imageView = (ImageView) findViewById(R.id.imageView);
        text_title = (TextView) findViewById(R.id.textView_title);
        text_snippet = (TextView) findViewById(R.id.textView_content);
        text_address = (TextView) findViewById(R.id.textView_address);
        text_category = (TextView) findViewById(R.id.textView_category);

        text_title.setText(data.getTitle());
        text_snippet.setText(data.getSnippet());
        text_category.setText(data.getCategory());
        text_address.setText(getAddress(ConfirmActivity.this, data.getLatitude(), data.getLongitude()));


        final File imgFile = new File(data.getImage());

        Matrix rotateMatrix = new Matrix();
        rotateMatrix.postRotate(90); //-360~360 // 회전 매트릭스

        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getPath());
        Bitmap sideInversionImg = Bitmap.createBitmap(myBitmap, 0, 0,
                myBitmap.getWidth(), myBitmap.getHeight(), rotateMatrix, false);
        imageView.setImageBitmap(sideInversionImg);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // 맵 준비 기다림
        }

        imageView.setOnClickListener(new View.OnClickListener() { // 이미지 클릭 시 전체화면
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShowBigImage.class);
                intent.putExtra("OBJECT", imgFile.getPath());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapClick(LatLng latLng) { // 맵 클릭 시 축소
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
        zoom -= 2;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // 구글맵이 준비가되면 호출되는 콜백
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // 맵 타입 변경
        mMap.setOnMapClickListener(ConfirmActivity.this); // 맵을 클릭시 반응하는 setOnMapClickListener 적용
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { // 마커 클릭 시 확대
            @Override
            public boolean onMarkerClick(Marker marker) {  // 마커가 매개변수로 넘어옴
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
                zoom += 2;
                return false;
            }
        });

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
                                myLocation = new LatLng(data.getLatitude(),
                                        data.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));

                                Bitmap myBitmap = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.flag);
                                Bitmap myBitmap_resized = Bitmap.createScaledBitmap(myBitmap,
                                        130, 130, true);

                                mMap.addMarker(new MarkerOptions() // 마커 생성 함수
                                        .position(myLocation) // 마커 위치
                                        .icon(BitmapDescriptorFactory.fromBitmap(myBitmap_resized))
                                        .alpha(0.5f) // 투명도 1은 불투명 0은 투명
                                );
                            }
                        }
                    });
        } catch (SecurityException e) {
            Log.d("GPSActivity", e.getMessage());
        }
    }

    public void btn_share(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmActivity.this);
        builder.setMessage("공유를 하시겠습니까?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LocationTemplate params = LocationTemplate
                                .newBuilder(getAddress(ConfirmActivity.this, data.getLatitude(),
                                        data.getLongitude()),
                                        ContentObject.newBuilder(data.getTitle(),
                                                "http://mud-kage.kakao.co.kr/dn/RY8ZN/btqgOGzITp3/uCM1x2xu7GNfr7NS9QvEs0/kakaolink40_original.png",
                                                LinkObject.newBuilder()
                                                        .setWebUrl("https://developers.kakao.com")
                                                        .setMobileWebUrl("https://developers.kakao.com")
                                                        .build())
                                                .setDescrption(data.getSnippet())
                                                .build())
                                .setAddressTitle("마커 주소")
                                .build();

                        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
                        serverCallbackArgs.put("user_id", "${current_user_id}");
                        serverCallbackArgs.put("product_id", "${shared_product_id}");

                        KakaoLinkService.getInstance().sendDefault(ConfirmActivity.this, params,
                                serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                            @Override
                            public void onFailure(ErrorResult errorResult) {
                                Logger.e(errorResult.toString());
                            }

                            @Override
                            public void onSuccess(KakaoLinkResponse result) {
                            }
                        });
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    public static String getAddress(Context mContext, double lat, double lng) { //위도,경도로 주소구하기
        String nowAddress = "현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
            address = geocoder.getFromLocation(lat, lng, 1);

            if (address != null && address.size() > 0) {
                // 주소 받아오기
                nowAddress = address.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            Toast.makeText(mContext, "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return nowAddress;
    }

    public void btn_delete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmActivity.this);
        builder.setMessage("삭제를 하시겠습니까?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.dbHelper.removeDB_Item(data);

                        // listview 초기화
                        First_Fragment.db_itemData = MainActivity.dbHelper.getAllDB_ItemData();
                        First_Fragment.adapter = new ListViewAdapter(First_Fragment.db_itemData,
                                ConfirmActivity.this);
                        First_Fragment.listview.setAdapter(First_Fragment.adapter);

                        // map 마커 삭제
                        try{
                            Second_Fragment.click_marker.remove();
                        }catch (NullPointerException e){
                            Log.e("error", "null marker");
                        }

                        finish();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }
}
