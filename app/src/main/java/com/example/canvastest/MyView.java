package com.example.canvastest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;

import java.util.ArrayList;
import java.util.Stack;

public class MyView extends View{
    public Paint myPaint;
    private Path myPath;
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        myPaint.setStrokeWidth(size);
        myPaint.setColor(color);
        for(int i =0;i<pathList.size();i++){
            canvas.drawPath(pathList.get(i),paintList.get(i));
        }
        if(isEraser)canvas.drawCircle(x,y,size/5,myPaint);
        canvas.drawPath(myPath,myPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();

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
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
    public Bitmap getCanvasBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(),this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

}
