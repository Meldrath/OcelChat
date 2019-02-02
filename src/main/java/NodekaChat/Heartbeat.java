package NodekaChat;

class Heartbeat implements Runnable {

    private final Object instance = new Object();

    protected Heartbeat() {
    }

    // Runtime initialization
    // By defualt ThreadSafe
    public Object getInstance() {
        return instance;
    }

    @Override
    public void run() {
        for (User u : NodekaChat.onlineUsers) {
            u.incrementTimeout();
            u.getOutput().println("BEAT");
        }
    }
}
