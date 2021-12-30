package Message;

import java.io.Serializable;
import java.time.LocalTime;

public class Message implements Serializable {
    String from;
    String to;
    String info;
    LocalTime time;
    TYPE type;

    public enum TYPE{
        SYS,CLIENT,NOTIFY,EXIT
    }

    public Message() {
    }

    public Message(String from, String to, String info, LocalTime time) {
        this.from = from;
        this.to = to;
        this.info = info;
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", info='" + info + '\'' +
                ", time=" + time +
                ", type=" + type +
                '}';
    }
}
