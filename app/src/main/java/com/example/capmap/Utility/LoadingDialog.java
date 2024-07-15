package com.example.capmap.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.capmap.R;
import com.example.capmap.databinding.DialogLayoutBinding;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog(Activity activity){
        this.activity =  activity;
    }

    public void startLoading(){
        if(activity!= null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.LoadingDialogStyle);
            DialogLayoutBinding binding = DialogLayoutBinding.inflate(LayoutInflater.from(activity),null,false);
            builder.setView(binding.getRoot());
            builder.setCancelable(false);
            alertDialog = builder.create();
            binding.progressBar.setVisibility(View.VISIBLE);
            alertDialog.show();
        } else {
            Log.e("LoadingDialog", "Activity is null");
        }
    }

    public void stopLoading(){
        if(alertDialog!= null) {
            alertDialog.dismiss();
        } else {
            Log.e("LoadingDialog", "AlertDialog is null");
        }
    }
}