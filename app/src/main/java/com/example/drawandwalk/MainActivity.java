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
    private int permissionCheck,c_permissionCheck,s_permissionCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        permissionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        c_permissionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        s_permissionCheck=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck==PackageManager.PERMISSION_GRANTED&&c_permissionCheck==PackageManager.PERMISSION_GRANTED&&s_permissionCheck==PackageManager.PERMISSION_GRANTED){//이미 모든 권한 승인 된 경우
            startFirstActivity();//다음으로
        }
        onCheckPermission();//권한 확인
    }
    public void onCheckPermission(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this,"앱 실행을 위해 권한 설정이 필요합니다.",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST);
            }else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&&
                        grantResults[1]==PackageManager.PERMISSION_GRANTED&&
                        grantResults[2]==PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 설정완료", Toast.LENGTH_SHORT).show();
                    startFirstActivity();

                } else {
                    Toast.makeText(this, "권한 취소됨", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder shutDownDialog=new AlertDialog.Builder(this);
                    shutDownDialog.setMessage("권한을 승인하지 않으면 본 애플리케이션을 이용할 수 없습니다. 애플리케이션을 종료합니다.");
                    shutDownDialog.setTitle("권한 승인 필요");
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
    public void startFirstActivity(){
        Intent intent=new Intent(getApplicationContext(),FirstActivity.class);
        startActivity(intent);
        finish();
    }
}