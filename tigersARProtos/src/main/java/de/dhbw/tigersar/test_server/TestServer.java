package de.dhbw.tigersar.test_server;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import de.dhwb.tigersar.TigersARProtos;

public class TestServer implements Runnable {

    private boolean running;
    private InetAddress group;
    private int port;
    private MulticastSocket socket;
    private TigersARProtos.ARMessage message;

    public TestServer(InetAddress group, int port) throws IOException {
        this.group = group;
        this.port = port;
        socket = new MulticastSocket(port);
        socket.joinGroup(group);
        System.out.println("server started");
    }

    public void setMessage(TigersARProtos.ARMessage message) {
        this.message = message;
    }

    @Override
    public void run() {
        if (!running) {
            System.out.println("server running");
            running = true;
            while (running) {
                if (message != null) {
                    try {
                        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(32000 + 1);

                        message.writeDelimitedTo(byteOutputStream);

                        byte[] bytes = byteOutputStream.toByteArray();
                        DatagramPacket packet = new DatagramPacket(bytes, byteOutputStream.size(), group, port);
                        socket.send(packet);

                        byteOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

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
            System.out.println("server stopped");
        }
    }

    public void stop() {
        System.out.println("server stopping...");
        running = false;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        TestServer testServer = new TestServer(InetAddress.getByName("225.225.125.225"), 25225);

        Thread testServerThread = new Thread(testServer);
        testServerThread.start();

        TigersARProtos.Field field = TigersARProtos.Field.newBuilder()
                .setWidth(1200)
                .setHeight(600)
                .build();

        TigersARProtos.ARMessage message = TigersARProtos.ARMessage.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setField(field)
                .build();

        System.out.println(message);
        testServer.setMessage(message);

        for (int i = 3; i > 0; i--) {
            System.out.println(i);
            Thread.sleep(1000);
        }
        testServer.stop();
    }
}
