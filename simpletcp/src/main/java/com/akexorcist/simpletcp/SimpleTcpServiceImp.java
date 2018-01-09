package com.akexorcist.simpletcp;

import java.net.InetAddress;

/**
 * Created by Akexorcist on 10/1/2018 AD.
 */

public interface SimpleTcpServiceImp {
    boolean isConnected();

    void setConnected(boolean isConnected);

    SimpleTcpServer.OnDataReceivedListener getDataReceivedListener();

    InetAddress getInetAddress();

    void setInetAddress(InetAddress inetAddress);
}
