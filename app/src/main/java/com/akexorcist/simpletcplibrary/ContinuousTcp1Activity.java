package com.akexorcist.simpletcplibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.simpletcp.ContinuousTcpClient;
import com.akexorcist.simpletcp.TcpUtils;

import java.net.Socket;

public class ContinuousTcp1Activity extends AppCompatActivity {
    public final int TCP_PORT = 2000;

    private Button buttonConnect;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button9;
    private TextView textViewIpAddress;
    private TextView textViewStatus;
    private EditText editTextIpAddress;

    private ContinuousTcpClient continuousTcpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continuous_tcp_1);

        textViewIpAddress = findViewById(R.id.textViewIpAddress);
        textViewStatus = findViewById(R.id.textViewStatus);
        editTextIpAddress = findViewById(R.id.editTextIpAddress);
        buttonConnect = findViewById(R.id.buttonConnect);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);

        TcpUtils.forceInputIp(editTextIpAddress);
        textViewIpAddress.setText(TcpUtils.getIpAddress(getApplicationContext()));

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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

        button1.setOnClickListener(buttonListener);
        button2.setOnClickListener(buttonListener);
        button3.setOnClickListener(buttonListener);
        button4.setOnClickListener(buttonListener);
        button5.setOnClickListener(buttonListener);
        button6.setOnClickListener(buttonListener);
        button7.setOnClickListener(buttonListener);
        button8.setOnClickListener(buttonListener);
        button9.setOnClickListener(buttonListener);

        continuousTcpClient = new ContinuousTcpClient(TCP_PORT, new ContinuousTcpClient.TcpConnectionListener() {
            public void onDisconnected() {
                textViewStatus.setText("Disconnect");
                editTextIpAddress.setEnabled(true);
                buttonConnect.setEnabled(true);
                buttonConnect.setText("Open");
            }

            public void onDataReceived(String message, String ip) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

            public void onConnected(String hostName, String hostAddress, Socket s) {
                textViewStatus.setText("Connect");
                editTextIpAddress.setText(hostAddress);
                editTextIpAddress.setEnabled(false);
                buttonConnect.setText("Close");
            }
        });
    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.button1) {
                continuousTcpClient.send("1");
            } else if (id == R.id.button2) {
                continuousTcpClient.send("2");
            } else if (id == R.id.button3) {
                continuousTcpClient.send("3");
            } else if (id == R.id.button4) {
                continuousTcpClient.send("4");
            } else if (id == R.id.button5) {
                continuousTcpClient.send("5");
            } else if (id == R.id.button6) {
                continuousTcpClient.send("6");
            } else if (id == R.id.button7) {
                continuousTcpClient.send("7");
            } else if (id == R.id.button8) {
                continuousTcpClient.send("8");
            } else if (id == R.id.button9) {
                continuousTcpClient.send("9");
            }
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
