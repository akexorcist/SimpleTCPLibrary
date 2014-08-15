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
import android.widget.Button;
import android.widget.TextView;
import app.akexorcist.simpletcplibrary.ContinuousTCP;
import app.akexorcist.simpletcplibrary.TCPUtils;
import app.akexorcist.simpletcplibrary.ContinuousTCP.TCPConnectionListener;

public class Server extends Activity {
	public final int TCP_PORT = 2000;
	
	private Button buttonUp, buttonDown, buttonLeft, buttonRight;
	private TextView textViewIP, textViewStatus;
	
	private ContinuousTCP tcp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_server);

		textViewIP = (TextView)findViewById(R.id.textViewIP);
		textViewIP.setText(TCPUtils.getIP(Server.this));
		
		textViewStatus = (TextView)findViewById(R.id.textViewStatus);

		buttonUp = (Button)findViewById(R.id.buttonUp);
		buttonDown = (Button)findViewById(R.id.buttonDown);
		buttonRight = (Button)findViewById(R.id.buttonRight);
		buttonLeft = (Button)findViewById(R.id.buttonLeft);
		
		tcp = new ContinuousTCP(TCP_PORT, new TCPConnectionListener() {
			public void onDisconnected() {
				textViewStatus.setText("Disconnect");
			}
			
			public void onDataReceived(String message, String ip) {
				if(message.equals("U_DOWN")) {
					buttonUp.setPressed(true);
				} else if(message.equals("U_UP")) {
					buttonUp.setPressed(false);
				} else if(message.equals("D_DOWN")) {
					buttonDown.setPressed(true);
				} else if(message.equals("D_UP")) {
					buttonDown.setPressed(false);
				} else if(message.equals("R_DOWN")) {
					buttonRight.setPressed(true);
				} else if(message.equals("R_UP")) {
					buttonRight.setPressed(false);
				} else if(message.equals("L_DOWN")) {
					buttonLeft.setPressed(true);
				} else if(message.equals("L_UP")) {
					buttonLeft.setPressed(false);
				}
				//Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			}
			
			public void onConnected(String hostName, String hostAddress, Socket s) {
				textViewStatus.setText("Connected from " + hostAddress);
			}
		});
	}
	
	public void onResume() {
		super.onResume();
		tcp.start();
	}
	
	public void onStop() {
		super.onStop();
		tcp.stop();
	}
}
