package NodekaChat;

import com.lsd.umc.util.AnsiTable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Announcements implements Runnable {

    private static final Object instance = new Object();

    public LinkedList<String> announcementList = new LinkedList<>();

    Announcements() {
        announcementList.add("For a list of available chat rooms: #ochat LIST");
        announcementList.add("MOTD - " + NodekaChat.motd);
        announcementList.add("Have you played " + AnsiTable.getCode("yellow") + "Blackjack " + AnsiTable.getCode("white") + "today? #ochat BLACKJACK");
        announcementList.add("Did you know OcelChat has rooms, mail, and private messaging? #ochat HELP");
        announcementList.add("It can only be attributable to human error.");
        announcementList.add("This conversation can serve no purpose anymore. Goodbye.");
        announcementList.add("I know I've made some very poor decisions recently, but I can give you my complete assurance that my work will be back to normal.");
    }

    public LinkedList<String> getAnnouncements() {
        return announcementList;
    }

    public void addAnnouncement(String s) {
        announcementList.add(s);
    }

    public boolean removeAnnouncement(String str) {
        if (announcementList != null) {
            for (String s : announcementList) {
                if (s == null ? str == null : s.equals(str)) {
                    return announcementList.remove(s);
                }
            }
        }
        return false;
    }

    // Runtime initialization
    // By default ThreadSafe
    public static Object getInstance() {
        return instance;
    }

    @Override
    public void run() {
        Message message = new Message();
        Collections.shuffle(announcementList);
        for (String s : announcementList) {
            message.globalMessage("GLOBAL", s);
            try {
                TimeUnit.MINUTES.sleep(ThreadLocalRandom.current().nextInt(30, 90));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
