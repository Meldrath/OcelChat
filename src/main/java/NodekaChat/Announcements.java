package NodekaChat;//Move to Hibernate

import com.lsd.umc.util.AnsiTable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Leviticus
 */
public class Announcements extends NodekaChat implements Runnable {

    private static final Object instance = new Object();

    /**
     *
     */
    protected static final LinkedList<String> announcementList = new LinkedList<>();

    protected Announcements() {
        announcementList.add("For a list of available chat rooms: #ochat LIST");
        announcementList.add("MOTD - " + motd);
        announcementList.add("Have you played " + AnsiTable.getCode("yellow") + "Blackjack " + AnsiTable.getCode("white") + "today? #ochat BLACKJACK");
        announcementList.add("Did you know OcelChat has rooms, mail, and private messaging? #ochat HELP");
        announcementList.add("It can only be attributable to human error.");
        announcementList.add("This conversation can serve no purpose anymore. Goodbye.");
        announcementList.add("I know I've made some very poor decisions recently, but I can give you my complete assurance that my work will be back to normal.");
    }

    public void removeAnnouncements() {

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
    // By defualt ThreadSafe
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
                TimeUnit.MINUTES.sleep(new Random().nextInt(60));
            } catch (InterruptedException ex) {
                System.out.println("Announcement failed.");
            }
        }
    }
}
