package com.exchangerate.client;

import com.exchangerate.common.ExchangeRateData;
import com.exchangerate.client.gui.ExchangeRateFrame;

import java.net.*;

public class ExchangeRateClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9876;
    private static final int BUFFER_SIZE = 256;

    public void start() throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddr = InetAddress.getByName(SERVER_HOST);

        // gui goi tin dang ky den server
        byte[] sub = "SUBSCRIBE".getBytes();
        DatagramPacket subPacket = new DatagramPacket(sub, sub.length, serverAddr, SERVER_PORT);
        socket.send(subPacket);
        System.out.println("subscribed to server " + SERVER_HOST + ":" + SERVER_PORT);

        ExchangeRateFrame frame = new ExchangeRateFrame();

        // lang nghe goi du lieu tu server
        byte[] buf = new byte[BUFFER_SIZE];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String raw = new String(packet.getData(), 0, packet.getLength()).trim();
            ExchangeRateData data = ExchangeRateData.decode(raw);
            if (data != null) {
                frame.updateData(data);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new ExchangeRateClient().start();
    }
}
