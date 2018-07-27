package com.lafm.fingerprint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Chris on 17/04/2018.
 */

public class Fingerprint {

    private static final Fingerprint ourInstance = new Fingerprint();

    private Context context;

    private IFingerprint iFingerprint;

    private FingerprintManager fingerprintManager;

    private CancellationSignal cancellationSignal;

    private FingerprintManager.AuthenticationCallback authenticationCallback;

    private boolean isAvailable;

    @SuppressLint("NewApi")
    public static Fingerprint getInstance(Context context, IFingerprint iFingerprint) {

        ourInstance.context = context;
        ourInstance.iFingerprint = iFingerprint;

        ourInstance.validateFingerprintManager();

        return ourInstance;
    }

    @SuppressLint("NewApi")
    public void startListening() {

        if(ourInstance.cancellationSignal.isCanceled())
            ourInstance.setFingerprintManager();

        if (ourInstance.isAvailable){

            ourInstance.fingerprintManager.authenticate(null, ourInstance.cancellationSignal, 0 /* flags */, ourInstance.authenticationCallback, null);

            if (ourInstance.iFingerprint != null){
                ourInstance.iFingerprint.onAuthenticationStart();
            }

        }
    }

    public void stopListening() {
        if (ourInstance.isAvailable)
            ourInstance.cancellationSignal.cancel();
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @SuppressLint("NewApi")
    private void setFingerprintManager(){

        ourInstance.fingerprintManager = ourInstance.context.getSystemService(FingerprintManager.class);

        ourInstance.cancellationSignal = new CancellationSignal();

        ourInstance.authenticationCallback = new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {

                if (ourInstance.iFingerprint != null)
                    ourInstance.iFingerprint.onAuthenticationHelp(errString.toString());

            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

                if (ourInstance.iFingerprint != null)
                    ourInstance.iFingerprint.onAuthenticationHelp(helpString.toString());

            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

                if (ourInstance.iFingerprint != null)
                    ourInstance.iFingerprint.onAuthenticationSucceeded();

            }

            @Override
            public void onAuthenticationFailed() {

                if (ourInstance.iFingerprint != null)
                    ourInstance.iFingerprint.onAuthenticationFailed();

            }
        };

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void validateFingerprintManager() {

        try{

            if(ourInstance.iFingerprint == null){
                ourInstance.isAvailable = false;
                return;
            }

            ourInstance.setFingerprintManager();

            ourInstance.isAvailable = true;

            if (ourInstance.fingerprintManager == null)
                ourInstance.isAvailable = false;

            if (!ourInstance.fingerprintManager.isHardwareDetected())
                ourInstance.isAvailable = false;

            if (!ourInstance.fingerprintManager.hasEnrolledFingerprints())
                ourInstance.isAvailable = false;

        }catch (Exception ex){
            ex.printStackTrace();
            ourInstance.isAvailable = false;
        }

    }

    /*Verificar como agregar el funcionamiento encapsulado*/
    private void showAlert(Context context){

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.layout_fingerprint);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView message = (TextView) dialog.findViewById(R.id.message);
        ImageView ic_fingerprint = (ImageView) dialog.findViewById(R.id.ic_fingerprint);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.show();
    }

}
