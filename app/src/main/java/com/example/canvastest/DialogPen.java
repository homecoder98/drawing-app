package com.example.canvastest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

public class DialogPen extends DialogFragment {
    private Context context;
    private SeekBar sizeBar;
    private TextView sizeText;
    private ConstraintLayout const_example;
    Button btn_default_pen,btn_second_pen,btn_third_pen;
    public DialogPen(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_pen,null);
        builder.setView(layout);

        const_example = layout.findViewById(R.id.const_example);
        ExampleView exampleView = new ExampleView(context);
        const_example.addView(exampleView);

        btn_default_pen = (Button)layout.findViewById(R.id.btn_default_pen);
        btn_second_pen = (Button)layout.findViewById(R.id.btn_second_pen);
        btn_third_pen = (Button)layout.findViewById(R.id.btn_third_pen);
        btn_default_pen.setOnClickListener(onClickListener);
        btn_second_pen.setOnClickListener(onClickListener);
        btn_third_pen.setOnClickListener(onClickListener);

        sizeBar = (SeekBar)layout.findViewById(R.id.sizeBar);
        sizeText = (TextView)layout.findViewById(R.id.sizeText);
        sizeText.setText("펜 굵기 : "+ MainActivity.myView.size+"px");
        sizeBar.setProgress((int)MainActivity.myView.size);
        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sizeText.setText("펜 굵기 : "+ i + "px");
                exampleView.strokeWidth = (float)i;
                exampleView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("test","시작");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("test","끝");
            }
        });
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.myView.size = sizeBar.getProgress();
                Log.d("test",sizeBar.getProgress()+"");
            }
        });

        return builder.create();
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btn_default_pen:
                    Toast.makeText(context,"기본 펜 선택",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_second_pen:
                    Toast.makeText(context,"두번째 펜 선택",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_third_pen:
                    Toast.makeText(context,"세번째 펜 선택",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
