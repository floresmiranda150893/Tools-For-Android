package com.lafm.floatingbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class FloatingActionsMenu {

    private LinearLayout background;
    private LinearLayout layout_menu;
    private LinearLayout layout_opcions;
    private FloatingActionButton button;
    private IFloatingAction [] options;
    private IFloatingAction action;
    private Activity context;
    private long first_touch;
    private boolean active_menu;

    private ViewGroup vRoot;
    private View vMenu;
    private int rMenu = R.layout.layout_floating_menu;

    public FloatingActionsMenu(Activity context, IFloatingAction action){
        this.action = action;
        this.context = context;

        initActionsMenu();
    }

    public FloatingActionsMenu(Activity context, IFloatingAction action, IFloatingAction [] options){
        this.action = action;
        this.options = options;
        this.context = context;

        initActionsMenu();
    }

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    private void initActionsMenu(){

        if(vMenu == null){

            vRoot = context.findViewById(android.R.id.content);

            vMenu = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(rMenu, null);

            vMenu.setElevation(10);

            vRoot.addView(vMenu, 0,
                    new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT));

        }


        background = (LinearLayout) vMenu.findViewById(R.id.background);
        layout_menu = (LinearLayout) vMenu.findViewById(R.id.layout_menu);
        layout_opcions = (LinearLayout) vMenu.findViewById(R.id.layout_opcions);
        button = (FloatingActionButton) vMenu.findViewById(R.id.favoritos);

        layout_menu.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                layout_opcions.setVisibility(View.VISIBLE);
                action.onAction(true);
                background.setVisibility(View.VISIBLE);
                active_menu = true;
            }
        });


        layout_menu.setOnTouchListener(new View.OnTouchListener() {

            private PointF DownPT = new PointF();
            private int CLICK_ACTION_THRESHHOLD = 80;

            @Override public boolean onTouch(View view, MotionEvent event) {
                int eid = event.getAction();


                switch (eid) {
                    case MotionEvent.ACTION_DOWN :

                        setLocation(layout_menu, event);

                        first_touch = System.currentTimeMillis();

                        DownPT.x = view.getX() - event.getRawX();
                        DownPT.y = view.getY() - event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE :

                        if(layout_opcions.getVisibility() == View.VISIBLE){
                            layout_opcions.setVisibility(View.INVISIBLE);
                            background.setVisibility(View.GONE);
                            break;
                        }

                        View viewParent = (View)view.getParent();
                        int parentWidth = viewParent.getWidth();
                        int parentHeight = viewParent.getHeight();

                        float newX = event.getRawX() + DownPT.x;
                        newX = Math.max(0, newX);
                        newX = Math.min(parentWidth - view.getWidth(), newX);

                        float newY = event.getRawY() + DownPT.y;
                        newY = Math.max((layout_opcions.getHeight()) * -1, newY);
                        newY = Math.min(parentHeight - view.getHeight(), newY);

                        layout_menu.setX(newX);
                        layout_menu.setY(newY);

                        if (System.currentTimeMillis() - first_touch > 80)
                            first_touch = 0;

                        break;
                    case MotionEvent.ACTION_UP :

                        if(active_menu){
                            active_menu = false;
                            return true;
                        }

                        if (System.currentTimeMillis() - first_touch < CLICK_ACTION_THRESHHOLD) {

                            if(layout_opcions.getVisibility() == View.VISIBLE){
                                action.onAction(false);
                                layout_opcions.setVisibility(View.INVISIBLE);
                                background.setVisibility(View.GONE);

                                new Handler().postDelayed(new Runnable() { public void run(){
                                    active_menu = false;
                                }}, 1);

                                break;
                            }else
                                restarLocation();

                        }
                        break;
                    default :
                        break;
                }

                return true;

            } });

        if(options != null)
            for(int i=0; i<options.length; i++){

                View layout = LayoutInflater.from(context).inflate(R.layout.layout_floating_menu, null, false);



                final IFloatingAction opction = options[i];

                FloatingActionButton item = new FloatingActionButton(context);

                item.setSize(FloatingActionButton.SIZE_MINI);

                item.setElevation(0);

                item.setImageDrawable(context.getResources().getDrawable(opction.getIcon()));

                item.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        opction.onAction(true);
                        return false;
                    }
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(5, 0, 5, 15);

                layout_opcions.addView(item, params);
            }

    }

    private float orgX = 0, orgY = 0;

    private void setLocation(View view, MotionEvent motionEvent){

        if(orgX == 0){

            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight();

            View viewParent = (View)view.getParent();
            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();

            float newX = motionEvent.getRawX();
            newX = Math.max(0, newX); // Don't allow the FAB past the left hand side of the parent
            newX = Math.min(parentWidth - viewWidth, newX); // Don't allow the FAB past the right hand side of the parent

            float newY = motionEvent.getRawY();
            newY = Math.max(0, newY); // Don't allow the FAB past the top of the parent
            newY = Math.min(parentHeight - viewHeight, newY); // Don't allow the FAB past the bottom of the parent

            orgX = newX;
            orgY = newY;
        }

    }

    private void restarLocation(){
        layout_menu.animate().x(orgX).y(orgY).setDuration(150).start();
    }

}
