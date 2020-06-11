package com.example.tesistrabajador.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.tesistrabajador.R;

 public class Alertdialoglogin {
    Activity activity;
    AlertDialog alertDialog;


    public Alertdialoglogin(Activity myactivity){
        activity=myactivity;
    }


    public void startalertdialog(){

        AlertDialog.Builder builderlogin = new AlertDialog.Builder(activity);
        LayoutInflater inflaterload = activity.getLayoutInflater();
        View viewlogin = inflaterload.inflate(R.layout.alertdialogloaginglogin,null);
        builderlogin.setCancelable(false);
        builderlogin.setView(viewlogin);
        alertDialog = builderlogin.create();
        alertDialog.show();

    }

    public void cancelalerdialog(){
        alertDialog.dismiss();
    }

}
