package Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Channel {
    private String name;
    private ObjectOutputStream out;
    private ConcurrentLinkedQueue<Message> list;

    public Channel() {
        this.list = new ConcurrentLinkedQueue<>();
    }

    public void offer(Message msg) {
        list.offer(msg);
    }

    public Message poll() {
        return list.poll();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public void consume() throws IOException {
        while (!list.isEmpty()) {
            Message msg = list.poll();
            out.writeObject(msg);
            out.flush();
        }
    }
}
