package com.malinowski.quadro;

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
        ip1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
               if(!editable.toString().equals(""))
                   Client.ip[0] = Integer.parseInt(editable.toString());
            }
        });
        ip1.setText(""+Client.ip[0]);


        ip2 = findViewById(R.id.ip2);
        ip2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals(""))
                    Client.ip[1] = Integer.parseInt(editable.toString());
            }
        });
        ip2.setText(""+Client.ip[1]);

        ip3 = findViewById(R.id.ip3);
        ip3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals(""))
                    Client.ip[2] = Integer.parseInt(editable.toString());
            }
        });
        ip3.setText(""+Client.ip[2]);

        ip4 = findViewById(R.id.ip4);
        ip4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals(""))
                    Client.ip[3] = Integer.parseInt(editable.toString());
            }
        });
        ip4.setText(""+Client.ip[3]);

        port = findViewById(R.id.port);
        port.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals(""))
                    Client.port = Integer.parseInt(editable.toString());
            }
        });
        port.setText(""+Client.port);
    }

    public void showPopUp(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(SettingsActivity.this);
        popup.inflate(R.menu.popup);
        popup.show();
    }
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
}