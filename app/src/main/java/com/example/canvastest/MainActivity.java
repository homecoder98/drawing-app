package com.example.canvastest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import petrov.kristiyan.colorpicker.ColorPicker;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "MainActivity";
    private static int GET_GALLERY_IMAGE = 200;
    LinearLayout container,settingContainer;
    ConstraintLayout constraint;
    Button saveBtn,loadBtn, colorBtn, backBtn,screenBtn,eraserBtn;
    TextView sizeText;
    SeekBar sizeBar;
    MyView myView;
    ColorPicker colorPicker;
    private boolean isWideScreen = false;
    private long backPressedTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("권한이 부족합니다")
                .setDeniedMessage("권한이 필요합니다")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

        //인플레이션
        container = findViewById(R.id.container);
        settingContainer = findViewById(R.id.settingContainer);
        constraint = findViewById(R.id.constraint);
        saveBtn = findViewById(R.id.saveBtn);
        loadBtn = findViewById(R.id.loadBtn);
        colorBtn = findViewById(R.id.colorBtn);
        eraserBtn = findViewById(R.id.eraserBtn);
        backBtn = findViewById(R.id.backBtn);
        sizeText = findViewById(R.id.sizeText);
        sizeBar = findViewById(R.id.sizeBar);
        screenBtn = findViewById(R.id.screenBtn);

        //캔버스 인플레이션 + addview
        myView = new MyView(this);
        constraint.addView(myView);


        //사진 갤러리 저장 버튼 리스너
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = myView.getCanvasBitmap();
                if (isExternalStorageWritable()) {
                    saveImage(bitmap);
                }
            }
        });
        //사진 불러오기 버튼 리스너
        loadBtn.setOnClickListener(onClickListener);

        //색상 선택 버튼 리스너
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myView.isEraser)myView.isEraser = false;
                colorPicker = new ColorPicker(MainActivity.this);
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        myView.color = color;
                    }

                    @Override
                    public void onCancel() {
                        // put code
                    }
                });
                colorPicker.show();
            }
        });
        //지우개 버튼 리스너
        eraserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myView.color = Color.WHITE;
                myView.isEraser = true;
            }
        });
        //한 휙 지우기 버튼 리스너
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myView.pathList.size() > 0) {
                    myView.pathList.remove(myView.pathList.size() - 1);
                    myView.paintList.remove(myView.paintList.size() - 1);
                    myView.invalidate();
                }
            }
        });
        //붓 사이즈 조절 시크바 리스너
        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sizeText.setText("굵기: " + i);
                myView.size = (float) i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //전체화면 버튼 클릭 리스너
        screenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isWideScreen){
                    settingContainer.setVisibility(View.INVISIBLE);
                    screenBtn.setBackgroundResource(R.drawable.ic_baseline_fullscreen_exit_24);
                    isWideScreen = true;
                }else{
                    settingContainer.setVisibility(View.VISIBLE);
                    screenBtn.setBackgroundResource(R.drawable.ic_baseline_fullscreen_24);
                    isWideScreen = false;
                }
            }
        });


    }
    //onCreate 끝
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.loadBtn:
                    loadImage();
                    break;
                default:
                    break;
            }
        }
    };
    //이미지 불러오기
    public void loadImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
        intent.setDataAndType(uri, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
//                InputStream in = getContentResolver().openInputStream(data.getData());
//                Bitmap img = BitmapFactory.decodeStream(in);
//                in.close();
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                myView.drawCanvas();
//                Canvas canvas = new Canvas(bitmap);
//                myView.getPicture(canvas);
            } catch (IOException e) {
                Log.d("test","에러는 : "+e.toString());
            }
        }
    }

    //sd카드 저장 가능 여부 확인
    public Boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }
    //canvas > bitmap > sd카드에 저장 + 갤러리 등록
    public void saveImage(Bitmap bitmap){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = timeStamp + ".jpg";

        File file1 = new File(Environment.getExternalStorageDirectory()+"");

        try{
            File file = new File(file1,fileName);
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
            //갤러리 비트맵 저장
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, timeStamp, "from canvas test app");
            os.flush();
            Toast.makeText(getApplicationContext(),"갤러리에 저장되었습니다",Toast.LENGTH_SHORT).show();
            os.close();
        }catch(Exception e){
            Log.d(TAG,e.toString());
        }
    }
    //TedPermission
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Log.d(TAG,"권한 허가");
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Log.d(TAG,"권한 불허");
        }
    };
    //뒤로가기 2초 안에 2번 누를시 앱 종료
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if(backPressedTime - curTime < 2000){
            super.onBackPressed();
        }else{
            backPressedTime = curTime;
            Toast.makeText(this,"뒤로가기 누를시 앱이 종료됩니다",Toast.LENGTH_SHORT).show();
        }
    }
}