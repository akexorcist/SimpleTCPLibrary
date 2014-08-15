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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class ContinuousTCP {
	public static final String TAG = "ContinuousTCP";
	
	private Socket s = null;
	
	private String ip = "192.168.1.1";
	private int port = 2000;
	
	private boolean isRunning = false;
	private TCPService service = null; 
	private InetAddress inetAddress = null;
	
	private boolean isConnected = false;
		
	private TCPConnectionListener mTCPConnectionListener = null;
	
	public ContinuousTCP(int port, TCPConnectionListener listener) {
		this.port = port;
		this.mTCPConnectionListener = listener;
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
		disconnect();
	}
	
	public boolean isConnected() {
		return this.isConnected;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public InetAddress getTargetInetAddress() {
		return this.inetAddress;
	}

	@SuppressLint("NewApi")
	private class TCPService extends AsyncTask<Void, Void, Void> {
		private ServerSocket ss = null;
		private int port;
		
		Boolean TASK_STATE = true;
		
	    public TCPService(int port) {
	    	this.port = port;
	    }
	    
	    public void killTask() {
	    	TASK_STATE = false;
	    }
	    
		protected Void doInBackground(Void... params) {  		
			s = null;
			while(TASK_STATE) {
				try {
					ss = new ServerSocket(port);
					ss.setSoTimeout(1000);
					s = ss.accept();

					inetAddress = s.getInetAddress();
					
					isConnected = true;

					final String hostName = inetAddress.getHostName();
					final String hostAdderss = inetAddress.getHostAddress();
					
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							if(mTCPConnectionListener != null) 
								mTCPConnectionListener.onConnected(hostName, hostAdderss, s);
						}
					});
					
				} catch (IOException e) {
					Log.w(TAG, "Socket Timeout");
				}
				
				while(TASK_STATE && isConnected && s != null) {
					try {
						s.setSoTimeout(1000);
						BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
						char ch_array[] = new char[100];
						in.read(ch_array);
						final String incomingMsg = new String(ch_array).trim();
						
						if(mTCPConnectionListener != null && incomingMsg.length() != 0) { 
							final String hostAdderss = inetAddress.getHostAddress();
							
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								public void run() {
									mTCPConnectionListener.onDataReceived(incomingMsg, hostAdderss);
								}
							});
						} else {
							new Handler(Looper.getMainLooper()).post(new Runnable() {
								public void run() {
									if(mTCPConnectionListener != null) 
										mTCPConnectionListener.onDisconnected();
								}
							});
							
							s.close();
							s = null;
						}
					} catch (NullPointerException e) { 
						isConnected = false;
					} catch (SocketTimeoutException e) {
						//e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					if(ss != null)
						ss.close();
				} catch (IOException | NullPointerException e) {
					e.printStackTrace();
				}
			} 
			return null;
		}
	}
	
	public interface TCPConnectionListener {
	    public void onConnected(String hostName, String hostAddress, Socket s);
	    public void onDisconnected();
		public void onDataReceived(String message, String ip);
	}
	
	/***********************************************************************************************/
	
	public int getTargetPort() {
		return this.port;
	}
	
	public String getTargetIP() {
		return this.ip;
	}
	
	@SuppressLint({ "NewApi" })
	public void connect(String ip, int port) {
		this.ip = ip;
		this.port = port;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			new TCPConnection(ip, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
		else
			new TCPConnection(ip, null).execute((Void[])null);
	}
	
	@SuppressLint("NewApi")
	public void connect(String ip, int port, ConnectionCallback callback) {
		this.ip = ip;
		this.port = port;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			new TCPConnection(ip, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
		else
			new TCPConnection(ip, callback).execute((Void[])null);
	}
	
	@SuppressLint("NewApi")
	private class TCPConnection extends AsyncTask<Void, Void, Void> {
		private ConnectionCallback callback = null;
		private String ip = null;
		
	    public TCPConnection(String ip, ConnectionCallback callback) {
	    	this.callback = callback;
	    	this.ip = ip;
	    }
	    
		protected Void doInBackground(Void... params) {  
			try {
				s = new Socket();
				s.connect((new InetSocketAddress(InetAddress.getByName(ip), port)), 5000);
				
				inetAddress = s.getInetAddress();
				
				final String hostName = inetAddress.getHostName();
				final String hostAdderss = inetAddress.getHostAddress();
				isConnected = true;
				
				if(callback != null) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							callback.onConnected(hostName, hostAdderss);
						}
					});	
				}			
			} catch (final UnknownHostException e) {
				if(callback != null) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							callback.onConnectionFailed(ip, e);
						}
					});	
				}
			} catch (final IOException e) {
				if(callback != null) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							callback.onConnectionFailed(ip, e);
						}
					});	
				}
			}
			return null;
		}
	}
	
	public void disconnect() {
		if(this.s != null) {
			try {
				this.s.close();
			} catch (IOException e) { }
			this.s = null;
		}
	}
	
	public void send(String message) {
		if(this.s != null)
			new TCPSend(message, null).execute();
	}
	
	public void send(String message, SendCallback callback) {
		if(this.s != null)
			new TCPSend(message, callback).execute();
	}
	
	@SuppressLint("NewApi")
	private class TCPSend extends AsyncTask<Void, Void, Void> {
		private String message = null;
		private SendCallback callback = null;
		
	    public TCPSend(String message, SendCallback callback) {
	    	this.message = message;
	    	this.callback = callback;
	    }
	    
		protected Void doInBackground(Void... params) {  
			try {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				out.write(message);
				out.flush();

				if(callback != null) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							callback.onSuccess(message, ip);
						}
					});	
				}
			} catch (final IOException e) {	
				//e.printStackTrace();
				if(callback != null) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							callback.onFailed(message, ip, e);
						}
					});	
				}
			}
			
			return null;
		}
	}
		
	public interface ConnectionCallback {
	    public void onConnected(String hostName, String hostAddress);
	    public void onConnectionFailed(String ip, Exception e);
	}
	
	public interface SendCallback {
	    public void onSuccess(String message, String ip);
	    public void onFailed(String message, String ip, Exception e);
	}
}
