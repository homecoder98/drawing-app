package com.example.canvastest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

public class MyView extends View{
    public Paint myPaint;
    private Path myPath;
    private ScaleGestureDetector detector;
    private float scale = 1.0f;
    private float x,y;
    public ArrayList<Path> pathList = new ArrayList<Path>();
    public ArrayList<Paint> paintList = new ArrayList<Paint>();
    public int color = Color.BLACK;
    public float size = 10.0f;
    public boolean isEraser = false;

    public MyView(Context context) {
        super(context);
        x = 0;
        y = 0;
        myPaint = new Paint();
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(30.0f);
        myPaint.setAntiAlias(true);
        myPaint.setColor(Color.BLACK);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPath = new Path();
        pathList.add(myPath);
        paintList.add(myPaint);
        detector = new ScaleGestureDetector(this.getContext(), new ScaleListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(scale,scale,x,y);
        myPaint.setStrokeWidth(size);
        myPaint.setColor(color);
        for(int i =0;i<pathList.size();i++){
            canvas.drawPath(pathList.get(i),paintList.get(i));
        }
        if(isEraser)canvas.drawCircle(x,y,size/7,myPaint);
        canvas.drawPath(myPath,myPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        detector.onTouchEvent(event);

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                myPath.moveTo(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                myPath.lineTo(x,y);
                myPath.moveTo(x,y);
                break;
            case MotionEvent.ACTION_UP:
                pathList.add(myPath);
                myPath = new Path();
                paintList.add(myPaint);
                myPaint = new Paint();
                myPaint.setStyle(Paint.Style.STROKE);
                myPaint.setStrokeCap(Paint.Cap.ROUND);
                myPaint.setAntiAlias(true);
                setButtonAlpha();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
    //현재 캔버스를 비트맵에 넣어 리턴해줍니다.
    public Bitmap getCanvasBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(),this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }
    //사진 불러와서 뷰 객체 배경에 그려줌
    public void setCanvasBitmap(Bitmap bitmap){
        this.setBackground(new BitmapDrawable(getResources(), bitmap));
    }
    //버튼 투명도 설정
    public void setButtonAlpha(){
        if(paintList.size() > 0){
            MainActivity.backBtn.setAlpha(1.f);
        }else{
            MainActivity.backBtn.setAlpha(.5f);
        }
        if(MainActivity.tempPath.size() > 0){
            MainActivity.forwardBtn.setAlpha(1.f);
        }else{
            MainActivity.forwardBtn.setAlpha(.5f);
        }
    }
    //핀치 줌 리스너
    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5.0f));
            invalidate();
            return true;
        }
    }
}
