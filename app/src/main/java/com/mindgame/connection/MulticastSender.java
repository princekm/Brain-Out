package com.mindgame.connection;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class MulticastSender
{
    private MulticastSocket multicastSocket;
    public MulticastSender(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }
    public synchronized void send(byte[] bytes) throws IOException {
        DatagramPacket datagramPacket= new DatagramPacket(bytes,bytes.length,ConnectionHelper.getInstance().getGroup(),ConnectionHelper.PORT);
        multicastSocket.send(datagramPacket);
    }
}