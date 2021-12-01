package com.example.drawandwalk;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GpsDrawActivity extends AppCompatActivity {
    private RelativeLayout mapViewContainer;
    private TextView tvTopic, tvTimeLimit;
    private MapView mapView;
    private MapPOIItem marker = new MapPOIItem();
    private Button btnDrawStart, btnDrawEnd,btnSave;
    private Chronometer chronometer;
    private LocationManager manager;
    private Location lastLocation;
    private Double latitude, longitude;
    private int permissonCheck, c_permissonCheck;
    private MapPoint point;
    private MapPolyline polyline = new MapPolyline();
    private LocationListener locationListener;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent GPSIntent = getIntent();
        permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        c_permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        btnDrawStart = findViewById(R.id.btnDrawStart);
        btnDrawEnd = findViewById(R.id.btnDrawEnd);
        mapView = new MapView(this);
        mapViewContainer = (RelativeLayout) findViewById(R.id.mapView);
        mapViewContainer.addView(mapView);
        marker.setItemName("당신의 위치");
        marker.setTag(0);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        mapView.addPOIItem(marker);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        btnDrawStart = findViewById(R.id.btnDrawStart);
        tvTopic = findViewById(R.id.tvTopic);
        tvTimeLimit = findViewById(R.id.tvTimeLimit);
        btnSave = findViewById(R.id.btnSave);
        chronometer=findViewById(R.id.chronometer);

        Boolean timeLimit = GPSIntent.getBooleanExtra("timeLimit", false);
        tvTopic.setText("그림 주제: " + GPSIntent.getStringExtra("topic"));
        if (timeLimit == false) {
            tvTimeLimit.setText("제한시간: 없음");
        } else {
            tvTimeLimit.setText("제한시간: " + GPSIntent.getIntExtra("hour", 0) + "시간" + GPSIntent.getIntExtra("min", 0) + "분");
        }
        ArrayList<MapPoint> pointList = new ArrayList<MapPoint>();
        polyline.setLineColor(Color.argb(0, 255, 51, 0));
        if (permissonCheck != PackageManager.PERMISSION_GRANTED && c_permissonCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        } else {
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                point = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                marker.setMapPoint(point);
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
                mapView.addPolyline(polyline);
            }

            @Override
            public void onProviderDisabled(String provider) {
                showToast("GPS를 켜주세요");
                finish();
            }

            public void onProviderEnabled(String provider) {

            }
        };
        connectivityManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);//인터넷 연결 확인 위한 매니저

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);//와이파이 확인 콜백
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback);//콜백 등록
        }else{
            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
            if(networkInfo==null){
                showToast("네트워크 연결이 없어 앱을 사용할 수 없습니다.");
                finish();
            }else{
                showToast("뭐야");
            }
        }


        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);

        lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation == null) {//gps가 잡히지 않는 경우
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);
            lastLocation = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastLocation == null) {
                showToast("인터넷 연결이 없거나 GPS정보를 알 수 없는 위치입니다.");
                finish();
            } else {
                GPSServiceStart();
            }

        } else {//gps로 위치 파익이 되는 경우
            GPSServiceStart();
        }
    }
    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override public void onAvailable(Network network) {
            super.onAvailable(network);
            Log.e("onAvailable", "WI-FI 연결됨");
        }
        @Override public void onLost(Network network) {
            super.onLost(network);
            Log.e("onAvailable", "WI-FI 연결 끊김");
        }
    };

    @Override
    protected void onPause() {
        System.out.println("on pause");
        mapViewContainer.removeView(mapView);

        super.onPause();
    }

    @Override
    public void finish() {
        System.out.println("on finish");
        mapViewContainer.removeView(mapView);
        super.finish();
    }
    public void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    public void GPSServiceStart(){
        longitude=lastLocation.getLongitude();
        latitude=lastLocation.getLatitude();//처음 위치 세팅
        point=MapPoint.mapPointWithGeoCoord(latitude,longitude);//포인트
        marker.setMapPoint(point);
        mapView.addPOIItem(marker);//사용자 위치 표시하는 마커
        //polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude));
        //mapView.addPolyline(polyline);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude),true);//맵 중앙에 사용자 위치가 오도록
        btnDrawStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                polyline.setLineColor(Color.argb(128,255,51,0));
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            }
        });
        btnDrawEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                polyline.setLineColor(Color.argb(0,255,51,0));
                chronometer.stop();
            }
        });
    }

    @Override
    protected void onDestroy() {//https://gooners0304.tistory.com/entry/NetworkCallback%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%98%EC%97%AC-%EB%84%A4%ED%8A%B8%EC%9B%8C%ED%81%AC-%EC%83%81%ED%83%9C%EB%A5%BC-%EB%B0%9B%EC%95%84%EC%98%A4%EC%9E%90
        connectivityManager.unregisterNetworkCallback(networkCallback);
        chronometer.stop();
        super.onDestroy();
    }
}