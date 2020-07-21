package com.malinowski.quadro;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity  implements PopupMenu.OnMenuItemClickListener {

    Button menu;
    Switch aSwitch;
    SeekBar seekBar;
    EditText ip1,ip2,ip3,ip4,port;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings_activity);

        menu = findViewById(R.id.mode_menu);
        aSwitch = findViewById(R.id.switch1);
        seekBar = findViewById(R.id.seekBar);
        Log.i("Seek",""+MainActivity.image.getRotation()/360*100);
        seekBar.setProgress((int) (MainActivity.image.getRotation()/360*100));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
               MainActivity.image.setRotation(360/100*i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        menu.setText(""+MainActivity.mode);
        aSwitch.setChecked(MainActivity.useJoyStick);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MainActivity.useJoyStick = b;
            }
        });

        ip1 = findViewById(R.id.ip1);
        ip1.setText(""+Client.ip[0]);


        ip2 = findViewById(R.id.ip2);
        ip2.setText(""+Client.ip[1]);

        ip3 = findViewById(R.id.ip3);
        ip3.setText(""+Client.ip[2]);

        ip4 = findViewById(R.id.ip4);
        ip4.setText(""+Client.ip[3]);

        port = findViewById(R.id.port);
        port.setText(""+Client.port);
    }

    public void showPopUp(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(SettingsActivity.this);
        popup.inflate(R.menu.popup);
        popup.show();
    }
    @SuppressLint("SetTextI18n")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.zero:
                MainActivity.mode = 0;
                break;
            case R.id.one:
                MainActivity.mode = 1;
                break;
            case R.id.two:
                MainActivity.mode = 2;
                break;
            case R.id.three:
                MainActivity.mode = 3;
                break;
            default:
                break;
        }
        menu.setText(""+MainActivity.mode);
        return true;
    }
    @Override
    protected void onDestroy () {
        super.onDestroy();
        boolean change = false;
        if(!ip1.getText().toString().equals("")) {
            int ip_1 = Integer.parseInt(ip1.getText().toString());
            change = change | (ip_1!=Client.ip[0]);
            Client.ip[0] = ip_1;
        }
        if(!ip2.getText().toString().equals("")) {
            int ip_2 = Integer.parseInt(ip2.getText().toString());
            change = change | (ip_2!=Client.ip[1]);
            Client.ip[1] = ip_2;
        }
        if(!ip3.getText().toString().equals("")) {
            int ip_3 = Integer.parseInt(ip3.getText().toString());
            change = change | (ip_3!=Client.ip[2]);
            Client.ip[2] = ip_3;
        }
        if(!ip4.getText().toString().equals("")) {
            int ip_4 = Integer.parseInt(ip4.getText().toString());
            change = change | (ip_4!=Client.ip[3]);
            Client.ip[3] = ip_4;
        }
        if(!port.getText().toString().equals("")) {
            int port_ = Integer.parseInt(port.getText().toString());
            change = change | (port_!=Client.port);
            Client.port = port_;
        }
        if(change)
            MainActivity.client.isConnected = false;
    }
}