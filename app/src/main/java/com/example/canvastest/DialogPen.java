package com.example.canvastest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
    public int size = 15;
    private SeekBar sizeBar;
    public DialogPen(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        sizeBar = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pen,null).findViewById(R.id.sizeBar);
        TextView sizeText = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pen,null).findViewById(R.id.sizeText);
        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sizeText.setText(i+"");
                MainActivity.myView.size = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getContext(),size+"",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(R.layout.dialog_pen);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });

        return builder.create();
    }
}
