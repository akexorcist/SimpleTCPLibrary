package com.akexorcist.simpletcplibrary;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.akexorcist.simpletcp.SimpleTcpClient;
import com.akexorcist.simpletcp.SimpleTcpServer;
import com.akexorcist.simpletcp.TcpUtils;

public class SimpleTcp3ServerActivity extends AppCompatActivity {
    public final int TCP_PORT = 21111;

    private SimpleTcpServer simpleTcpServer;

    private TextView textViewIpAddress;
    private TextView textViewSample;
    private CheckBox checkBoxItalic;
    private CheckBox checkBoxBold;
    private RadioButton radioBlack;
    private RadioButton radioRed;
    private RadioButton radioGreen;
    private RadioButton radioBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_tcp_3_server);

        checkBoxItalic = findViewById(R.id.checkBoxItalic);
        checkBoxBold = findViewById(R.id.checkBoxBold);
        radioBlack = findViewById(R.id.radioBlack);
        radioRed = findViewById(R.id.radioRed);
        radioGreen = findViewById(R.id.radioGreen);
        radioBlue = findViewById(R.id.radioBlue);
        textViewSample = findViewById(R.id.textViewSample);
        textViewIpAddress = findViewById(R.id.textViewIpAddress);

        textViewIpAddress.setText(TcpUtils.getIpAddress(this));

        simpleTcpServer = new SimpleTcpServer(TCP_PORT);
        simpleTcpServer.setOnDataReceivedListener(new SimpleTcpServer.OnDataReceivedListener() {
            int style = 0;

            public void onDataReceived(String message, String ip) {
                switch (message) {
                    case "FONT_ITALIC_ON":
                        checkBoxItalic.setChecked(true);
                        textViewSample.setTypeface(null, style |= Typeface.ITALIC);
                        break;
                    case "FONT_ITALIC_OFF":
                        checkBoxItalic.setChecked(false);
                        textViewSample.setTypeface(null, style &= ~Typeface.ITALIC);
                        break;
                    case "FONT_BOLD_ON":
                        checkBoxBold.setChecked(true);
                        textViewSample.setTypeface(null, style |= Typeface.BOLD);
                        break;
                    case "FONT_BOLD_OFF":
                        checkBoxBold.setChecked(false);
                        textViewSample.setTypeface(null, style &= ~Typeface.BOLD);
                        break;
                    case "COLOR_BLACK":
                        radioBlack.setChecked(true);
                        textViewSample.setTextColor(Color.parseColor("#333333"));
                        break;
                    case "COLOR_RED":
                        radioRed.setChecked(true);
                        textViewSample.setTextColor(Color.parseColor("#D35267"));
                        break;
                    case "COLOR_GREEN":
                        radioGreen.setChecked(true);
                        textViewSample.setTextColor(Color.parseColor("#72CC94"));
                        break;
                    case "COLOR_BLUE":
                        radioBlue.setChecked(true);
                        textViewSample.setTextColor(Color.parseColor("#59ACd3"));
                        break;
                    case "UPDATE":
                        String refresh = checkBoxItalic.isChecked() + ","
                                + checkBoxBold.isChecked() + ","
                                + radioBlack.isChecked() + ","
                                + radioRed.isChecked() + ","
                                + radioGreen.isChecked() + ","
                                + radioBlue.isChecked();
                        SimpleTcpClient.send(refresh, ip, TCP_PORT);
                        break;
                }
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
