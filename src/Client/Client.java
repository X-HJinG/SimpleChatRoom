package Client;

import Message.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Random;

public class Client extends JFrame implements Runnable {
    public static final int PORT = 10001;
    ClientGUI gui;
    Socket clientSocket;
    String nickName;
    ObjectInputStream in;
    ObjectOutputStream out;
    Thread thread;

    public Client() {
        thread = new Thread(this);
        JFrame.setDefaultLookAndFeelDecorated(true);
        JTextField textField = new JTextField(15);
        JButton login = new JButton("Login");
        setTitle("Login");
        setSize(400, 300);
        setLocation(400, 100);
        Container container = new Container();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        Container subContainer = new Container();
        subContainer.setLayout(new FlowLayout());
        subContainer.add(new JLabel("nickName:"));
        subContainer.add(textField);
        container.add(Box.createVerticalGlue());
        container.add(subContainer);
        container.add(login);
        container.add(Box.createVerticalGlue());
        add(container);
        login.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (textField.getText().length() != 0) {
                    nickName = textField.getText();
                } else {
                    Random random = new Random();
                    StringBuilder builder = new StringBuilder("AnonymousUser-");
                    for (int i = 0; i < 3; i++) {
                        int r = random.nextInt(26);
                        builder.append((char) (65 + r));
                    }
                    nickName = builder.toString();
                }
                dispose();
                connect();
            }
        });
        setVisible(true);
    }

    public void connect() {
        clientSocket = new Socket();
        InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", PORT);
        try {
            if (!clientSocket.isConnected()) {
                clientSocket.connect(serverAddress);
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
            }
            if (clientSocket.isConnected()) {
                writeMsg(nickName);
                gui = new ClientGUI(this);
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        thread.start();
    }

    @Override
    public void run() {
        String s;
        while (true) {
            try {
                Message msg = (Message) in.readObject();
                System.out.println(msg);
                switch (msg.getType()) {
                    case CLIENT:
                        if (msg.getTo() != null) {
                            if (msg.getFrom().equals(nickName)) {
                                s = "you " + " @ " + msg.getTo() + " : " + msg.getInfo();
                            } else {
                                s = msg.getFrom() + " @ " + "you : " + msg.getInfo();
                            }
                        } else {
                            s = msg.getFrom() + ": " + msg.getInfo();
                        }
                        gui.addChat(s);
                        break;
                    case SYS:
                        s = msg.getInfo();
                        gui.reNewList(s);
                        break;
                    case NOTIFY:
                        s = "Notify : " + msg.getInfo();
                        gui.addChat(s);
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("The connection has been disconnected.");
                break;
            }
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

    public void sendMsg(String to, String info) {
        Message message = new Message(nickName, to, info, LocalTime.now());
        message.setType(Message.TYPE.CLIENT);
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
