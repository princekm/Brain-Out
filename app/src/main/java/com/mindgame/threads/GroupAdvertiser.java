package com.mindgame.threads;

import android.util.Log;
import com.mindgame.connection.MultiPlayerHandler;


public class GroupAdvertiser extends Thread {
    private String DEBUG_STRING = "groupadvertiser";

    private static int DELAY_MILLISECS = 1000;
    private MultiPlayerHandler multiPlayerHandler;

    private boolean paused;



    private boolean sendLeavePacket;
    private Object syncObject;


    public GroupAdvertiser(MultiPlayerHandler multiPlayerHandler) {
        this.multiPlayerHandler = multiPlayerHandler;
        syncObject = new Object();
        setPaused(false,true);
    }

    @Override
    public void run() {

        while (true) {
            // Do stuff.
            multiPlayerHandler.sendGroupAdvertisement();

            synchronized (syncObject) {
                while (isPaused()) {
                    try {
                        if(sendLeavePacket)
                            multiPlayerHandler.sendLeaveGroupPacket();
                        syncObject.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            try {
                Thread.sleep(DELAY_MILLISECS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused,boolean sendLeavePacket) {
        synchronized (syncObject)
        {
            this.paused=paused;
            setSendLeavePacket(sendLeavePacket);
            if(!paused)
              syncObject.notifyAll();
        }
    }
    public void setSendLeavePacket(boolean sendLeavePacket) {
        this.sendLeavePacket = sendLeavePacket;
    }

}