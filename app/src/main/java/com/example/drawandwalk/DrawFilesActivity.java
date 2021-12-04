package com.example.drawandwalk;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DrawFilesActivity  extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DrawFilesAdapter adapter;
    private List<DrawFiles>filesNameList;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_recyclerview);
        filesNameList=new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.myDrawing_recycler);
        adapter=new DrawFilesAdapter(getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/DrawAndWalk";
        File directory=new File(path);
        File[]files=directory.listFiles();
        for(int i=0;i<files.length;i++){
            String filename=files[i].getName();
            filesNameList.add(new DrawFiles(filename));
        }
        setDrawFileList();
    }
    public void setDrawFileList(){
        if(filesNameList.size()==0){
            Toast.makeText(this,"작품이 없습니다.",Toast.LENGTH_LONG).show();
        }else{
            for(int i=0;i<filesNameList.size();i++){
                adapter.setMyFiles(filesNameList.get(i));
            }
            recyclerView.setAdapter(adapter);
        }
    }
}
