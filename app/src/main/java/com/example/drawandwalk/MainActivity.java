package com.example.drawandwalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    private int nCurrentPermission=0;
    private static final int PERMISSION_REQUEST=0x0000001;
    private int permissionCheck,c_permissionCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle("Walk and Draw");
        onCheckPermission();
        permissionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        c_permissionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionCheck==PackageManager.PERMISSION_GRANTED&&c_permissionCheck==PackageManager.PERMISSION_GRANTED){
            startSelectActivity();
        }
    }
    public void onCheckPermission(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this,"앱 실행을 위해 권한 설정이 필요합니다.",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_REQUEST);
            }else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 설정완료", Toast.LENGTH_SHORT).show();
                    startSelectActivity();

                } else {
                    Toast.makeText(this, "권한 취소됨", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder shutDownDialog=new AlertDialog.Builder(this);
                    shutDownDialog.setMessage("GPS 권한을 승인하지 않으면 본 애플리케이션을 이용할 수 없습니다. 애플리케이션을 종료합니다.");
                    shutDownDialog.setTitle("GPS권한 승인 필요");
                    shutDownDialog.setCancelable(false);
                    shutDownDialog.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    shutDownDialog.show();
                }
                break;
        }
    }
    public void startSelectActivity(){
        Intent intent=new Intent(getApplicationContext(),SelectActivity.class);
        startActivity(intent);
        finish();
    }
}