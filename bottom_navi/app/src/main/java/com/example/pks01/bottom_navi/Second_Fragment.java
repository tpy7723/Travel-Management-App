package com.example.pks01.bottom_navi;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Second_Fragment extends Fragment
        implements OnMapReadyCallback {

    GoogleMap mMap;
    Context context_;
    List<DB_Item> mark;
    static Marker click_marker;
    private MapView mapView = null;
    FusedLocationProviderClient mFusedLocationProviderClient;

    public Second_Fragment() {
        // required
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        context_ = context;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_second, container, false);

        mapView = (MapView) layout.findViewById(R.id.map);
        mapView.getMapAsync(this);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // 구글맵이 준비가되면 호출되는 콜백
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // 맵 타입 변경

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { // 마커 클릭 시 반응하는 리스너
            @Override
            public boolean onMarkerClick(Marker marker) {  // 마커가 매개변수로 넘어옴
                DB_Item db;
                Integer id_ = (Integer) marker.getTag();

                click_marker = marker;
                db = MainActivity.dbHelper.get_ItemDatabyid(id_);

                Intent intent = new Intent(getContext(), ConfirmActivity.class);
                intent.putExtra("OBJECT", db);
                startActivity(intent);

                return false;
            }
        });

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

        try {
            mMap.setMyLocationEnabled(true); // 내 위치 활성화
            mMap.getUiSettings().setMyLocationButtonEnabled(true); // 내 위치 버튼을 띄움
        } catch (SecurityException e) {
            Log.d("exception", e.getMessage());
        }

        mark = First_Fragment.db_itemData; // 데이터를 담은 리스트

        for (int i = 0; i < mark.size(); i++) {
            Bitmap myBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.pin);
            Bitmap myBitmap_resized = Bitmap.createScaledBitmap(myBitmap, 130, 130, true);

            LatLng location = new LatLng(mark.get(i).getLatitude(), mark.get(i).getLongitude());
            Marker mapmarker=mMap.addMarker(new MarkerOptions() // 마커 생성 함수
                            .position(location) // 마커 위치
                            .title(mark.get(i).getTitle()) // 마커 제목
                            .draggable(false) // 꾹 누르면 이동 가능
                            .snippet(mark.get(i).getSnippet()) // 부가 설명 추가
                            .alpha(0.8f) // 투명도 1은 불투명 0은 투명
                            .icon(BitmapDescriptorFactory.fromBitmap(myBitmap_resized)) // 아이콘
                    // 아이콘 모양 변경
            );
            mapmarker.setTag(mark.get(i).getID()); // 태그 지정
        }
    }
}
