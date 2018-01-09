package com.akexorcist.simpletcp;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Akexorcist on 10/1/2018 AD.
 */

public class SimpleTcpServer implements SimpleTcpServiceImp {
    private static final String TAG = SimpleTcpServer.class.getSimpleName();

    private TcpService service;
    private InetAddress inetAddress;

    private int port = 2000;
    private boolean isRunning = false;
    private boolean isConnected = false;

    private OnDataReceivedListener dataReceivedListener;

    public SimpleTcpServer(int port) {
        this.port = port;
    }

    public void start() {
        if (!this.isRunning) {
            service = new TcpService(port, this);
            service.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {
            service.killTask();
            isRunning = false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getTargetInetAddress() {
        return inetAddress;
    }

    public void onMessageIncoming(String message, String ip) {
        dataReceivedListener.onDataReceived(message, ip);
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    @Override
    public OnDataReceivedListener getDataReceivedListener() {
        return dataReceivedListener;
    }

    @Override
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    private static class TcpService extends AsyncTask<Void, Void, Void> {
        private ServerSocket serverSocket;
        private SimpleTcpServiceImp simpleTcpServiceImp;

        private int port;
        private Boolean taskState = true;

        public TcpService(int port, SimpleTcpServiceImp simpleTcpServiceImp) {
            this.port = port;
            this.simpleTcpServiceImp = simpleTcpServiceImp;
        }

        public void killTask() {
            taskState = false;
        }

        protected Void doInBackground(Void... params) {
            Socket socket = null;
            while (taskState) {
                try {
                    serverSocket = new ServerSocket(port);
                    serverSocket.setSoTimeout(1000);
                    socket = serverSocket.accept();
                    simpleTcpServiceImp.setInetAddress(socket.getInetAddress());
                    simpleTcpServiceImp.setConnected(true);
                } catch (IOException e) {
                    Log.w(TAG, "Socket Timeout");
                }

                while (taskState && simpleTcpServiceImp.isConnected() && socket != null) {
                    try {
                        socket.setSoTimeout(1000);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        final String incomingMsg = in.readLine();

                        if (simpleTcpServiceImp.getDataReceivedListener() != null && incomingMsg.length() > 0) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    simpleTcpServiceImp.getDataReceivedListener().onDataReceived(incomingMsg, simpleTcpServiceImp.getInetAddress().getHostAddress());
                                }
                            });
                        }

                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        String outgoingMsg = "%OK%" + System.getProperty("line.separator");
                        out.write(outgoingMsg);
                        out.flush();

                    } catch (NullPointerException e) {
                        simpleTcpServiceImp.setConnected(false);
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (serverSocket != null)
                        serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        this.dataReceivedListener = listener;
    }

    public interface OnDataReceivedListener {
        void onDataReceived(String message, String ip);
    }
}
