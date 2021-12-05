package com.example.drawandwalk;

import android.Manifest;
import android.content.Context;
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
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GpsDrawActivity extends AppCompatActivity {
    private RelativeLayout mapViewContainer;
    private TextView tvTopic, tvTimeLimit,tvEndDraw,tvHourLimit,tvMinLimit,tvSecondLimit;
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
    private LinearLayout linearDraw;
    private ConnectivityManager connectivityManager;
    private ArrayList<DrawLocation> locations;
    private boolean started=false;
    private Intent GPSIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gpsdraw_activity);
        GPSIntent = getIntent();
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
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        btnDrawStart = findViewById(R.id.btnDrawStart);
        tvTopic = findViewById(R.id.tvTopic);
        tvTimeLimit = findViewById(R.id.tvTimeLimit);
        tvHourLimit=findViewById(R.id.tvHourLimit);
        tvMinLimit=findViewById(R.id.tvMinLimit);
        tvSecondLimit=findViewById(R.id.tvSecondLimit);
        btnSave = findViewById(R.id.btnSave);
        chronometer=findViewById(R.id.chronometer);
        linearDraw=findViewById(R.id.linearDrawing);
        tvEndDraw=findViewById(R.id.tvEndDraw);
        locations=new ArrayList<DrawLocation>();

        Boolean timeLimit = GPSIntent.getBooleanExtra("timeLimit", false);
        tvTopic.setText("그림 주제: " + GPSIntent.getStringExtra("topic"));//주제세팅
        if (timeLimit == false) {//제한시간 세팅
            tvTimeLimit.setText("제한시간: 없음");
            tvHourLimit.setVisibility(View.GONE);
            tvMinLimit.setVisibility(View.GONE);
            tvSecondLimit.setVisibility(View.GONE);
        } else {
            tvMinLimit.setText(GPSIntent.getIntExtra("min",0)+" 분 ");
            tvHourLimit.setText(GPSIntent.getIntExtra("hour",0)+ "시간 ");
            tvSecondLimit.setText("0 초");
        }

        if (permissonCheck != PackageManager.PERMISSION_GRANTED && c_permissonCheck != PackageManager.PERMISSION_GRANTED) {//위치 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        } else {
        }
        locationListener = new LocationListener() {//사용자 이동 감지 리스너
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                point = MapPoint.mapPointWithGeoCoord(latitude, longitude);//지도 위의 한 위치
                marker.setMapPoint(point);//해당 위치로 마커 설정, 사용자 위치 알림
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);//사용자 현 위치로 중앙을 설정

                if(started==true){//그림그리기 시작된 경우에만 지도와 저장 파일에 점을 추가한다.
                    polyline.addPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));//사용자 위치 추가
                    mapView.addPolyline(polyline);
                    locations.add(new DrawLocation(latitude,longitude));
                }
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
            }
        }
        showToast("GPS 위치 파악이 정확해질 때까지 시작하지 말고 기다려주세요..");

        if(manager.isProviderEnabled(manager.GPS_PROVIDER)==true){//gps사용 가능한 경우
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
        }else{//아닌경우 네트워크로 위치 파악
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, locationListener);
        }

        lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//gps로 위치 잡는것을 우선시한다
        if (lastLocation == null) {//앱 설치 후 최초 실행시 gps가 잡히지 않는 경우
            lastLocation = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastLocation == null) {
                showToast("인터넷 연결이 없거나 GPS정보를 알 수 없는 위치입니다.");
                finish();
            } else {
                GPSServiceStart();
            }

        } else {//마지막 GPS위치 파악이 가능한 경우
            GPSServiceStart();
        }
    }
    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {//네트워크 연결 확인
        @Override public void onAvailable(Network network) {
            super.onAvailable(network);
            Log.e("onAvailable", "WI-FI 연결됨");
        }
        @Override public void onLost(Network network) {
            super.onLost(network);
            Log.e("onAvailable", "WI-FI 연결 끊김");
            showToast("네트워크 연결이 없어 앱을 사용할 수 없습니다.");
            finish();
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
    public void showToast(String message){//토스트 메시지 메서드
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    public void GPSServiceStart(){//그림그리기위한 세팅 완료 이후
        longitude=lastLocation.getLongitude();
        latitude=lastLocation.getLatitude();//처음 위치 세팅
        point=MapPoint.mapPointWithGeoCoord(latitude,longitude);//포인트
        marker.setMapPoint(point);
        mapView.addPOIItem(marker);//사용자 위치 표시하는 마커
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude),true);//맵 중앙에 사용자 위치가 오도록
        btnDrawStart.setOnClickListener(new View.OnClickListener() {//시작 버튼 클릭
            @Override
            public void onClick(View v) {
                started=true;
                btnDrawEnd.setVisibility(View.VISIBLE);
                btnDrawStart.setVisibility(View.GONE);
                polyline.setLineColor(Color.argb(128,255,51,0));
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                if(GPSIntent.getBooleanExtra("timeLimit",false)==true){//제한시간 기능
                    countDown();
                }
            }
        });
        btnDrawEnd.setOnClickListener(new View.OnClickListener() {//그림그리기 종료
            @Override
            public void onClick(View v) {
                started=false;
                btnSave.setVisibility(View.VISIBLE);
                btnDrawEnd.setVisibility(View.GONE);
                chronometer.stop();
                manager.removeUpdates(locationListener);//리스너 제거, 제거하지 않으면 배터리 소모 문제있음
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {//저장 버튼 클릭시
            @Override
            public void onClick(View v) {
                MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
                int padding = 100; // px
                mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));//그림전체가 보이게 이동

                long mNow=System.currentTimeMillis();//현재 시각
                Date mDate=new Date(mNow);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
                String nowTime=simpleDateFormat.format(mDate);

                String filename="DrawAndWalk_"+nowTime+".txt";//저장되는 파일명

                saveDrawing(filename);

                btnSave.setVisibility(View.GONE);
                tvEndDraw.setVisibility(View.VISIBLE);
            }
        });
    }
    public void saveDrawing(String filename){//저장
        try{
            String strFolderPath=Environment.getExternalStorageDirectory()+"/DrawAndWalk";
            File folder=new File(strFolderPath);
            if(!folder.exists()){//저장할 폴더인 DrawAndWalk가 없는 경우
                folder.mkdirs();//폴더를 만듦
            }
            File file=new File(folder+"/"+filename);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter writer;
            writer=new FileWriter(file,true);
            for(int i=0;i<locations.size();i++){
                writer.write(locations.get(i).getLatitude()+","+locations.get(i).getLongitude()+"\n");
            }
            showToast("저장 완료");
            writer.flush();
            writer.close();
        }catch (Exception e){
            showToast("그림을 저장하지 못했습니다..");
        }
    }
    @Override
    protected void onDestroy() {
        connectivityManager.unregisterNetworkCallback(networkCallback);
        chronometer.stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mapViewContainer.removeView(mapView);
        Intent intent=new Intent(getApplicationContext(),FirstActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public void countDown(){
        long conversionTime=GPSIntent.getIntExtra("hour",0)*1000*60*60+GPSIntent.getIntExtra("min",0)*1000*60;
        new CountDownTimer(conversionTime,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                if(started==true){//그림그리기 종료된 이후로는 측정하지 않음
                    String hour=String.valueOf(millisUntilFinished/(60*60*1000));//hour
                    long getMin=millisUntilFinished%(60*60*1000);//hour제외 남은시간
                    String min=String.valueOf(getMin/(60*1000));//남은 시간에서 분 구함
                    String second=String.valueOf((getMin%(60*1000))/1000);//분 제외 초는 나머지로 구함
                    tvHourLimit.setText(hour+" 시간 ");
                    tvMinLimit.setText(min+" 분 ");
                    tvSecondLimit.setText(second+" 초");
                }
            }
            @Override
            public void onFinish() {
                if(started==true){//타이머 끝나기 전에 그림그리기 종료되었으면 알림 보내지 않음
                    showToast("제한 시간 종료!!");
                }
            }
        }.start();
    }
}