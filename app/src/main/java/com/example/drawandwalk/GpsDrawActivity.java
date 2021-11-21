package com.example.drawandwalk;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.Map;

public class GpsDrawActivity extends AppCompatActivity {
    private RelativeLayout mapViewContainer;
    private MapView mapView;
    private MapPOIItem marker=new MapPOIItem();
    private Button btnDrawStart,btnDrawEnd;
    private LocationManager manager;
    Location lastLocation;
    private Double latitude,longitude;
    private int permissonCheck;
    private final int MY_PERMISSIONS_REQUEST_CAMERA=1001;
    private MapPoint point;
    private MapPolyline polyline=new MapPolyline();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissonCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        mapView=new MapView(this);
        mapViewContainer=(RelativeLayout) findViewById(R.id.mapView);
        mapViewContainer.addView(mapView);
        marker.setItemName("DefaultMarker");
        marker.setTag(0);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        mapView.addPOIItem(marker);
        manager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        btnDrawStart=findViewById(R.id.btnDrawStart);
        ArrayList<MapPoint> pointList=new ArrayList<MapPoint>();
        polyline.setLineColor(Color.argb(128,255,51,0));
        if(permissonCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }else{
        }
        lastLocation=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude=lastLocation.getLongitude();
        latitude=lastLocation.getLatitude();//처음 위치 세팅
        point=MapPoint.mapPointWithGeoCoord(latitude,longitude);//포인트
        marker.setMapPoint(point);
        mapView.addPOIItem(marker);
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude));
        mapView.addPolyline(polyline);

        LocationListener locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude=location.getLatitude();
                longitude=location.getLongitude();
                point=MapPoint.mapPointWithGeoCoord(latitude,longitude);
                marker.setMapPoint(point);
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude),true);
                polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude));
                mapView.addPolyline(polyline);
            }
        };


        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,1,locationListener);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude),true);


    }
    public void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
