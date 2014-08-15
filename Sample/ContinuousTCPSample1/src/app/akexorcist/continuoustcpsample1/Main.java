/*
 * Copyright (c) 2013 Akexorcist
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package app.akexorcist.continuoustcpsample1;

import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import app.akexorcist.simpletcplibrary.ContinuousTCP.ConnectionCallback;
import app.akexorcist.simpletcplibrary.ContinuousTCP;
import app.akexorcist.simpletcplibrary.ContinuousTCP.TCPConnectionListener;
import app.akexorcist.simpletcplibrary.TCPUtils;

public class Main extends Activity {
	public final int TCP_PORT = 2000;
	
	private Button buttonConnect, button1, button2, button3, button4
			, button5, button6, button7, button8, button9;
	private TextView textViewIP, textViewStatus;
	private EditText editTextIP;
	
	private ContinuousTCP tcp;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		
		tcp = new ContinuousTCP(TCP_PORT, new TCPConnectionListener() {
			public void onDisconnected() {
				textViewStatus.setText("Disconnect");
				editTextIP.setEnabled(true);
				buttonConnect.setEnabled(true);
				buttonConnect.setText("Open");
			}
			
			public void onDataReceived(String message, String ip) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			}
			
			public void onConnected(String hostName, String hostAddress, Socket s) {
				textViewStatus.setText("Connect");
				editTextIP.setText(hostAddress);
				editTextIP.setEnabled(false);
				buttonConnect.setText("Close");
			}
		});
		
		textViewIP = (TextView)findViewById(R.id.textViewIP);
		textViewIP.setText(TCPUtils.getIP(getApplicationContext()));
		
		textViewStatus = (TextView)findViewById(R.id.textViewStatus);
		
		editTextIP = (EditText)findViewById(R.id.editTextIP);
		TCPUtils.forceInputIP(editTextIP);

		buttonConnect = (Button)findViewById(R.id.buttonConnect);
		buttonConnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(buttonConnect.getText().toString().equals("Open")) {
					buttonConnect.setEnabled(false);
					editTextIP.setEnabled(false);
					
					String ip = editTextIP.getText().toString();
					tcp.connect(ip, TCP_PORT, new ConnectionCallback() {			
						public void onConnectionFailed(String ip, Exception e) {
							textViewStatus.setText("Disconnect");
							buttonConnect.setEnabled(true);
							editTextIP.setEnabled(true);
						}
						
						public void onConnected(String hostName, String hostAddress) {
							textViewStatus.setText("Connect");
							editTextIP.setEnabled(false);
							buttonConnect.setEnabled(true);
							buttonConnect.setText("Close");
						}
					});
				} else if(buttonConnect.getText().toString().equals("Close")) {
					tcp.disconnect();
					textViewStatus.setText("Disconnect");
					editTextIP.setEnabled(true);
					buttonConnect.setText("Open");
				}
			}
		});
		
		button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(buttonListener);
		
		button2 = (Button)findViewById(R.id.button2);
		button2.setOnClickListener(buttonListener);
		
		button3 = (Button)findViewById(R.id.button3);
		button3.setOnClickListener(buttonListener);
		
		button4 = (Button)findViewById(R.id.button4);
		button4.setOnClickListener(buttonListener);
		
		button5 = (Button)findViewById(R.id.button5);
		button5.setOnClickListener(buttonListener);
		
		button6 = (Button)findViewById(R.id.button6);
		button6.setOnClickListener(buttonListener);
		
		button7 = (Button)findViewById(R.id.button7);
		button7.setOnClickListener(buttonListener);
		
		button8 = (Button)findViewById(R.id.button8);
		button8.setOnClickListener(buttonListener);
		
		button9 = (Button)findViewById(R.id.button9);
		button9.setOnClickListener(buttonListener);
	}
	
	private OnClickListener buttonListener = new OnClickListener() {
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.button1) {
				tcp.send("1");
			} else if(id == R.id.button2) {
				tcp.send("2");
			} else if(id == R.id.button3) {
				tcp.send("3");
			} else if(id == R.id.button4) {
				tcp.send("4");
			} else if(id == R.id.button5) {
				tcp.send("5");
			} else if(id == R.id.button6) {
				tcp.send("6");
			} else if(id == R.id.button7) {
				tcp.send("7");
			} else if(id == R.id.button8) {
				tcp.send("8");
			} else if(id == R.id.button9) {
				tcp.send("9");
			}
		}
	};
	
	public void onResume() {
		super.onResume();
		tcp.start();
	}
	
	public void onStop() {
		super.onStop();
		tcp.stop();
	}
}
