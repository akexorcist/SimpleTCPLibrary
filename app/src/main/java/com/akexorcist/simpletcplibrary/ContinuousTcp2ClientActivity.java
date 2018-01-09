package com.akexorcist.simpletcplibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.akexorcist.simpletcp.ContinuousTcpClient;
import com.akexorcist.simpletcp.TcpUtils;

import java.net.Socket;

public class ContinuousTcp2ClientActivity extends AppCompatActivity {
    public static final int TCP_PORT = 2000;

    private Button buttonConnect;
    private Button buttonUp;
    private Button buttonDown;
    private Button buttonLeft;
    private Button buttonRight;
    private TextView textViewStatus;
    private EditText editTextIpAddress;

    private ContinuousTcpClient continuousTcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continuous_tcp_2_client);

        textViewStatus = findViewById(R.id.textViewStatus);
        editTextIpAddress = findViewById(R.id.editTextIpAddress);
        buttonUp = findViewById(R.id.buttonUp);
        buttonDown = findViewById(R.id.buttonDown);
        buttonLeft = findViewById(R.id.buttonLeft);
        buttonRight = findViewById(R.id.buttonRight);
        buttonConnect = findViewById(R.id.buttonConnect);

        TcpUtils.forceInputIp(editTextIpAddress);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (buttonConnect.getText().toString().equals("Open")) {
                    buttonConnect.setEnabled(false);
                    editTextIpAddress.setEnabled(false);

                    String ip = editTextIpAddress.getText().toString();
                    continuousTcpClient.connect(ip, TCP_PORT, new ContinuousTcpClient.ConnectionCallback() {
                        public void onConnectionFailed(String ip, Exception e) {
                            textViewStatus.setText("Disconnect");
                            buttonConnect.setEnabled(true);
                            editTextIpAddress.setEnabled(true);
                        }

                        public void onConnected(String hostName, String hostAddress) {
                            textViewStatus.setText("Connect");
                            editTextIpAddress.setEnabled(false);
                            buttonConnect.setEnabled(true);
                            buttonConnect.setText("Close");
                        }
                    });
                } else if (buttonConnect.getText().toString().equals("Close")) {
                    continuousTcpClient.disconnect();
                    textViewStatus.setText("Disconnect");
                    editTextIpAddress.setEnabled(true);
                    buttonConnect.setText("Open");
                }
            }
        });

        buttonUp.setOnTouchListener(buttonTouchListener);
        buttonDown.setOnTouchListener(buttonTouchListener);
        buttonLeft.setOnTouchListener(buttonTouchListener);
        buttonRight.setOnTouchListener(buttonTouchListener);

        continuousTcpClient = new ContinuousTcpClient(TCP_PORT, new ContinuousTcpClient.TcpConnectionListener() {
            public void onDisconnected() {
            }

            public void onDataReceived(String message, String ip) {
            }

            public void onConnected(String hostName, String hostAddress, Socket s) {
            }
        });
    }

    private View.OnTouchListener buttonTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
                String str = "";
                int id = v.getId();
                if (id == R.id.buttonUp) {
                    str = "U";
                } else if (id == R.id.buttonDown) {
                    str = "D";
                } else if (id == R.id.buttonLeft) {
                    str = "L";
                } else if (id == R.id.buttonRight) {
                    str = "R";
                }
                if (action == MotionEvent.ACTION_DOWN) {
                    str += "_DOWN";
                } else if (action == MotionEvent.ACTION_UP) {
                    str += "_UP";
                }
                continuousTcpClient.send(str);
            }
            return false;
        }
    };

    public void onResume() {
        super.onResume();
        continuousTcpClient.start();
    }

    public void onStop() {
        super.onStop();
        continuousTcpClient.stop();
    }
}
