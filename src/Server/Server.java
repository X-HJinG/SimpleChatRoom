package Server;

import Message.*;


import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends ServerSocket implements Runnable {
    public static final int PORT = 10001;
    private static ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<>();
    private Boolean goon = true;

    public Server() throws IOException {
        super(PORT);
        System.out.println("Server start up...");
    }


    @Override
    public void run() {
        MessageQueue messageQueue = new MessageQueue();
        try {
            while (goon) {
                new Client(accept(),new Channel());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addClient(String nickName, Client client) throws InterruptedException {
        clients.put(nickName, client);
        reNew();
    }

    public static void removeClient(String nickName){
        clients.remove(nickName);
    }

    public static void reNew() throws InterruptedException {
        StringBuilder builder = new StringBuilder();
        for (String s : clients.keySet()) {
            builder.append(s).append(";");
        }
        Message message = new Message();
        message.setTo(null);
        message.setTime(LocalTime.now());
        message.setType(Message.TYPE.SYS);
        message.setInfo(builder.toString());
        MessageQueue.put(message);
    }


    public static void sendNotifyMsg(String str) throws InterruptedException {
        Message message = new Message();
        message.setType(Message.TYPE.NOTIFY);
        message.setTo(null);
        message.setTime(LocalTime.now());
        message.setInfo(str);
        MessageQueue.put(message);
    }


    public static void sendMsgToAll(Message msg) {
        for (Client client : clients.values()) {
            client.sendMsg(msg);
        }
    }

    public static void sendMsgToTarget(String from, String to, Message msg) {
        for (Client client : clients.values()) {
            if (client.nickName.equals(from) || client.nickName.equals(to)) {
                client.sendMsg(msg);
            }
        }
    }
}
