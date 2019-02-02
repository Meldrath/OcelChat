package NodekaChat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import NodekaChat.Channels.defaultChannels;
import com.lsd.umc.util.AnsiTable;

import java.util.Queue;


/**
 *
 * @author Isaac
 */
public class Message {

    protected void globalMessage(String messageType, String message) {
        for (User u : NodekaChat.onlineUsers) {
            u.getOutput().println(Channels.defaultChannels.valueOf(messageType).toString() + message);
        }
    }

    protected void broadcastMessage(String message, User user) {
        boolean findRoom = false;
        for (defaultChannels r : defaultChannels.values()) {
            if (user.getCurrentChannel().toUpperCase().equals(r.name().toUpperCase())) {
                findRoom = true;
                for (User u : NodekaChat.onlineUsers) {
                    if (!u.isBlind() && (u.getCurrentChannel() == null ? user.getCurrentChannel() == null : u.getCurrentChannel().equals(user.getCurrentChannel()))) {
                        u.getOutput().println(defaultChannels.valueOf(user.getCurrentChannel().toUpperCase()).toString() + user.getDisplayName() + AnsiTable.getCode("white") + ": " + message);
                    }
                }
                break;
            }
        }
        if (!findRoom) {
            for (User u : NodekaChat.onlineUsers) {
                if (!u.isBlind() && (u.getCurrentChannel() == null ? user.getCurrentChannel() == null : u.getCurrentChannel().equals(user.getCurrentChannel()))) {
                    u.getOutput().println(AnsiTable.getCode("light red") + "<"
                            + AnsiTable.getCode("red") + "< "
                            + AnsiTable.getCode("white") + user.getCurrentChannel()
                            + AnsiTable.getCode("red") + " >"
                            + AnsiTable.getCode("light red") + "> "
                            + AnsiTable.getCode("white") + user.getDisplayName() + AnsiTable.getCode("white") + ": " + message);
                }
            }
        }

    }

    protected void broadcastMessage(String messageType, String message, User user) {
        boolean findRoom = false;
        for (defaultChannels r : defaultChannels.values()) {
            if (messageType == null ? r.name() == null : messageType.equals(r.name().toUpperCase())) {
                findRoom = true;
                for (User u : NodekaChat.onlineUsers) {
                    if (!u.isBlind() && (u.getCurrentChannel() == null ? user.getCurrentChannel() == null : u.getCurrentChannel().equals(user.getCurrentChannel()))) {
                        u.getOutput().println(defaultChannels.valueOf(messageType) + user.getDisplayName() + AnsiTable.getCode("white") + ": " + message);
                    }
                }
                break;
            }
        }
        if (!findRoom) {
            for (User u : NodekaChat.onlineUsers) {
                if (!u.isBlind() && (u.getCurrentChannel() == null ? user.getCurrentChannel() == null : u.getCurrentChannel().equals(user.getCurrentChannel()))) {
                    u.getOutput().println(AnsiTable.getCode("light red") + "<"
                            + AnsiTable.getCode("red") + "< "
                            + AnsiTable.getCode("yellow") + user.getCurrentChannel()
                            + AnsiTable.getCode("red") + " >"
                            + AnsiTable.getCode("light red") + "> "
                            + AnsiTable.getCode("white") + user.getDisplayName() + AnsiTable.getCode("white") + ": " + message);
                }
            }
        }
    }

    protected void gameMessage(Queue<User> userQueue, String message) {
        for (User u : userQueue) {
            u.getOutput().println(defaultChannels.GAMBLE.toString() + message);
        }
    }

    protected void adminMessage(String message) {
        for (User u : NodekaChat.onlineUsers) {
            if (u.isAdminChannel()) {
                u.getOutput().println(defaultChannels.ADMIN.toString() + message);
            }
        }
    }

    protected void commandMessage(String message, User u) {
        u.getOutput().println(message);
    }

    protected void personalMessage(String messageType, String message, User u) {
        u.getOutput().println(defaultChannels.valueOf(messageType).toString() + message);
    }

    protected void personalMessage(String messageType, String message, User u, User user) {
        u.getOutput().println(AnsiTable.getCode("red") + ">> "
                + AnsiTable.getCode("cyan") + user.getLoginName()
                + AnsiTable.getCode("red") + " << "
                + AnsiTable.getCode("white") + message);
    }
}
