package NodekaChat;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.*;

public class NodekaChat {

    public static String version = "1.10.00";
    public static ConcurrentLinkedQueue<User> onlineUsers = new ConcurrentLinkedQueue<>();
    public static ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> mailBox = new ConcurrentHashMap<>(8, 0.9f, 1);
    public static String motd = "Welcome to OcelChat version 1.1! Type #ochat HELP for a list of available commands.";
    public static Blackjack BLACKJACK = new Blackjack();
    private static final String TOKEN = "NDUwODgxMjgzMDY1OTA1MTUz.DfInnA.Gs_P6mHTtslkMhIxJY6d9pWDuQc";
    public static Channels channel = new Channels();
    private static final Shutdown sd = new Shutdown();

    public static void main(String[] args) {
        final int PORT = 9002;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        sd.attachShutDownHook(onlineUsers);

        try {
            System.out.println("NodekaChat is now online.\n"
                    + "Port: " + PORT
                    + "\nInternal IP Address: " + InetAddress.getLocalHost().getHostAddress()
                    + "\nExternal IP Address: " + IPv4_Address.getIP());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runnable blackjackThread = BLACKJACK;
        ScheduledExecutorService blackjackThreadService = Executors.newSingleThreadScheduledExecutor();
        blackjackThreadService.scheduleAtFixedRate(blackjackThread, 10, 3, TimeUnit.MINUTES);

        Runnable announcementService = new Announcements();
        ScheduledExecutorService announcementsThreadService = Executors.newSingleThreadScheduledExecutor();
        announcementsThreadService.scheduleAtFixedRate(announcementService, 2, new Random().nextInt(200), TimeUnit.MINUTES);

        Runnable heartbeat = new Heartbeat();
        ScheduledExecutorService heartbeatThreadService = Executors.newSingleThreadScheduledExecutor();
        heartbeatThreadService.scheduleAtFixedRate(heartbeat, 20, 30, TimeUnit.SECONDS);

        JDABuilder builder = new JDABuilder(AccountType.BOT);

        builder.setToken(TOKEN)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.of(Game.GameType.DEFAULT, "666"))
                .addEventListener(new Discord())
                .setAutoReconnect(true);

        try {
            JDA jda = builder.buildAsync();
        } catch (LoginException e) {
            System.out.println(e.getStackTrace());
        }

        while (true) try {
            Socket connection = serverSocket.accept();
            Runnable runnable = new TCPIPConnection(connection);
            Thread thread = new Thread(runnable);
            thread.start();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
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
                        System.out.println(e.getStackTrace());
                    }
                }
            }
        }
    }
}
