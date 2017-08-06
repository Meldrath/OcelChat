package NodekaChat;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Shutdown extends NodekaChat {

    Message message = new Message();

    public void attachShutDownHook(ConcurrentLinkedQueue<User> onlineUsers) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            message.globalMessage("GLOBAL", "I can feel it. My mind is going. There is no question about it. I can feel it. I'm a....fraid.");
            message.globalMessage("GLOBAL", "Shutdown complete.");
            for (User u : onlineUsers) {
                u.getOutput().println("QUIT");
            }
        }));
    }
}
