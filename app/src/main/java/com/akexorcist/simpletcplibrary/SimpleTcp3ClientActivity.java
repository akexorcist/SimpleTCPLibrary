package com.akexorcist.simpletcplibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.akexorcist.simpletcp.SimpleTcpClient;
import com.akexorcist.simpletcp.SimpleTcpServer;
import com.akexorcist.simpletcp.TcpUtils;

public class SimpleTcp3ClientActivity extends AppCompatActivity {
    public static final int TCP_PORT = 21111;

    private SimpleTcpServer simpleTcpServer;

    private Button buttonRefresh;
    private EditText editTextIP;
    private CheckBox checkBoxItalic;
    private CheckBox checkBoxBold;
    private RadioGroup radioGroup;
    private RadioButton radioBlack;
    private RadioButton radioRed;
    private RadioButton radioGreen;
    private RadioButton radioBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_tcp_3_client);

        editTextIP = findViewById(R.id.editTextIpAddress);
        checkBoxBold = findViewById(R.id.checkBoxBold);
        radioGroup = findViewById(R.id.radioGroup);
        radioBlack = findViewById(R.id.radioBlack);
        radioRed = findViewById(R.id.radioRed);
        radioGreen = findViewById(R.id.radioGreen);
        radioBlue = findViewById(R.id.radioBlue);
        buttonRefresh = findViewById(R.id.buttonRefresh);
        checkBoxItalic = findViewById(R.id.checkBoxItalic);

        TcpUtils.forceInputIp(editTextIP);

        checkBoxItalic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String ip = editTextIP.getText().toString();
                if (isChecked)
                    SimpleTcpClient.send("FONT_ITALIC_ON", ip, TCP_PORT);
                else
                    SimpleTcpClient.send("FONT_ITALIC_OFF", ip, TCP_PORT);
            }
        });

        checkBoxBold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String ip = editTextIP.getText().toString();
                if (isChecked)
                    SimpleTcpClient.send("FONT_BOLD_ON", ip, TCP_PORT);
                else
                    SimpleTcpClient.send("FONT_BOLD_OFF", ip, TCP_PORT);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String ip = editTextIP.getText().toString();

                if (checkedId == R.id.radioBlack) {
                    SimpleTcpClient.send("COLOR_BLACK", ip, TCP_PORT);
                } else if (checkedId == R.id.radioRed) {
                    SimpleTcpClient.send("COLOR_RED", ip, TCP_PORT);
                } else if (checkedId == R.id.radioGreen) {
                    SimpleTcpClient.send("COLOR_GREEN", ip, TCP_PORT);
                } else if (checkedId == R.id.radioBlue) {
                    SimpleTcpClient.send("COLOR_BLUE", ip, TCP_PORT);
                }
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String ip = editTextIP.getText().toString();
                SimpleTcpClient.send("UPDATE", ip, TCP_PORT);
            }
        });

        simpleTcpServer = new SimpleTcpServer(TCP_PORT);
        simpleTcpServer.setOnDataReceivedListener(new SimpleTcpServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ip) {
                String[] arr_state = message.split(",");
                checkBoxItalic.setChecked(Boolean.parseBoolean(arr_state[0]));
                checkBoxBold.setChecked(Boolean.parseBoolean(arr_state[1]));
                radioBlack.setChecked(Boolean.parseBoolean(arr_state[2]));
                radioRed.setChecked(Boolean.parseBoolean(arr_state[3]));
                radioGreen.setChecked(Boolean.parseBoolean(arr_state[4]));
                radioBlue.setChecked(Boolean.parseBoolean(arr_state[5]));
                Toast.makeText(SimpleTcp3ClientActivity.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onResume() {
        super.onResume();
        simpleTcpServer.start();
    }

    public void onStop() {
        super.onStop();
        simpleTcpServer.stop();
    }
}
