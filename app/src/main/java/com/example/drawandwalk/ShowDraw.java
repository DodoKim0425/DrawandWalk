package com.example.drawandwalk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import java.util.ArrayList;

public class ShowDraw extends AppCompatActivity {
    private RelativeLayout mapViewContainer;
    private MapView mapView;
    private ArrayList<DrawLocation> myDrawPoints;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_draw);
        Intent drawintent=getIntent();
        myDrawPoints=new ArrayList<>();
        mapView = new MapView(this);
        mapViewContainer = (RelativeLayout) findViewById(R.id.showDrawMapView);
        mapViewContainer.addView(mapView);
        myDrawPoints=(ArrayList<DrawLocation>)drawintent.getSerializableExtra("loc");
        MapPolyline polyline=new MapPolyline();
        polyline.setLineColor(Color.argb(255,255,51,0));
        for(int i=0;i<myDrawPoints.size();i++){
            polyline.addPoint(MapPoint.mapPointWithGeoCoord(myDrawPoints.get(i).getLatitude(),myDrawPoints.get(i).getLongitude()));
        }
        mapView.addPolyline(polyline);
        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
        int padding = 100; // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));

    }
}
