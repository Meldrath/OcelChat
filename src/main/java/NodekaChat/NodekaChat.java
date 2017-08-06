package NodekaChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.*;

/**
 * A multi-threaded chat room server. When a client connects the server requests
 * a screen displayName by sending the client the text "SUBMITNAME", and keeps
 * requesting a displayName until a unique one is received. After a client
 * submits a unique displayName, the server acknowledges with "NAMEACCEPTED".
 */
public class NodekaChat {

    //ansiStrippedName, outputWriter
    //ansiStrippedName, list of mail
    public static String version = "1.10.00";
    public static ConcurrentLinkedQueue<User> onlineUsers = new ConcurrentLinkedQueue<>();
    public static ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> mailBox = new ConcurrentHashMap<>(8, 0.9f, 1);
    public static String motd = "Welcome to OcelChat version 1.1! Type #ochat HELP for a list of available commands.";
    public static Blackjack BLACKJACK = new Blackjack();
    public static Channels room = new Channels();
    public static Announcements announcements = new Announcements();
    private static final Shutdown sd = new Shutdown();

    public static void main(String[] args) throws Exception {
        final int PORT = 9002;
        ServerSocket serverSocket = new ServerSocket(PORT);
        sd.attachShutDownHook(onlineUsers);

        System.out.println("NodekaChat is now online.\n"
                + "Port: " + PORT
                + "\nInternal IP Address: " + InetAddress.getLocalHost().getHostAddress()
                + "\nExternal IP Address: " + IPv4_Address.getIP());

        Runnable blackjackThread = BLACKJACK;
        ScheduledExecutorService blackjackThreadService = Executors.newSingleThreadScheduledExecutor();
        blackjackThreadService.scheduleAtFixedRate(blackjackThread, 10, 3, TimeUnit.MINUTES);

        Runnable announcementService = new Announcements();
        ScheduledExecutorService announcementsThreadService = Executors.newSingleThreadScheduledExecutor();
        announcementsThreadService.scheduleAtFixedRate(announcementService, 2, new Random().nextInt(200), TimeUnit.MINUTES);

        Runnable heartbeat = new Heartbeat();
        ScheduledExecutorService heartbeatThreadService = Executors.newSingleThreadScheduledExecutor();
        heartbeatThreadService.scheduleAtFixedRate(heartbeat, 20, 30, TimeUnit.SECONDS);

        while (true) {
            try {
                Socket connection = serverSocket.accept();
                Runnable runnable = new TCPIPConnection(connection);
                Thread thread = new Thread(runnable);
                thread.start();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    protected static class IPv4_Address {

        protected static String getIP() throws Exception {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(
                        whatismyip.openStream()));
                return in.readLine();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        }
    }
}
