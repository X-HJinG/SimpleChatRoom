package Server;

import Message.*;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    Socket clientSocket;
    String nickName;
    ObjectInputStream in;
    ObjectOutputStream out;
    Boolean goon = true;
    Thread thread;

    public Client(Socket clientSocket, Channel channel) throws IOException, InterruptedException {
        super();
        this.clientSocket = clientSocket;
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        nickName = in.readUTF();
        Server.addClient(nickName, this);
        channel.setName(nickName);
        channel.setOut(out);
        MessageQueue.addChannel(channel);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (goon) {
                Message msg = (Message) in.readObject();
                sendToMQ(msg);
                /*
                    以下是非消息队列的版本的
                    if(msg.getTo()==null){
                    Server.sendMsgToAll(msg);
                    }else{
                        String src = msg.getFrom();
                        String target = msg.getTo();
                        Server.sendMsgToTarget(src,target,msg);
                    }
                */
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void sendMsg(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToMQ(Message msg) throws IOException, InterruptedException {
        MessageQueue.put(msg);
    }
}
