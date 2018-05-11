package com.lafm.fingerprint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Chris on 17/04/2018.
 */

public class Fingerprint {

    private static final Fingerprint ourInstance = new Fingerprint();

    private Activity context;

    private IFingerprint iFingerprint;

    private FingerprintManager fingerprintManager;

    private CancellationSignal cancellationSignal;

    private FingerprintManager.AuthenticationCallback authenticationCallback;

    private boolean isAvailable;

    private Dialog dialog;

    private TextView message;

    private ImageView ic_fingerprint;

    @SuppressLint("NewApi")
    public static Fingerprint getInstance(Activity context, IFingerprint iFingerprint) {

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
                if(ourInstance.iFingerprint.showAlert())
                    ourInstance.showAlert();
                else
                    ourInstance.iFingerprint.onAuthenticationStart();
            }

        }
    }

    public void stopListening() {
        if (ourInstance.isAvailable){

            if (ourInstance.iFingerprint != null)
                if(ourInstance.iFingerprint.showAlert() && ourInstance.dialog != null)
                    ourInstance.dialog.cancel();

            ourInstance.cancellationSignal.cancel();

        }
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

                if (ourInstance.iFingerprint != null){

                    ourInstance.iFingerprint.onAuthenticationHelp(errString.toString());

                    if(ourInstance.iFingerprint.showAlert() && errorCode == 7){
                        message.setText(errString.toString());
                        message.setTextColor(Color.parseColor("#F44336"));
                        ic_fingerprint.setColorFilter(Color.parseColor("#F44336"));
                    }

                }

            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

                if (ourInstance.iFingerprint != null){

                    ourInstance.iFingerprint.onAuthenticationHelp(helpString.toString());

                    if(ourInstance.iFingerprint.showAlert()){
                        message.setText(helpString.toString());
                        message.setTextColor(Color.parseColor("#F44336"));
                    }

                }

            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

                if (ourInstance.iFingerprint != null){

                    ourInstance.iFingerprint.onAuthenticationSucceeded();

                    if(ourInstance.iFingerprint.showAlert())
                        ic_fingerprint.setColorFilter(Color.parseColor("#28DF03"));

                }

            }

            @Override
            public void onAuthenticationFailed() {

                if (ourInstance.iFingerprint != null){

                    ourInstance.iFingerprint.onAuthenticationFailed();

                    if(ourInstance.iFingerprint.showAlert()){
                        message.setText(R.string.failed_fingerprint);
                        message.setTextColor(Color.parseColor("#F44336"));
                    }
                }

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

    private void showAlert(){

        if(ourInstance.dialog != null && ourInstance.dialog.getWindow() == ourInstance.context.getWindow())
            ourInstance.dialog.cancel();

        ourInstance.dialog = new Dialog(ourInstance.context);
        ourInstance.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ourInstance.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ourInstance.dialog.setContentView(R.layout.layout_fingerprint);
        ourInstance.dialog.setCancelable(false);
        ourInstance.dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView cancel = (TextView) ourInstance.dialog.findViewById(R.id.cancel);
        ourInstance.message = (TextView) ourInstance.dialog.findViewById(R.id.message);
        ourInstance.ic_fingerprint = (ImageView) ourInstance.dialog.findViewById(R.id.ic_fingerprint);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ourInstance.stopListening();
            }
        });

        ourInstance.dialog.show();
    }

}
