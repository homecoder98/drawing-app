package com.example.canvastest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import petrov.kristiyan.colorpicker.ColorPicker;

public class MainActivity extends AppCompatActivity{
    public static String TAG = "MainActivity";
    private static int GET_GALLERY_IMAGE = 200;
    private LinearLayout settingContainer,canvasContainer;
    private ImageButton saveBtn,loadBtn, colorBtn, backBtn,screenBtn,eraserBtn,btn_pen,forwardBtn;
    public static MyView myView;
    private ColorPicker colorPicker;
    private ArrayList<Path> tempPath;
    private ArrayList<Paint> tempPaint;
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
        settingContainer = (LinearLayout)findViewById(R.id.settingContainer);
        canvasContainer = (LinearLayout)findViewById(R.id.canvasContainer);
        saveBtn = (ImageButton)findViewById(R.id.saveBtn);
        loadBtn = (ImageButton)findViewById(R.id.loadBtn);
        colorBtn = (ImageButton)findViewById(R.id.colorBtn);
        eraserBtn = (ImageButton)findViewById(R.id.eraserBtn);
        backBtn = (ImageButton)findViewById(R.id.backBtn);
        forwardBtn = (ImageButton)findViewById(R.id.forwardBtn);
        screenBtn = (ImageButton)findViewById(R.id.screenBtn);
        btn_pen = (ImageButton)findViewById(R.id.btn_pen);
        //캔버스 인플레이션 + addview
        myView = new MyView(this);
        canvasContainer.addView(myView);

        //버튼 리스너 등록
        saveBtn.setOnClickListener(onClickListener);
        loadBtn.setOnClickListener(onClickListener);

        colorBtn.setOnClickListener(onClickListener);
        eraserBtn.setOnClickListener(onClickListener);
        backBtn.setOnClickListener(onClickListener);
        forwardBtn.setOnClickListener(onClickListener);
        btn_pen.setOnClickListener(onClickListener);
        screenBtn.setOnClickListener(onClickListener);

        //뒤로가기,앞으로가기 임시 리스트 초기화
        tempPath = new ArrayList<Path>();
        tempPaint = new ArrayList<Paint>();
    }
    //onCreate 끝
    //버튼 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.saveBtn:
                    Bitmap bitmap = myView.getCanvasBitmap();
                    if (isExternalStorageWritable()) {
                        saveImage(bitmap);
                    }
                    break;
                case R.id.colorBtn:
                    penChangeColor();
                    break;
                case R.id.loadBtn:
                    loadImage();
                    break;
                case R.id.eraserBtn:
                    penChangeErase();
                    break;
                case R.id.backBtn:
                    removeOneLine();
                    break;
                case R.id.forwardBtn:
                    addOneLine();
                    break;
                case R.id.btn_pen:
                    penSizeChange();
                    break;
                case R.id.screenBtn:
                    if(!isWideScreen){
                        settingContainer.setVisibility(View.GONE);
                        screenBtn.setBackgroundResource(R.drawable.ic_baseline_fullscreen_exit_24);
                        isWideScreen = true;
                    }else{
                        settingContainer.setVisibility(View.VISIBLE);
                        screenBtn.setBackgroundResource(R.drawable.img_fullscreen);
                        isWideScreen = false;
                    }
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
    //색상 선택 버튼 클릭
    private void penChangeColor(){
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
    //지우개 들기
    private void penChangeErase(){
        myView.color = Color.WHITE;
        myView.isEraser = true;
    }
    //뒤로가기 버튼 누를시 한 획 지우기
    private void removeOneLine(){
    }
    //한 획 되돌리기
    private void addOneLine(){
    }
    //펜 사이즈 변경
    private void penSizeChange(){
        DialogPen dialogPen = new DialogPen(MainActivity.this);
        dialogPen.show(getSupportFragmentManager(),null);
    }
    //토스트 생성
    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
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
