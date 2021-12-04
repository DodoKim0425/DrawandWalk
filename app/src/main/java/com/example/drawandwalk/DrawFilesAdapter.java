package com.example.drawandwalk;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DrawFilesAdapter extends RecyclerView.Adapter <DrawFilesViewHolder>{
    private ArrayList<DrawFiles> myFiles=null;
    private Context mcontext;
    public DrawFilesAdapter(Context context){
        myFiles=new ArrayList<>();
        this.mcontext=context;
    }
    @NonNull
    @Override
    public DrawFilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=(LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.draw_items,parent,false);
        DrawFilesViewHolder viewHolder=new DrawFilesViewHolder(context,view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DrawFilesViewHolder holder, int position) {
        DrawFiles item=myFiles.get(position);
        String filename=item.getFilename();
        holder.tvFile.setText(filename);
        holder.tvFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context=v.getContext();
                //해당 그림 불러오는 새로운 창
                Intent intent=new Intent(v.getContext(),ShowDraw.class);
                ArrayList<DrawLocation> loc=getLoc(filename);
                intent.putExtra("loc",loc);
                v.getContext().startActivity(intent);
            }
        });
    }
    public ArrayList<DrawLocation> getLoc(String filename){
        ArrayList<DrawLocation> myDrawPoints=new ArrayList<>();
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/DrawAndWalk"+"/"+filename;
        try {
            String line=null;
            BufferedReader buf=new BufferedReader(new FileReader(path));
            while((line=buf.readLine())!=null){
                String[] Point=line.split(",");
                Double latitude=Double.parseDouble(Point[0]);
                Double longitude=Double.parseDouble(Point[1]);
                myDrawPoints.add(new DrawLocation(latitude,longitude));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myDrawPoints;
    }
    @Override
    public int getItemCount() {
        return myFiles.size();
    }
    public void setMyFiles(DrawFiles data){
        myFiles.add(data);
    }
}
