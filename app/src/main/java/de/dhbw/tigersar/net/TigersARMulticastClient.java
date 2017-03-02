package de.dhbw.tigersar.net;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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
        socket.joinGroup(group);
        Log.d(TAG, "Multicast client created");
    }

    public OnNewMessageCallback getCallback() {
        return callback;
    }

    public void setCallback(OnNewMessageCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        if (!running) {
            Log.d(TAG, "Multicast client running...");

            running = true;

            byte[] bytes = new byte[32000 + 1];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

            while (running) {

                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

                    socket.receive(packet);

                    TigersARProtos.ARMessage newMessage = TigersARProtos.ARMessage.parseDelimitedFrom(byteArrayInputStream);

                    byteArrayInputStream.close();

                    if (message == null || newMessage.getTimestamp() > message.getTimestamp()) {
                        message = newMessage;
                        if (callback != null) {
                            callback.onNewMessage(newMessage);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                packet.setLength(0);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                socket.leaveGroup(group);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Multicast client stopped");
        }
    }

    public void stop() {
        Log.d(TAG, "Multicast client stopping...");
        running = false;
    }
}
