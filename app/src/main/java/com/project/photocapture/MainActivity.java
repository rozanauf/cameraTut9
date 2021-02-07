package com.project.photocapture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    SurfaceView surfaceView;
    Button btnCapture;
    Camera camera;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback pictureCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView=findViewById(R.id.surfaceView);
        btnCapture=findViewById(R.id.btnCapture);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new
                    String[]{Manifest.permission.CAMERA},100);
        }
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null,null,pictureCallback);
            }
        });
        pictureCallback = new Camera.PictureCallback(){

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Bitmap bmp= BitmapFactory.decodeByteArray(data,0, data.length);
                Bitmap cbmp= Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),null,true);
                String pathFileName=currentDateFormat();
                storePhotoToStorage(cbmp,pathFileName);
                MainActivity.this.camera.startPreview();
            }
        };
    }

    private void storePhotoToStorage(Bitmap cbmp, String pathFileName) {
        String path="data/data/mmu.edu.my.tutorial9/"+pathFileName+".jpg";
        File outputFile=new File(path);
        Toast.makeText(this, "Photo stored in:"+path, Toast.LENGTH_SHORT).show();
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(outputFile);
            cbmp.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String currentDateFormat() {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String currentTime=dateFormat.format(new Date());
        return currentTime;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        camera=Camera.open();
        Camera.Parameters param;
        param=camera.getParameters();
        param.setPreviewFrameRate(30);
        camera.setParameters(param);
        camera.setDisplayOrientation(0);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera=null;
    }
}