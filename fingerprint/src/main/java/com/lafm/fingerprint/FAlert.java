package com.lafm.fingerprint;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class FAlert {

    private Dialog dialog;

    private TextView message;

    private ImageView ic_fingerprint;

    private Activity context;

    public FAlert(Activity context){
        this.context = context;
    }

    private void show(){

        if(dialog != null && dialog.getWindow() == context. getWindow())
            dialog.cancel();

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_fingerprint);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        message = (TextView) dialog.findViewById(R.id.message);
        ic_fingerprint = (ImageView) dialog.findViewById(R.id.ic_fingerprint);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();
    }


}
