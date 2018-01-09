package com.akexorcist.simpletcp;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Akexorcist on 10/1/2018 AD.
 */

public class SimpleTcpClient {
    public static void send(String message, String ip, int port) {
        send(message, ip, port, null, null);
    }

    public static void send(String message, String ip, int port, SendCallback callback, String tag) {
        new TCPSend(message, ip, port, callback, tag).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
    }

    private static class TCPSend extends AsyncTask<Void, Void, Void> {
        private SendCallback callback;
        private String message;
        private String ip;
        private String tag;
        private int port;

        public TCPSend(String message, String ip, int port, SendCallback callback, String tag) {
            this.message = message;
            this.ip = ip;
            this.port = port;
            this.callback = callback;
            this.tag = tag;
        }

        protected Void doInBackground(Void... params) {
            try {
                Socket socket = new Socket(ip, port);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String outgoingMessage = message + System.getProperty("line.separator");
                bufferedWriter.write(outgoingMessage);
                bufferedWriter.flush();
                if (callback != null) {
                    socket.setSoTimeout(5000);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final String incomingMessage = bufferedReader.readLine();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            if (incomingMessage.contains("%OK%")) {
                                callback.onSuccess(tag);
                            } else {
                                callback.onFailed(tag);
                            }
                        }
                    });
                }
                socket.close();
            } catch (IOException e) {
                if (callback != null) {
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
        void onSuccess(String tag);

        void onFailed(String tag);
    }
}
