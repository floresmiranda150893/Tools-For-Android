package com.lafm.loading;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Loading {

    private ViewGroup vRoot;
    private View vLoading;
    private int rLoading = R.layout.layout_loading;
    private Activity context;

    public Loading(Activity context){
        this.context = context;
    }

    public void setResourceLoading(int rLoading) {
        this.rLoading = rLoading;
    }

    //Bloquear pantalla para cargar un recurso, etc.
    public void onLoading(boolean show){

        if(vLoading == null){

            vRoot = context.findViewById(android.R.id.content);

            vLoading = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(rLoading, null);

            vRoot.addView(vLoading, 0,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));



            vLoading.setVisibility(View.GONE);

        }

        if (show)
            vLoading.setVisibility(View.VISIBLE);
        else
            new Handler().postDelayed(new Runnable() { public void run(){
                vLoading.setVisibility(View.GONE);
            }}, 1000);

        vLoading.bringToFront();

    }

    //Solicitar lista de permisos.
    protected void requestPermissions(String[] permissions) {

        if(permissions.length > 0)
            if(!checkPermission(permissions))
                ActivityCompat.requestPermissions(context, permissions,0);

    }

    private boolean checkPermission(String[] permissions) {

        for (int i=0; i<permissions.length; i++)
            if(ContextCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED)
                return false;

        return true;
    }


}
