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
import java.net.Socket;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

public class SimpleTCPClient {
	public static final String TAG = "SimpleTCPClient";
	
	@SuppressLint("NewApi")
	public static void send(String message, String ip, int port) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			new TCPSend(message, ip, port).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
		else
			new TCPSend(message, ip, port).execute((Void[])null);
	}
	
	@SuppressLint("NewApi")
	public static void send(String message, String ip, int port, SendCallback callback, String tag) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			new TCPSend(message, ip, port, callback, tag).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
		else
			new TCPSend(message, ip, port, callback, tag).execute((Void[])null);
	}
	
	@SuppressLint("NewApi")
	private static class TCPSend extends AsyncTask<Void, Void, Void> {
		private SendCallback callback = null;
		
		private String message = null;
		private String ip = null;
		private String tag = null;
		
		private int port;
		
	    public TCPSend(String message, String ip, int port) {
	    	this.message = message;
	    	this.ip = ip;
	    	this.port = port;
	    }
		
	    public TCPSend(String message, String ip, int port, SendCallback callback, String tag) {
	    	this.message = message;
	    	this.ip = ip;
	    	this.port = port;
	    	this.callback = callback;
	    	this.tag = tag;
	    }

		protected Void doInBackground(Void... params) {
			try {
				Socket s = new Socket(ip, port);
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				String outgoingMsg = message + System.getProperty("line.separator"); 
				out.write(outgoingMsg);
				out.flush();
				
				if(callback != null) {
					s.setSoTimeout(5000);
					BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
					final String inMessage = in.readLine();
					
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							if(inMessage.contains("%OK%")) {
								callback.onSuccess(tag);
							} else {
								callback.onFailed(tag);
							}
						}
					});
				}
				
				s.close();
			} catch (IOException e) {	
				if(callback != null) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							callback.onFailed(tag);
						}
					});
				}
			}
			
			return null;
		}
	}
	
	public interface SendCallback {
		public void onSuccess(String tag);
		public void onFailed(String tag);
	}
}
