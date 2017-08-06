package NodekaChat;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.lsd.umc.util.AnsiTable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Isaac
 */
public class Channels extends NodekaChat {

    public enum defaultChannels {

        LOBBY {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("light black") + "<"
                        + AnsiTable.getCode("yellow") + "< "
                        + AnsiTable.getCode("white") + "Lobby"
                        + AnsiTable.getCode("yellow") + " >"
                        + AnsiTable.getCode("light black") + "> "
                        + AnsiTable.getCode("white");
                    }
                },
        WARROOM {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("light black") + "<"
                        + AnsiTable.getCode("red") + "< "
                        + AnsiTable.getCode("light red") + "War Room"
                        + AnsiTable.getCode("red") + " >"
                        + AnsiTable.getCode("light black") + "> "
                        + AnsiTable.getCode("white");
                    }
                },
        LOGIN {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("light blue") + "<"
                        + AnsiTable.getCode("light magenta") + "< "
                        + AnsiTable.getCode("white") + "Login"
                        + AnsiTable.getCode("light blue") + " >"
                        + AnsiTable.getCode("light magenta") + "> "
                        + AnsiTable.getCode("white");
                    }
                },
        ADMIN {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("white") + "("
                        + AnsiTable.getCode("magenta") + "( "
                        + AnsiTable.getCode("light magenta") + "Admin"
                        + AnsiTable.getCode("magenta") + " )"
                        + AnsiTable.getCode("white") + ") "
                        + AnsiTable.getCode("white");
                    }
                },
        GLOBAL {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("light black") + "<"
                        + AnsiTable.getCode("light black") + "< "
                        + AnsiTable.getCode("light red") + "HAL"
                        + AnsiTable.getCode("light black") + " >"
                        + AnsiTable.getCode("light black") + "> "
                        + AnsiTable.getCode("white");
                    }
                },
        GAMBLE {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("light magenta") + "<"
                        + AnsiTable.getCode("light black") + "< "
                        + AnsiTable.getCode("yellow") + "Gamble"
                        + AnsiTable.getCode("light black") + " >"
                        + AnsiTable.getCode("light magenta") + "> "
                        + AnsiTable.getCode("white");
                    }
                },
        SERVER {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("light black") + ">"
                        + AnsiTable.getCode("light black") + "> "
                        + AnsiTable.getCode("light red") + "HAL"
                        + AnsiTable.getCode("light black") + " <"
                        + AnsiTable.getCode("light black") + "< "
                        + AnsiTable.getCode("white");
                    }
                },
        MAIL {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("green") + "<"
                        + AnsiTable.getCode("light black") + "< "
                        + AnsiTable.getCode("white") + "Mail"
                        + AnsiTable.getCode("light black") + " >"
                        + AnsiTable.getCode("green") + "> "
                        + AnsiTable.getCode("white");
                    }
                },
        LOCATION {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("green") + "<"
                        + AnsiTable.getCode("light green") + "< "
                        + AnsiTable.getCode("white") + "Location"
                        + AnsiTable.getCode("light green") + " >"
                        + AnsiTable.getCode("green") + "> "
                        + AnsiTable.getCode("white");
                    }
                },
        AUCTION {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("blue") + "<"
                        + AnsiTable.getCode("light blue") + "< "
                        + AnsiTable.getCode("white") + "Auction"
                        + AnsiTable.getCode("light blue") + " >"
                        + AnsiTable.getCode("blue") + "> "
                        + AnsiTable.getCode("white");
                    }
                },
        VOID {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("yellow") + "<"
                        + AnsiTable.getCode("yellow") + "< "
                        + AnsiTable.getCode("light black") + "The Void Echoes"
                        + AnsiTable.getCode("yellow") + " >"
                        + AnsiTable.getCode("yellow") + "> "
                        + AnsiTable.getCode("light black");
                    }
                },
        MISFITS {
                    @Override
                    public String toString() {
                        return AnsiTable.getCode("red") + "<"
                        + AnsiTable.getCode("light green") + "< "
                        + AnsiTable.getCode("white") + "Misfits"
                        + AnsiTable.getCode("light green") + " >"
                        + AnsiTable.getCode("red") + "> "
                        + AnsiTable.getCode("white");
                    }
                },

    }

    private static final String defaultChannel = "Misfits";
    private final List<String> channels = new ArrayList<>(Arrays.asList("Lobby", "War Room", "Auction", "Misfits"));

    /**
     * @return the defaultRoom
     */
    public static String getDefaultChannel() {
        return defaultChannel;
    }

    public boolean deleteChannel(String room) {
        for (String s : channels) {
            if (room.equalsIgnoreCase(s)) {
                if (room.equalsIgnoreCase("Lobby") || room.equalsIgnoreCase("War Room") || room.equalsIgnoreCase("Auction") || room.equalsIgnoreCase("Misfits")) {
                    return false;
                } else {
                    for (User u : onlineUsers) {
                        if (u.getCurrentChannel().equalsIgnoreCase(room)) {
                            new Message().personalMessage("SERVER", "The channel you were in has been deleted. You are being moved to the 'Lobby' now.", u);
                            u.setCurrentChannel("Lobby");
                        }
                    }
                    return channels.remove(this.equalsIgnoreCase(channels, room));
                }
            }
        }
        return false;
    }

    public String equalsIgnoreCase(List<String> l1, String search) {
        for (String s : l1) {
            if (s.equalsIgnoreCase(search)) {
                return s;
            }
        }
        return null;
    }

    /**
     * @return the rooms
     */
    public List<String> getChannels() {
        Collections.sort(channels);
        return channels;
    }

    /**
     * @param room the rooms to add to room array
     */
    public void addChannel(String room) {
        this.channels.add(room);
    }

    public boolean moveChannel(String user, String channel) {
        for (User u : onlineUsers) {
            if (u.getLoginName().equalsIgnoreCase(user)) {
                for (String c : getChannels()) {
                    if (c == null ? channel == null : c.equalsIgnoreCase(channel)) {
                        new Message().personalMessage("SERVER", "You have been moved to channel: " + channel, u);
                        u.setCurrentChannel(channel);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param channel
     * @return
     */
    public int usersInRoomCount(String channel) {
        int amount = 0;
        if (onlineUsers.size() > 0 && !channel.isEmpty()) {
            for (User p : onlineUsers) {
                if (p.getCurrentChannel() == null || !p.getCurrentChannel().equalsIgnoreCase(channel)) {
                } else {
                    amount++;
                }
            }
            return amount;
        }
        return 0;
    }
}
