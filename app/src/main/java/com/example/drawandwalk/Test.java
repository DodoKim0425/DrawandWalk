package com.example.drawandwalk;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Test extends AppCompatActivity {
    TextView tvTest;
    Button btnTest;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        AutoPermissions.Companion.loadAllPermissions(this,101);
       // tvTest=findViewById(R.id.tvTest);
       // btnTest=findViewById(R.id.btnTest);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationService();
            }
        });

    }
    public void startLocationService(){
        LocationManager manager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            long minTime=10000;//10초
            float minDistance=0;//거리
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,//10초에 한번씩 위치 갱신
                    minDistance,//이 거리만큼 움직이면 갱신
                    new LocationListener() {//위치 리스너
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            tvTest.setText("내위치:"+location.getLatitude()+","+location.getLongitude());
                        }
                    }
            );//위치정보계속 갱신
        }catch (SecurityException e){
            e.printStackTrace();
        }
        showToast("위치 요청됨");
    }
    public void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
