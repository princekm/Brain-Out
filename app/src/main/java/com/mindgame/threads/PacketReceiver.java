package com.mindgame.threads;

import android.util.Log;

import com.mindgame.connection.ConnectionHelper;
import com.mindgame.connection.MultiPlayerHandler;
import com.mindgame.connection.MulticastReceiver;

import java.io.IOException;

public class PacketReceiver extends Thread {
    private MultiPlayerHandler multiPlayerHandler;
    private static final String DEBUG_STRING = "packetreceiver";

    private MulticastReceiver multicastReceiver;

    public PacketReceiver(MultiPlayerHandler multiPlayerHandler) {
        this.multiPlayerHandler = multiPlayerHandler;
        multicastReceiver = multiPlayerHandler.getMulticastReceiver();

    }

    @Override
    public void run() {
        while (multicastReceiver == null) ;
        while (multicastReceiver.isOpen()) {
            byte[] buf = new byte[ConnectionHelper.MAX_SIZE];
            try {
                multicastReceiver.receive(buf);
                multiPlayerHandler.handlePacket(buf);
            } catch (IOException e) {
                Log.e(DEBUG_STRING, "receiver error");
            }
        }
    }
}
