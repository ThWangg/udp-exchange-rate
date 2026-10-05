package com.exchangerate.server;

import com.exchangerate.common.ExchangeRateData;

import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class ExchangeRateServer {

    private static final int PORT = 9876;
    private static final int BUFFER_SIZE = 256;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    // danh sach client da dang ky
    private final Set<String> clients = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, InetAddress> clientAddresses = new ConcurrentHashMap<>();
    private final Map<String, Integer> clientPorts = new ConcurrentHashMap<>();

    // gia tri ty gia hien tai (lam base)
    private double tokyo = 5.00;
    private double paris = 5.00;
    private double seoul = 5.00;

    private final Random random = new Random();

    public void start() throws Exception {
        DatagramSocket socket = new DatagramSocket(PORT);
        System.out.println("server started on port " + PORT);

        // lang nghe yeu cau tu client
        Thread listenThread = new Thread(() -> listenForClients(socket));
        listenThread.setDaemon(true);
        listenThread.start();

        // gui ty gia moi giay den client
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> sendUnicast(socket), 1, 1, TimeUnit.SECONDS);
    }

    private void listenForClients(DatagramSocket socket) {
        byte[] buf = new byte[BUFFER_SIZE];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength()).trim();
                if (msg.equals("SUBSCRIBE")) {
                    String key = packet.getAddress().getHostAddress() + ":" + packet.getPort();
                    if (!clients.contains(key)) {
                        clients.add(key);
                        clientAddresses.put(key, packet.getAddress());
                        clientPorts.put(key, packet.getPort());
                        System.out.println("client registered: " + key);
                    }
                }
            } catch (Exception e) {
                System.err.println("listen error: " + e.getMessage());
            }
        }
    }

    // gui unicast den tung client
    private void sendUnicast(DatagramSocket socket) {
        // bien thien ty gia ngau nhien trong khoang -0.5 den +0.5, (gioi han 1 den 10)
        tokyo = clamp(tokyo + (random.nextDouble() - 0.5), 1.0, 10.0);
        paris = clamp(paris + (random.nextDouble() - 0.5), 1.0, 10.0);
        seoul = clamp(seoul + (random.nextDouble() - 0.5), 1.0, 10.0);

        String time = LocalTime.now().format(TIME_FMT);
        ExchangeRateData data = new ExchangeRateData(
                time,
                Math.round(tokyo * 100.0) / 100.0,
                Math.round(paris * 100.0) / 100.0,
                Math.round(seoul * 100.0) / 100.0);
        String encoded = data.encode();
        byte[] buf = encoded.getBytes();

        synchronized (clients) {
            for (String key : clients) {
                try {
                    InetAddress addr = clientAddresses.get(key);
                    int port = clientPorts.get(key);
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, addr, port);
                    socket.send(packet);
                } catch (Exception e) {
                    System.err.println("send error to " + key + ": " + e.getMessage());
                }
            }
        }
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static void main(String[] args) throws Exception {
        new ExchangeRateServer().start();
        // giu main thread song
        Thread.currentThread().join();
    }
}
