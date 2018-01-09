package com.akexorcist.simpletcp;

import android.annotation.SuppressLint;
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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Akexorcist on 10/1/2018 AD.
 */

public class ContinuousTcpClient implements ContinuousTcpServiceImp {
    public static final String TAG = ContinuousTcpClient.class.getSimpleName();

    private Socket socket;
    private TcpService service;
    private InetAddress inetAddress;

    private String ipAddress = "192.168.1.1";
    private int port = 2000;
    private boolean isRunning = false;
    private boolean isConnected = false;

    private TcpConnectionListener tcpConnectionListener;

    public ContinuousTcpClient(int port, TcpConnectionListener listener) {
        this.port = port;
        this.tcpConnectionListener = listener;
    }

    @SuppressLint("NewApi")
    public void start() {
        if (!isRunning) {
            service = new TcpService(port, this);
            service.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
            isRunning = true;
        }
    }

    public void stop() {
        if (this.isRunning) {
            this.service.killTask();
            this.isRunning = false;
        }
        disconnect();
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return this.port;
    }

    @Override
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    @Override
    public TcpConnectionListener getTcpConnectionListener() {
        return tcpConnectionListener;
    }

    public InetAddress getTargetInetAddress() {
        return this.inetAddress;
    }

    @SuppressLint("NewApi")
    private static class TcpService extends AsyncTask<Void, Void, Void> {
        private ServerSocket serverSocket;

        private int port;
        private boolean isTaskRunning = true;

        private ContinuousTcpServiceImp continuousTcpServiceImp;

        public TcpService(int port, ContinuousTcpServiceImp continuousTcpServiceImp) {
            this.port = port;
            this.continuousTcpServiceImp = continuousTcpServiceImp;
        }

        public void killTask() {
            isTaskRunning = false;
        }

        protected Void doInBackground(Void... params) {
            while (isTaskRunning) {
                try {
                    serverSocket = new ServerSocket(port);
                    serverSocket.setSoTimeout(1000);
                    continuousTcpServiceImp.setSocket(serverSocket.accept());
                    continuousTcpServiceImp.setInetAddress(continuousTcpServiceImp.getSocket().getInetAddress());
                    continuousTcpServiceImp.setConnected(true);
                    final String hostName = continuousTcpServiceImp.getInetAddress().getHostName();
                    final String hostAddress = continuousTcpServiceImp.getInetAddress().getHostAddress();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            if (continuousTcpServiceImp.getTcpConnectionListener() != null) {
                                continuousTcpServiceImp.getTcpConnectionListener().onConnected(hostName, hostAddress, continuousTcpServiceImp.getSocket());
                            }
                        }
                    });

                } catch (IOException e) {
                    Log.w(TAG, "Socket Timeout");
                }

                while (isTaskRunning && continuousTcpServiceImp.isConnected() && continuousTcpServiceImp.getSocket() != null) {
                    try {
                        continuousTcpServiceImp.getSocket().setSoTimeout(1000);
                        BufferedReader in = new BufferedReader(new InputStreamReader(continuousTcpServiceImp.getSocket().getInputStream()));
                        char ch_array[] = new char[100];
                        in.read(ch_array);
                        final String incomingMsg = new String(ch_array).trim();

                        if (continuousTcpServiceImp.getTcpConnectionListener() != null && incomingMsg.length() != 0) {
                            final String hostAdderss = continuousTcpServiceImp.getInetAddress().getHostAddress();

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    continuousTcpServiceImp.getTcpConnectionListener().onDataReceived(incomingMsg, hostAdderss);
                                }
                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    if (continuousTcpServiceImp.getTcpConnectionListener() != null)
                                        continuousTcpServiceImp.getTcpConnectionListener().onDisconnected();
                                }
                            });

                            continuousTcpServiceImp.getSocket().close();
                            continuousTcpServiceImp.setSocket(null);
                        }
                    } catch (NullPointerException e) {
                        continuousTcpServiceImp.setConnected(false);
                    } catch (SocketTimeoutException e) {
                        //e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (serverSocket != null)
                        serverSocket.close();
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public interface TcpConnectionListener {
        void onConnected(String hostName, String hostAddress, Socket socket);

        void onDisconnected();

        void onDataReceived(String message, String ipAddress);
    }

    public int getTargetPort() {
        return this.port;
    }

    public String getTargetIP() {
        return this.ipAddress;
    }

    @SuppressLint({"NewApi"})
    public void connect(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        new TCPConnection(ipAddress, port, null, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
    }

    @SuppressLint("NewApi")
    public void connect(String ipAddress, int port, ConnectionCallback callback) {
        this.ipAddress = ipAddress;
        this.port = port;
        new TCPConnection(ipAddress, port, callback, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
    }

    @SuppressLint("NewApi")
    private static class TCPConnection extends AsyncTask<Void, Void, Void> {
        private String ipAddress;
        private int port;

        private ConnectionCallback callback;
        private ContinuousTcpServiceImp continuousTcpServiceImp;

        public TCPConnection(String ipAddress, int port, ConnectionCallback callback, ContinuousTcpServiceImp continuousTcpServiceImp) {
            this.ipAddress = ipAddress;
            this.port = port;
            this.callback = callback;
            this.continuousTcpServiceImp = continuousTcpServiceImp;
        }

        protected Void doInBackground(Void... params) {
            try {
                continuousTcpServiceImp.setSocket(new Socket());
                continuousTcpServiceImp.getSocket().connect((new InetSocketAddress(InetAddress.getByName(ipAddress), port)), 5000);

                continuousTcpServiceImp.setInetAddress(continuousTcpServiceImp.getSocket().getInetAddress());

                final String hostName = continuousTcpServiceImp.getInetAddress().getHostName();
                final String hostAddress = continuousTcpServiceImp.getInetAddress().getHostAddress();
                continuousTcpServiceImp.setConnected(true);

                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            callback.onConnected(hostName, hostAddress);
                        }
                    });
                }
            } catch (final UnknownHostException e) {
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            callback.onConnectionFailed(ipAddress, e);
                        }
                    });
                }
            } catch (final IOException e) {
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            callback.onConnectionFailed(ipAddress, e);
                        }
                    });
                }
            }
            return null;
        }
    }

    public void disconnect() {
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException ignored) {
            }
            this.socket = null;
        }
    }

    public void send(String message) {
        if (this.socket != null)
            new TCPSend(socket, message, ipAddress, null).execute();
    }

    public void send(String message, SendCallback callback) {
        if (this.socket != null)
            new TCPSend(socket, message, ipAddress, callback).execute();
    }

    private static class TCPSend extends AsyncTask<Void, Void, Void> {
        private Socket socket;

        private String message;
        private String ipAddress;

        private SendCallback callback;

        public TCPSend(Socket socket, String message, String ipAddress, SendCallback callback) {
            this.socket = socket;
            this.message = message;
            this.ipAddress = ipAddress;
            this.callback = callback;
        }

        protected Void doInBackground(Void... params) {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                bufferedWriter.write(message);
                bufferedWriter.flush();

                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            callback.onSuccess(message, ipAddress);
                        }
                    });
                }
            } catch (final IOException exception) {
                if (callback != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            callback.onFailed(message, ipAddress, exception);
                        }
                    });
                }
            }
            return null;
        }
    }

    public interface ConnectionCallback {
        void onConnected(String hostName, String hostAddress);

        void onConnectionFailed(String ip, Exception e);
    }

    public interface SendCallback {
        void onSuccess(String message, String ip);

        void onFailed(String message, String ip, Exception e);
    }
}
