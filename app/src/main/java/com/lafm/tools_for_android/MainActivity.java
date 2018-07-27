package com.lafm.tools_for_android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lafm.fingerprint.Fingerprint;
import com.lafm.fingerprint.IFingerprint;
import com.lafm.floatingbutton.FloatingActionsMenu;
import com.lafm.floatingbutton.IFloatingAction;
import com.lafm.loading.Loading;

public class MainActivity extends Activity {

    private Activity context;

    private Button btn_fingerprint;
    private Button btn_loading;
    private Button btn_floatingbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        btn_fingerprint = findViewById(R.id.fingerprint);
        btn_loading = findViewById(R.id.loading);
        btn_floatingbutton = findViewById(R.id.floatingbutton);

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

        btn_floatingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingbutton();
            }
        });

    }

    @Override
    public void onPause(){
        super.onPause();

        if(fingerprint != null)
            fingerprint.stopListening();

    }

    private Fingerprint fingerprint;

    public void fingerprint(){

        fingerprint = Fingerprint.getInstance(getApplicationContext(), new IFingerprint() {

            @Override
            public void onAuthenticationStart() {

            }

            @Override
            public void onAuthenticationSucceeded() {
                fingerprint.stopListening();
                Toast.makeText(context,"onAuthenticationSucceeded",Toast.LENGTH_LONG).show();
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

    private Loading loading;

    public void loading(){

        loading = new Loading(this);

        loading.onLoading(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.onLoading(false);
            }
        }, 500);

    }

    private FloatingActionsMenu floatingMenu;

    public void floatingbutton(){

        floatingMenu = new FloatingActionsMenu(this,
                new IFloatingAction() {
                    @Override
                    public void onAction(boolean active) {

                    }

                    @Override
                    public String getText() {
                        return null;
                    }

                    @Override
                    public int getIcon() {
                        return 0;
                    }
                },
                new IFloatingAction[]{
                        new IFloatingAction() {
                            @Override
                            public void onAction(boolean active) {
                                Toast.makeText(context, "Opcion 1", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public String getText() {
                                return "Opcion 1";
                            }

                            @Override
                            public int getIcon() {
                                return R.drawable.ic_launcher_foreground;
                            }
                        },
                        new IFloatingAction() {
                            @Override
                            public void onAction(boolean active) {
                                Toast.makeText(context, "Opcion 2", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public String getText() {
                                return "Opcion 2";
                            }

                            @Override
                            public int getIcon() {
                                return R.drawable.ic_launcher_foreground;
                            }
                        }
                }
        );

    }


}
