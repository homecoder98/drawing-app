package com.example.canvastest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogPen extends DialogFragment {
    private Context context;
    private SeekBar sizeBar;
    private TextView sizeText;
    public DialogPen(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_pen,null);
        builder.setView(layout);

        sizeBar = (SeekBar)layout.findViewById(R.id.sizeBar);
        sizeText = (TextView)layout.findViewById(R.id.sizeText);
        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sizeText.setText("펜 굵기 : "+ i);
                Log.d("test",i+"");
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
}
