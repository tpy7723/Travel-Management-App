package com.example.pks01.bottom_navi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    static DBHelper dbHelper;
    final int RequestCode = 1000;
    private FloatingActionButton fab;
    FragmentTransaction fragmentTransaction1, fragmentTransaction2, fragmentTransaction3;

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1
                            .replace(R.id.fragment_container, new First_Fragment())
                            .commit();
                    return true;
                case R.id.navigation_dashboard:
                    fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2
                            .replace(R.id.fragment_container, new Second_Fragment())
                            .commit();
                    return true;
                case R.id.navigation_notifications:
                    fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3
                            .replace(R.id.fragment_container, new Third_Fragment())
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 권한 체크
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    RequestCode); // 권한 요청
        }

        dbHelper = new DBHelper(
                this,
                "myDB",
                null, 1);
        dbHelper.testDB();

        fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1
                .replace(R.id.fragment_container, new First_Fragment())
                .commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        // 이벤트 적용
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        try {
            Log.d("ApiActivity", "Key=" + getKeyHash(getApplicationContext()));
            //key hash를얻어서카카오개발자페이지에등록
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("error", e.getMessage());
        }

    }


    public String getKeyHash(final Context context) throws PackageManager.NameNotFoundException {
        //카카오API 콜을수행하려면앱별키해시를등록해야함
        //카카오API는앱에서추출한키해시를통하여악의적인앱인지아닌지를판별

        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(),
                PackageManager.GET_SIGNATURES);
        if (packageInfo == null) return null;
        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("ApiActivity", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }
}
