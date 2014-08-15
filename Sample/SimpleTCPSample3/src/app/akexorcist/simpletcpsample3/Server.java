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

package app.akexorcist.simpletcpsample3;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import app.akexorcist.simpletcplibrary.SimpleTCPClient;
import app.akexorcist.simpletcplibrary.SimpleTCPServer;
import app.akexorcist.simpletcplibrary.TCPUtils;
import app.akexorcist.simpletcplibrary.SimpleTCPServer.OnDataReceivedListener;

public class Server extends Activity {
	public final int TCP_PORT = 21111;
	
	private SimpleTCPServer server;
	
	private TextView textViewIP, textViewSample;
	private CheckBox checkBoxItalic, checkBoxBold;
	private RadioButton radioBlack, radioRed, radioGreen, radioBlue;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_server);

		checkBoxItalic = (CheckBox)findViewById(R.id.checkBoxItalic);
		checkBoxBold = (CheckBox)findViewById(R.id.checkBoxBold);

		radioBlack = (RadioButton)findViewById(R.id.radioBlack);
		radioRed = (RadioButton)findViewById(R.id.radioRed);
		radioGreen = (RadioButton)findViewById(R.id.radioGreen);
		radioBlue = (RadioButton)findViewById(R.id.radioBlue);

		textViewSample = (TextView)findViewById(R.id.textViewSample);
		
		textViewIP = (TextView)findViewById(R.id.textViewIP);
		textViewIP.setText(TCPUtils.getIP(Server.this));

		server = new SimpleTCPServer(TCP_PORT); 
		server.setOnDataReceivedListener(new OnDataReceivedListener() {
			int style = 0;
			
			public void onDataReceived(String message, String ip) {		
				Log.i("Check", message);		
				switch(message) {
				case "FONT_ITALIC_ON" : 
					checkBoxItalic.setChecked(true);
					textViewSample.setTypeface(null, style |= Typeface.ITALIC);
					break;
				case "FONT_ITALIC_OFF" : 
					checkBoxItalic.setChecked(false);
					textViewSample.setTypeface(null, style &= ~Typeface.ITALIC);
					break;
				case "FONT_BOLD_ON" : 
					checkBoxBold.setChecked(true);
					textViewSample.setTypeface(null, style |= Typeface.BOLD);
					break;
				case "FONT_BOLD_OFF" : 
					checkBoxBold.setChecked(false);
					textViewSample.setTypeface(null, style &= ~Typeface.BOLD);
					break;
				case "COLOR_BLACK" :
					radioBlack.setChecked(true);
					textViewSample.setTextColor(Color.parseColor("#333333"));
					break;
				case "COLOR_RED" : 
					radioRed.setChecked(true);
					textViewSample.setTextColor(Color.parseColor("#D35267"));
					break;
				case "COLOR_GREEN" : 
					radioGreen.setChecked(true);
					textViewSample.setTextColor(Color.parseColor("#72CC94"));
					break;
				case "COLOR_BLUE" : 
					radioBlue.setChecked(true);
					textViewSample.setTextColor(Color.parseColor("#59ACd3"));
					break;
				case "UPDATE" :
					String refresh = checkBoxItalic.isChecked() + ","
							+ checkBoxBold.isChecked() + ","
							+ radioBlack.isChecked() + ","
							+ radioRed.isChecked() + ","
							+ radioGreen.isChecked() + ","
							+ radioBlue.isChecked();
					Log.i("Check", refresh);
					SimpleTCPClient.send(refresh, ip, TCP_PORT);
					break;
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
