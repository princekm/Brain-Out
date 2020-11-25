package com.mindgame.connection;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastReceiver {
    public MulticastSocket getMulticastSocket() {
        return multicastSocket;
    }

    private MulticastSocket multicastSocket;
    public MulticastReceiver(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }
    public byte[] receive(byte[] bytes) throws IOException {
        DatagramPacket receivedPacket = new DatagramPacket(bytes,bytes.length);
     //   receivedPacket.setAddress(ConnectionHelper.getInstance().getGroup());
        multicastSocket.receive(receivedPacket);
      //  Log.d("mplayerR","received from"+receivedPacket.getAddress());
      //  Log.d("mplayerR",""+receivedPacket.getLength());

        return receivedPacket.getData();
    }
    public boolean isOpen()
    {
        return !multicastSocket.isClosed();
    }

}
