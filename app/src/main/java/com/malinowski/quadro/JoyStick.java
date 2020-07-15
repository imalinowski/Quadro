package com.malinowski.quadro;

import android.app.Activity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import static java.lang.Math.sqrt;
//виртуальный джойстик
public class JoyStick {
    ImageView point;
    private float cx, cy;
    double angle = 0;
    double distance = 0;
    private float size = 100;
    int id;
    JoyStick(float x, float y, Activity activity,int id){
        cx = x;
        cy = y;
        this.id = id;

        point = new ImageView(activity);
        point.setImageResource(R.drawable.green_point);
        point.setAlpha(50);
        point.setX(x-size/2.0f);
        point.setY(y-size/2.0f);

        activity.addContentView(point,new FrameLayout.LayoutParams((int)size,(int)size));

    }
  /*  JoyStick(float x, float y,int id){
        cx = x;
        cy = y;
        this.id = id;
        point.setAlpha(50);
        point.setX(x-size/2.0f);
        point.setY(y-size/2.0f);
    }*/
    //math stuff
    void move(float x, float y){

        double len = sqrt(Math.pow(cx-x,2)+Math.pow(cy-y,2));

        if(len > size*1.5){
            point.setX(cx + (float)((x-cx)/len*size*1.5) - size/2);
            point.setY(cy + (float)((y-cy)/len*size*1.5) - size/2);
        }
        else {
            point.setX(x - size / 2);
            point.setY(y - size / 2);
        }

        angle = Math.atan2((x-cx),(y-cy))*180;
        distance = len;
    }
    void delete(){
      /*  point.setAlpha(100);
        id = -1;*/
        FrameLayout parent = (FrameLayout) point.getParent();
        parent.removeView(point);
    }
}
