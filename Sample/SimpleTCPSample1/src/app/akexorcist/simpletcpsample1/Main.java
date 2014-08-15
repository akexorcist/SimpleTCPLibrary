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

package app.akexorcist.simpletcpsample1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import app.akexorcist.simpletcplibrary.SimpleTCPClient;
import app.akexorcist.simpletcplibrary.SimpleTCPServer;
import app.akexorcist.simpletcplibrary.SimpleTCPServer.OnDataReceivedListener;
import app.akexorcist.simpletcplibrary.TCPUtils;

public class Main extends Activity {
	public final int TCP_PORT = 21111;
	
	private SimpleTCPServer server;

	private TextView textViewIP;
	private EditText editTextMessage, editTextIP;
	private Button buttonSend;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		server = new SimpleTCPServer(TCP_PORT); 
		server.setOnDataReceivedListener(new OnDataReceivedListener() {
			public void onDataReceived(String message, String ip) {
				Toast.makeText(getApplicationContext()
						, message + " : " + ip, Toast.LENGTH_SHORT).show();
			}
		});

		textViewIP = (TextView)findViewById(R.id.textViewIP);
		textViewIP.setText(TCPUtils.getIP(Main.this) + ":" + TCP_PORT);

		editTextMessage = (EditText)findViewById(R.id.editTextMessage);
		
		editTextIP = (EditText)findViewById(R.id.editTextIP);
		TCPUtils.forceInputIP(editTextIP);
		
		buttonSend = (Button)findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(editTextMessage.getText().length() > 0) {
					String message = editTextMessage.getText().toString();
					String ip = editTextIP.getText().toString();
					
					// Send message without callback
					SimpleTCPClient.send(message, ip, TCP_PORT);
					
					// Send message and waiting for callback
					/*SimpleTCPClient.send(message, ip, TCP_PORT, new SendCallback() {
						public void onSuccess(String tag) {
							Toast.makeText(getApplicationContext(), "onSuccess", Toast.LENGTH_SHORT).show();
						}

						public void onFailed(String tag) {
							Toast.makeText(getApplicationContext(), "onFailed", Toast.LENGTH_SHORT).show();
						}
					}, "TAG");*/
				}
			}
		});
	}
	
	public void onResume() {
		super.onResume();
		server.start();
	}
	
	public void onStop() {
		super.onStop();
		server.stop();
	}
}