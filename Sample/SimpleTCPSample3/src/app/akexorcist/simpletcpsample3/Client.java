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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import app.akexorcist.simpletcplibrary.SimpleTCPClient;
import app.akexorcist.simpletcplibrary.SimpleTCPServer;
import app.akexorcist.simpletcplibrary.TCPUtils;
import app.akexorcist.simpletcplibrary.SimpleTCPServer.OnDataReceivedListener;

public class Client extends Activity {
	public final int TCP_PORT = 21111;

	private SimpleTCPServer server;
	
	private Button buttonRefresh;
	private EditText editTextIP;
	private CheckBox checkBoxItalic, checkBoxBold;
	private RadioGroup radioGroup;
	private RadioButton radioBlack, radioRed, radioGreen, radioBlue;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_client);

		editTextIP = (EditText)findViewById(R.id.editTextIP);
		TCPUtils.forceInputIP(editTextIP);
		
		checkBoxItalic = (CheckBox)findViewById(R.id.checkBoxItalic);
		checkBoxItalic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String ip = editTextIP.getText().toString();
				if(isChecked)
					SimpleTCPClient.send("FONT_ITALIC_ON", ip, TCP_PORT);
				else 
					SimpleTCPClient.send("FONT_ITALIC_OFF", ip, TCP_PORT);
			}
		});
		
		checkBoxBold = (CheckBox)findViewById(R.id.checkBoxBold);
		checkBoxBold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String ip = editTextIP.getText().toString();
				if(isChecked)
					SimpleTCPClient.send("FONT_BOLD_ON", ip, TCP_PORT);
				else 
					SimpleTCPClient.send("FONT_BOLD_OFF", ip, TCP_PORT);
			}
		});
		
		radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				String ip = editTextIP.getText().toString();
				
				if(checkedId == R.id.radioBlack) {
					SimpleTCPClient.send("COLOR_BLACK", ip, TCP_PORT);
				} else if(checkedId == R.id.radioRed) {
					SimpleTCPClient.send("COLOR_RED", ip, TCP_PORT);
				} else if(checkedId == R.id.radioGreen) {
					SimpleTCPClient.send("COLOR_GREEN", ip, TCP_PORT);
				} else if(checkedId == R.id.radioBlue) {
					SimpleTCPClient.send("COLOR_BLUE", ip, TCP_PORT);
				}
			}
		});

		radioBlack = (RadioButton)findViewById(R.id.radioBlack);
		radioRed = (RadioButton)findViewById(R.id.radioRed);
		radioGreen = (RadioButton)findViewById(R.id.radioGreen);
		radioBlue = (RadioButton)findViewById(R.id.radioBlue);
		
		buttonRefresh = (Button)findViewById(R.id.buttonRefresh);
		buttonRefresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String ip = editTextIP.getText().toString();
				SimpleTCPClient.send("UPDATE", ip, TCP_PORT);
			}
		});
		
		server = new SimpleTCPServer(TCP_PORT); 
		server.setOnDataReceivedListener(new OnDataReceivedListener() {			
			public void onDataReceived(String message, String ip) {				
				String[] arr_state = message.split(","); 
				checkBoxItalic.setChecked(Boolean.parseBoolean(arr_state[0]));
				checkBoxBold.setChecked(Boolean.parseBoolean(arr_state[1]));
				radioBlack.setChecked(Boolean.parseBoolean(arr_state[2]));
				radioRed.setChecked(Boolean.parseBoolean(arr_state[3]));
				radioGreen.setChecked(Boolean.parseBoolean(arr_state[4]));
				radioBlue.setChecked(Boolean.parseBoolean(arr_state[5]));
				
				Toast.makeText(Client.this, "Updated", Toast.LENGTH_SHORT).show();
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
