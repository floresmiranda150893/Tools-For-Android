package com.lafm.tools_for_android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.lafm.fingerprint.Fingerprint;
import com.lafm.fingerprint.IFingerprint;
import com.lafm.loading.Loading;

public class MainActivity extends Activity {

    private Activity context;

    private Button btn_fingerprint;
    private Button btn_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        btn_fingerprint = findViewById(R.id.fingerprint);
        btn_loading = findViewById(R.id.loading);

        btn_fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fingerprint();
            }
        });

        btn_loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading();
            }
        });

    }

    private Fingerprint fingerprint;

    public void fingerprint(){

        fingerprint = Fingerprint.getInstance(this, new IFingerprint() {
            @Override
            public boolean showAlert() {
                return true;
            }

            @Override
            public void onAuthenticationStart() {

            }

            @Override
            public void onAuthenticationSucceeded() {
                fingerprint.stopListening();
            }

            @Override
            public void onAuthenticationFailed() {

            }

            @Override
            public void onAuthenticationHelp(String string) {

            }

        });

        fingerprint.startListening();


    }

    @Override
    public void onPause(){
        super.onPause();

        if(fingerprint != null)
            fingerprint.stopListening();

    }

    private Loading loading = new Loading(this);

    public void loading(){

        loading.onLoading(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.onLoading(false);
            }
        }, 500);

    }

}
