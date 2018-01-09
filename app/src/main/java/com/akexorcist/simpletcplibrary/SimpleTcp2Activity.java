package com.akexorcist.simpletcplibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.akexorcist.simpletcp.SimpleTcpClient;
import com.akexorcist.simpletcp.SimpleTcpServer;
import com.akexorcist.simpletcp.TcpUtils;

import java.util.ArrayList;

public class SimpleTcp2Activity extends AppCompatActivity {
    public static final int TCP_PORT = 21111;

    private SimpleTcpServer simpleTcpServer;

    private TextView textViewIpAddress;
    private TextView textViewStatus;
    private EditText editTextMessage;
    private EditText editTextIpAddress;
    private Button buttonSend;
    private ListView listViewChat;

    private ArrayList<String> data;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_tcp_2);

        textViewStatus = findViewById(R.id.textViewStatus);
        textViewIpAddress = findViewById(R.id.textViewIpAddress);
        editTextMessage = findViewById(R.id.editTextMessage);
        editTextIpAddress = findViewById(R.id.editTextIpAddress);
        buttonSend = findViewById(R.id.buttonSend);
        listViewChat = findViewById(R.id.listViewChat);

        textViewIpAddress.setText(TcpUtils.getIpAddress(this));
        TcpUtils.forceInputIp(editTextIpAddress);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (editTextMessage.getText().length() > 0) {
                    String message = editTextMessage.getText().toString();
                    String ip = editTextIpAddress.getText().toString();

                    SimpleTcpClient.send(message, ip, TCP_PORT, new SimpleTcpClient.SendCallback() {
                        public void onSuccess(String tag) {
                            textViewStatus.setText("Status : Sent");
                        }

                        public void onFailed(String tag) {
                            textViewStatus.setText("Status : Failed");
                        }
                    }, "TAG");

                    editTextMessage.setText("");
                    textViewStatus.setText("Status : Sending...");
                }
            }
        });

        data = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        listViewChat.setAdapter(adapter);

        simpleTcpServer = new SimpleTcpServer(TCP_PORT);
        simpleTcpServer.setOnDataReceivedListener(new SimpleTcpServer.OnDataReceivedListener() {
            public void onDataReceived(String message, String ipAddress) {
                data.add(message);
                adapter.notifyDataSetChanged();
                listViewChat.post(new Runnable() {
                    public void run() {
                        listViewChat.smoothScrollToPosition(listViewChat.getCount() - 1);
                    }
                });
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
