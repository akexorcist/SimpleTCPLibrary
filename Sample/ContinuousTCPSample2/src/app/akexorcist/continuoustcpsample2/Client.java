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

package app.akexorcist.continuoustcpsample2;

import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import app.akexorcist.simpletcplibrary.ContinuousTCP;
import app.akexorcist.simpletcplibrary.TCPUtils;
import app.akexorcist.simpletcplibrary.ContinuousTCP.ConnectionCallback;
import app.akexorcist.simpletcplibrary.ContinuousTCP.TCPConnectionListener;

public class Client extends Activity {
	public final int TCP_PORT = 2000;

	private Button buttonConnect, buttonUp, buttonDown, buttonLeft, buttonRight;
	private TextView textViewStatus;
	private EditText editTextIP;
	
	private ContinuousTCP tcp;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_client);
		
		tcp = new ContinuousTCP(TCP_PORT, new TCPConnectionListener() {
			public void onDisconnected() { }
			public void onDataReceived(String message, String ip) { }
			public void onConnected(String hostName, String hostAddress, Socket s) { }
		});

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
		
		buttonUp = (Button)findViewById(R.id.buttonUp);
		buttonUp.setOnTouchListener(listener);
		
		buttonDown = (Button)findViewById(R.id.buttonDown);
		buttonDown.setOnTouchListener(listener);
		
		buttonLeft = (Button)findViewById(R.id.buttonLeft);
		buttonLeft.setOnTouchListener(listener);
		
		buttonRight = (Button)findViewById(R.id.buttonRight);
		buttonRight.setOnTouchListener(listener);

	}
	
	private OnTouchListener listener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			
			if(action == MotionEvent.ACTION_DOWN 
					|| action == MotionEvent.ACTION_UP) {
				
				String str = "";
				int id = v.getId();
				if(id == R.id.buttonUp) {
					str = "U";
				} else if(id == R.id.buttonDown) {
					str = "D";
				} else if(id == R.id.buttonLeft) {
					str = "L";
				} else if(id == R.id.buttonRight) {
					str = "R";
				}
				
				if(action == MotionEvent.ACTION_DOWN) {
					str += "_DOWN";
				} else if(action == MotionEvent.ACTION_UP) {
					str += "_UP";					
				}
				
				tcp.send(str);
			}
			
			return false;
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
