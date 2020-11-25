package com.mindgame.connection;

import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class ConnectionHelper {
    private static  ConnectionHelper connectionHelper=null;
    private static MulticastSocket multicastSocket=null;
    private final static String GROUP_IP="228.5.6.7";

    public InetAddress getGroup() {
        return group;
    }

    private InetAddress group;

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    private InetAddress inetAddress;
    public final static int MAX_SIZE=1000;
    public final static int  PORT=5567;

    public MulticastReceiver getMulticastReceiver() {
        return multicastReceiver;
    }

    public MulticastSender getMulticastSender() {
        return multicastSender;
    }

    private MulticastReceiver multicastReceiver;
    private MulticastSender multicastSender;

        public static InetAddress getInetAddr() {
            InetAddress inetAddress=null;
            try {
                List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif: all)
                {
                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                    Enumeration<InetAddress> en = nif.getInetAddresses();
                    while (en.hasMoreElements())
                    {
                        inetAddress= en.nextElement();
                        if ((inetAddress instanceof Inet4Address))
                            break;
                    }

                }
            } catch (Exception ex) {}
            return inetAddress;
        }
    public static String getMacAddr() {
        try {
            List <NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif: all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b: macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }


    public ConnectionHelper()  {

        try {
            group = InetAddress.getByName(GROUP_IP);
            joinGroup();
             inetAddress =getInetAddr();
             createMulticastReceiver();
             createMulticastSender();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static synchronized ConnectionHelper getInstance() throws UnknownHostException {
        if(connectionHelper==null)
        {
            connectionHelper = new ConnectionHelper();
        }
        return connectionHelper;
    }
    private MulticastSocket getMulticastSocket() throws IOException {
        if(multicastSocket==null)
        {
            multicastSocket = new MulticastSocket(PORT);

        }
        return multicastSocket;
    }
    public void createMulticastReceiver()
    {
        try {
            multicastReceiver = new MulticastReceiver(getMulticastSocket());
//            multicastSender.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createMulticastSender()
    {
        try {
            multicastSender = new MulticastSender(getMulticastSocket());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void joinGroup() throws IOException {
        getMulticastSocket().joinGroup(group);
    }
    public void removeGroup() throws IOException {
        getMulticastSocket().leaveGroup(group);
    }
}
