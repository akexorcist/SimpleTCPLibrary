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

package app.akexorcist.simpletcplibrary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class SimpleTCPServer {
	public static final String TAG = "SimpleTCPServer";
	
	private boolean isRunning = false;
	private TCPService service = null; 
	private InetAddress inetAddress = null;
	
	private int port = 2000;
	private boolean isConnected = false;
		
	private OnDataReceivedListener mDataReceivedListener = null;
	
	public SimpleTCPServer(int port) {
		this.port = port;
	}

	@SuppressLint("NewApi")
	public void start() {
		if(!this.isRunning) {
			this.service = new TCPService(port);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				this.service.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
			else
				this.service.execute((Void[])null);
			this.isRunning = true;
		}
	}
	
	public void stop() {
		if(this.isRunning) {
			this.service.killTask();
			this.isRunning = false;
		}
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public InetAddress getTargetInetAddress() {
		return this.inetAddress;
	}
	
	public void onMessageIncoming(String message, String ip) {
		this.mDataReceivedListener.onDataReceived(message, ip);
	}
	
	@SuppressLint("NewApi")
	private class TCPService extends AsyncTask<Void, Void, Void> {
		private ServerSocket ss;
		private int port;
		private Boolean TASK_STATE = true;
		
	    public TCPService(int port) {
	    	this.port = port;
	    }
	    
	    public void killTask() {
	    	TASK_STATE = false;
	    }
	    
		protected Void doInBackground(Void... params) {  
			Socket s = null;
			while(TASK_STATE) {
				try {
					ss = new ServerSocket(port);
					ss.setSoTimeout(1000);
					s = ss.accept();

					inetAddress = s.getInetAddress();
					isConnected = true;
				} catch (IOException e) {
					Log.w(TAG, "Socket Timeout");
				}
				
				while(TASK_STATE && isConnected && s != null) {
					try {
						s.setSoTimeout(1000);
						BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

						final String incomingMsg = in.readLine();
						
						if(mDataReceivedListener != null && incomingMsg.length() > 0) {
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								public void run() {
									mDataReceivedListener.onDataReceived(incomingMsg, inetAddress.getHostAddress());
								}
							});
						}

						BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
						String outgoingMsg = "%OK%" + System.getProperty("line.separator"); 
						out.write(outgoingMsg);
						out.flush();
						
					} catch (NullPointerException e) { 
						isConnected = false;
					} catch (SocketTimeoutException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					if(ss != null)
						ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
	
	public interface OnDataReceivedListener {
		public void onDataReceived(String message, String ip);
	}
	
	public void setOnDataReceivedListener (OnDataReceivedListener listener) {
		this.mDataReceivedListener = listener;
    }
}
