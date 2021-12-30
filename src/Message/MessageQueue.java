package Message;


import java.io.IOException;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {
    private static final LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    public static CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();
    public Channel channel;
    public boolean goon =true;

    public MessageQueue() {
        new Producer();
        new Consumer();
    }

    public class Producer implements Runnable {
        Thread thread;

        Producer() {
            thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            try {
                while (goon) {
                    produce();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class Consumer implements Runnable {
        Thread thread;

        Consumer() {
            thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            try {
                while (goon) {
                    consume();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void put(Message msg) throws InterruptedException {
        queue.put(msg);

    }

    public static Message take() throws InterruptedException {
        return queue.take();
    }


    public static void addChannel(Channel channel){
        channels.add(channel);
    }

    public static void removeChannel(String nickname){
        channels.removeIf(channel -> channel.getName().equals(nickname));
    }

    public int getSize() {
        return queue.size();
    }

    public static void produce() throws IOException, InterruptedException {
        Message msg = take();
        for (Channel channel : channels) {
            channel.offer(msg);
        }
    }

    public static void consume() throws IOException, InterruptedException {
        for (Channel channel : channels) {
            channel.consume();
        }
    }
}
