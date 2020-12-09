package com.example.canvastest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class ExampleView extends View {
    private Paint myPaint;
    private Path myPath;
    public float strokeWidth = 10.f;
    public ExampleView(Context context) {
        super(context);
        myPaint = new Paint();
        myPath = new Path();
        myPath.moveTo(100,100);
        myPath.lineTo(1000,100);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setAntiAlias(true);
        myPaint.setColor(Color.BLACK);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        myPaint.setStrokeWidth(strokeWidth);
        canvas.drawPath(myPath,myPaint);
    }
}
