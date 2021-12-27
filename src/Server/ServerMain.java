package Server;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(server);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            System.out.print("请输入通知:");
            String msg = scanner.next();
            Server.sendNotifyMsg(msg);
        }
    }
}
