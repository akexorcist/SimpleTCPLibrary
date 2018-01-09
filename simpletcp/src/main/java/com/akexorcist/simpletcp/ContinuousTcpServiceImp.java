package com.akexorcist.simpletcp;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Akexorcist on 10/1/2018 AD.
 */

public interface ContinuousTcpServiceImp {
    Socket getSocket();

    void setSocket(Socket socket);

    InetAddress getInetAddress();

    void setInetAddress(InetAddress inetAddress);

    int getPort();

    void setConnected(boolean isConnected);

    boolean isConnected();

    ContinuousTcpClient.TcpConnectionListener getTcpConnectionListener();
}
