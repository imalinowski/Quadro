package com.malinowski.quadro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    static TextView info;
    static ImageView point;
    static Button startStop;
    static ImageView image;

    static Client client;
    JoyStick joyStickR;
    JoyStick joyStickL;
    int screenWidth;
    int screenHeight;

    boolean isLaunched = false;
    static boolean useJoyStick = false;
    int throttle = 127;
    int yaw = 127;
    int pitch = 127;
    int roll = 127;
    static int mode = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        screenWidth = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        screenHeight = getApplicationContext().getResources().getDisplayMetrics().heightPixels;

        info = findViewById(R.id.info);
        point = findViewById(R.id.point);
        startStop = findViewById(R.id.start_stop);
        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(client.isConnected){
                    isLaunched = !isLaunched;
                    startStop.setText((isLaunched)?"stop" : "start");
                    client.send((isLaunched)?254:1,254,127,127,mode);
                }
            }
        });

        image = findViewById(R.id.image);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getApplicationContext().getResources(),
                R.drawable.no_video);
        image.setImageBitmap(bitmap);

        //joyStickR = new JoyStick(screenWidth*3/4,screenHeight/2,this,0);
        //joyStickL = new JoyStick(screenWidth*1/4,screenHeight/2,this,1);

        //клиент - класс, отвечающий за соединение в отдельном потоке выполняется подключение
        client = new Client();
        client.start();
        MyTimer timer = new MyTimer();
        timer.start();
    }

    void onDirectionChanged_left(double degrees, double distance){
        throttle = (int)Math.floor(127-127*distance*Math.cos(degrees*Math.PI/180));
        yaw = (int)Math.floor(127+127*distance*Math.sin(degrees* Math.PI/180));
        if(client.isConnected)
            client.send(yaw,throttle,pitch,roll,mode);
    }
    void onDirectionChanged_right(double degrees, double distance){
        pitch  = (int)Math.floor(127-127*distance*Math.cos(degrees*Math.PI/180));
        roll  = (int)Math.floor(127+127*distance*Math.sin(degrees* Math.PI/180));

        if(client.isConnected)
            client.send(yaw,throttle,pitch,roll,mode);
    }

    //работа с виртуальным джойстиком (мультитач внутри)
    @Override
    public boolean onTouchEvent(MotionEvent e) {

        int pointerID = e.getPointerId(e.getActionIndex());

        if(e.getPointerCount()<=pointerID)
            return true;
        try {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (e.getX(pointerID) > screenWidth / 2) {
                        if (joyStickR != null) joyStickR.delete();
                        joyStickR = new JoyStick(e.getX(pointerID), e.getY(pointerID),this, pointerID);
                    } else {
                        if (joyStickL != null) joyStickL.delete();
                        joyStickL = new JoyStick(e.getX(pointerID), e.getY(pointerID),this, pointerID);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    if (e.getPointerCount() == 1) {
                        if (joyStickR != null && joyStickR.id == e.getPointerId(e.getActionIndex()))
                            joyStickR.move(e.getX(pointerID), e.getY(pointerID));
                        else if (joyStickL != null && joyStickL.id == e.getPointerId(e.getActionIndex()))
                            joyStickL.move(e.getX(pointerID), e.getY(pointerID));
                    } else if (e.getPointerCount() == 2) {
                        joyStickR.move(e.getX(joyStickR.id), e.getY(joyStickR.id));
                        joyStickL.move(e.getX(joyStickL.id), e.getY(joyStickL.id));
                    }

                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    if (joyStickR != null && pointerID == joyStickR.id) {
                        joyStickR.delete();
                        joyStickR = null;
                    } else {
                        joyStickL.delete();
                        joyStickL = null;
                    }
                    break;
            }
        }catch (Throwable t){
            Log.e("Error multitouch: ", t.getMessage());
        }
        if(joyStickL!=null)onDirectionChanged_left(joyStickL.angle,joyStickL.distance);
        if(joyStickR!=null)onDirectionChanged_right(joyStickR.angle,joyStickR.distance);

        Log.i("Касания ", ""+e.getPointerCount() + " id ");
        return true;
    }

    //работа с реальным джойстиком
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        //проверка, что эвент пришел от джойстика
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK
                && event.getAction() == MotionEvent.ACTION_MOVE) {
            processJoystickInput(event,-1);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    private void processJoystickInput(MotionEvent event,
                                      int historyPos) {

        InputDevice inputDevice = event.getDevice();

        float x = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_X, historyPos);
        if (x == 0) {
            x = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_X, historyPos);
        }
        float y = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Y, historyPos);
        if (y == 0) {
            y = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_Y, historyPos);
        }
        double angle = Math.atan2(x,y)*180;
        double distance = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));

        onDirectionChanged_left(angle,distance);

        x = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_Z, historyPos);
        y = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_RZ, historyPos);
        angle = Math.atan2(x,y);
        distance = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        onDirectionChanged_right(angle,distance);

    }

    private static float getCenteredAxis(MotionEvent event,
                                         InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis):
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void update(){
        //спустя какое-то время после неудачной попытки соединения вновь пытаемся подключиться
        if(!client.isAlive()){
            client = new Client();
            client.start();
        }
    }

    public void onSettings(View view) {
        startActivity (new Intent(this, SettingsActivity.class));
    }

    //таймер для повторных подключений
    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, 100);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }
        @Override
        public void onFinish() {
        }
    }
}