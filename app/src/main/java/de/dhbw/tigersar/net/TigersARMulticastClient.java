package de.dhbw.tigersar.net;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import de.dhwb.tigersar.TigersARProtos;

/**
 * Created by lschmierer on 01/03/2017.
 */

public class TigersARMulticastClient implements Runnable {

    private static final String TAG = TigersARMulticastClient.class.getSimpleName();

    public interface OnNewMessageCallback {
        void onNewMessage(TigersARProtos.ARMessage message);
    }

    private boolean running;
    private InetAddress group;
    private MulticastSocket socket;
    private TigersARProtos.ARMessage message;

    private OnNewMessageCallback callback;

    public TigersARMulticastClient(InetAddress group, int port) throws IOException {
        this.group = group;
        socket = new MulticastSocket(port);
        socket.setNetworkInterface(getWlanEth());
        Log.d(TAG, "Multicast client created");
    }

    public OnNewMessageCallback getCallback() {
        return callback;
    }

    public void setCallback(OnNewMessageCallback callback) {
        Log.d(TAG, "callback set");
        this.callback = callback;
    }

    @Override
    public void run() {
        if (!running) {
            try {
                socket.joinGroup(group);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Multicast client running...");

            running = true;

            byte[] bytes = new byte[32000 + 1];
            while (running) {

                try {
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

                    socket.receive(packet);

                    ByteArrayInputStream byteArrayInputStream =
                            new ByteArrayInputStream(bytes);

                    TigersARProtos.ARMessage newMessage = TigersARProtos.ARMessage.parseDelimitedFrom(byteArrayInputStream);

                    byteArrayInputStream.close();

                    if (newMessage != null && (message == null || newMessage.getTimestamp() > message.getTimestamp())) {
                        message = newMessage;
                        if (callback != null) {
                            callback.onNewMessage(newMessage);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            running = false;
            Log.d(TAG, "Multicast client stopped");
        }
    }

    public void stop() {
        Log.d(TAG, "Multicast client stopping...");
        running = false;
        socket.close();
    }

    private static NetworkInterface getWlanEth() throws SocketException {
        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
        NetworkInterface wlan0;
        while (enumeration.hasMoreElements()) {
            wlan0 = enumeration.nextElement();
            if (wlan0.getName().equals("wlan0")) {
                Log.i(TAG, "wlan0 found");
                return wlan0;
            }
        }

        return null;
    }
}
