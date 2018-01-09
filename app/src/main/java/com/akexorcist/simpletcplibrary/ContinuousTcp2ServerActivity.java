package com.akexorcist.simpletcplibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.akexorcist.simpletcp.ContinuousTcpClient;
import com.akexorcist.simpletcp.TcpUtils;

import java.net.Socket;

public class ContinuousTcp2ServerActivity extends AppCompatActivity {
    public static final int TCP_PORT = 2000;

    private Button buttonUp;
    private Button buttonDown;
    private Button buttonLeft;
    private Button buttonRight;
    private TextView textViewIpAddress;
    private TextView textViewStatus;

    private ContinuousTcpClient continuousTcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continuous_tcp_2_server);

        textViewIpAddress = findViewById(R.id.textViewIpAddress);
        textViewStatus = findViewById(R.id.textViewStatus);
        buttonUp = findViewById(R.id.buttonUp);
        buttonDown = findViewById(R.id.buttonDown);
        buttonRight = findViewById(R.id.buttonRight);
        buttonLeft = findViewById(R.id.buttonLeft);

        textViewIpAddress.setText(TcpUtils.getIpAddress(this));

        continuousTcpClient = new ContinuousTcpClient(TCP_PORT, new ContinuousTcpClient.TcpConnectionListener() {
            public void onDisconnected() {
                textViewStatus.setText("Disconnect");
            }

            public void onDataReceived(String message, String ip) {
                if (message.equals("U_DOWN")) {
                    buttonUp.setPressed(true);
                } else if (message.equals("U_UP")) {
                    buttonUp.setPressed(false);
                } else if (message.equals("D_DOWN")) {
                    buttonDown.setPressed(true);
                } else if (message.equals("D_UP")) {
                    buttonDown.setPressed(false);
                } else if (message.equals("R_DOWN")) {
                    buttonRight.setPressed(true);
                } else if (message.equals("R_UP")) {
                    buttonRight.setPressed(false);
                } else if (message.equals("L_DOWN")) {
                    buttonLeft.setPressed(true);
                } else if (message.equals("L_UP")) {
                    buttonLeft.setPressed(false);
                }
            }

            public void onConnected(String hostName, String hostAddress, Socket s) {
                textViewStatus.setText("Connected from " + hostAddress);
            }
        });
    }

    public void onResume() {
        super.onResume();
        continuousTcpClient.start();
    }

    public void onStop() {
        super.onStop();
        continuousTcpClient.stop();
    }
}
