package NodekaChat;

/*
 NodekaChat.NodekaChat core concept is based off of http://cs.lmu.edu/~ray/notes/javanetexamples/
 All code is by: Isaac Croas.
 Open to permissible use, please contact me at incroas@hotmail.com to use the source code.
 */

import com.lsd.umc.util.AnsiTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCPIPConnection extends NodekaChat implements Runnable {

    private Announcements announcements;
    private HibernateUtil hibernate = new HibernateUtil();
    private final Pattern p = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
    private Message message = new Message();
    private User user = new User();

    /**
     *
     * @param key
     * @param value
     * @param map
     */
    protected void addElementToQueue(String key, String value, ConcurrentHashMap map) {
        Queue<String> q = (Queue<String>) map.get(key);
        if (q == null) {
            q = new ConcurrentLinkedQueue<>();
            Queue<String> curQ = (Queue<String>) map.putIfAbsent(key, q);
            if (curQ != null) {
                q = curQ;
            }
        }
        q.add(value);
    }

    protected void quit(User u) throws IOException {
        onlineUsers.remove(u);
        u.getSocket().shutdownOutput();
        u.getSocket().shutdownInput();
        u.getSocket().close();
        u.setStop(false);
        message.globalMessage("LOGIN", u.getLoginName() + " (" + u.getDisplayName() + AnsiTable.getCode("white") + ") has left the server.");
    }

    protected TCPIPConnection(Socket socket) {
        user.setSocket(socket);
        try {
            user.setOutput(new PrintWriter(user.getSocket().getOutputStream(), true));
            user.setInput(new BufferedReader(new InputStreamReader(user.getSocket().getInputStream())));
        } catch (Exception e) {
            System.out.println("Issue at socket creation.");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void run() {
        while (user.isStop()) {
            String clientVersion = "";
            boolean connectedClient = true;

            try {
                while (user.isStop()) {
                    user.getOutput().println("VERSION");
                    try {
                        clientVersion = user.getInput().readLine().trim();
                    } catch (IOException | NumberFormatException | NullPointerException e) {
                        System.out.println(clientVersion + " - version issue.");
                    }
                    if (clientVersion.isEmpty() || !clientVersion.equals(version)) {
                        message.personalMessage("LOGIN", "OcelChat: Your version is out of date. Please visit https://s3-us-west-2.amazonaws.com/ocelplugins/Plugins/OcelChat.zip for the newest version.", user);
                        connectedClient = false;
                        break;
                    }

                    user.getOutput().println("SUBMITNAME");
                    user.setDisplayName(user.getInput().readLine().trim());
                    user.setLoginName(user.getDisplayName().replaceAll("\033\\[\\d+;\\d+m", "").replaceAll("\033\\[\\d+m", ""));
                    user.getOutput().println("PASSWORD");
                    user.setPassword(user.getInput().readLine().trim());

                    if (hibernate.verifyUser(user)) {
                        break;
                    } else {
                        message.personalMessage("LOGIN", "OcelChat: " + AnsiTable.getCode("grey") + "Invalid login for " + AnsiTable.getCode("yellow") + user.getLoginName() + AnsiTable.getCode("grey") + ".", user);
                        connectedClient = false;
                        break;
                    }
                }

                //DC the client if they fail the login
                if (!connectedClient) {
                    user.getSocket().shutdownOutput();
                    user.getSocket().shutdownInput();
                    user.getSocket().close();
                    break;
                }

                //Check for if they are already logged in, remove the old login.
                for (User u : onlineUsers) {
                    try {
                        if (u.getLoginName() == null ? user.getLoginName() == null : u.getLoginName().equals(user.getLoginName())) {
                            u.getOutput().println("QUIT");
                            quit(u);
                        }
                    } catch (Exception e) {
                        System.out.println("Issue at overlog.");
                        System.out.println(Arrays.toString(e.getStackTrace()));
                    }
                }

                onlineUsers.add(user);
                message.commandMessage("REGISTERCHAT", user);

                user.getOutput().println(AnsiTable.getCode("light green") + " ::::::::   ::::::::  :::::::::: :::           ::::::::  :::    :::     ::: :::::::::::");
                user.getOutput().println(AnsiTable.getCode("light magenta") + ":+:    :+: :+:    :+  :+:      : :+:          :+:    :+  :+:    :+:   :+: :+:   :+:   :");
                user.getOutput().println(AnsiTable.getCode("light red") + "+:      :+ +:         +:+        +:+          +:         +:+    +:+  +:+   +:+  +:+");
                user.getOutput().println(AnsiTable.getCode("red") + "+#      :+ +          +#++:++#   +#+          +          +#++:++#++ +#++:++#++: +#+");
                user.getOutput().println(AnsiTable.getCode("light black") + "+#      #+ +#         +#+        +#+      +   +#         +#+    +#+ +#+     +#+ +#+");
                user.getOutput().println(AnsiTable.getCode("yellow") + "#+#    #+# #+#    +#  #+#      # #+#     ##   #+#    +#  #+#    #+# #+#     #+# #+#");
                user.getOutput().println(AnsiTable.getCode("light blue") + " ########   ########  ########## ##########    ########  ###    ### ###     ### ###");

                message.personalMessage("SERVER", "MOTD - " + motd, user);
                message.personalMessage("SERVER", onlineUsers.size() + " users currently logged in.", user);
                message.personalMessage("SERVER", String.valueOf(channel.usersInChannelCount(user.getCurrentChannel())) + " users currently in " + user.getCurrentChannel() + ".", user);
                for (User u : onlineUsers) {
                    if (u.isAdministrator() && u.isDeleteUser()) {
                        message.personalMessage("SERVER", AnsiTable.getCode("yellow") + "[ " + AnsiTable.getCode("light red") + "SUPER ADMIN" + AnsiTable.getCode("yellow") + " ] " + AnsiTable.getCode("white") + u.getLoginName() + " (" + u.getDisplayName() + AnsiTable.getCode("white") + ") in channel: " + u.getCurrentChannel(), user);
                    } else if (u.isAdministrator()) {
                        message.personalMessage("SERVER", AnsiTable.getCode("red") + "[ " + AnsiTable.getCode("light green") + "ADMIN" + AnsiTable.getCode("red") + " ] " + AnsiTable.getCode("white") + u.getLoginName() + " (" + u.getDisplayName() + AnsiTable.getCode("white") + ") in channel: " + u.getCurrentChannel(), user);
                    } else {
                        message.personalMessage("SERVER", u.getLoginName() + " (" + u.getDisplayName() + AnsiTable.getCode("white") + ") in channel: " + u.getCurrentChannel(), user);
                    }
                }
                message.globalMessage("LOGIN", user.getLoginName() + " (" + user.getDisplayName() + ") has joined the server.");
                message.adminMessage(user.getLoginName() + " has joined with IP address of: " + user.getSocket().getInetAddress().getHostAddress());

                for (String s : mailBox.keySet()) {
                    if (s == null ? user.getLoginName() == null : s.equals(user.getLoginName())) {
                        if (mailBox.get(s).peek() != null) {
                            message.personalMessage("MAIL", "You have mail. Please check via #ochat MAIL", user);
                        }
                    }
                }

                String input;
                Matcher m;
                while (user.isStop() && user.getTimeout() < 3) {
                    input = user.getInput().readLine();
                    
                    if (!input.contains("ALIVE")) {
                        System.out.println(input);
                    }
                    
                    List<String> parsedInput = new ArrayList<>();
                    m = p.matcher(input);
                    while (m.find()) {
                        parsedInput.add(m.group(1).replace("\"", ""));
                    }

                    if (parsedInput.size() > 0) {
                        if (!parsedInput.isEmpty()) {
                            if (parsedInput.get(0).replaceAll("\033\\[\\d+;\\d+m", "").replaceAll("\033\\[\\d+m", "").matches("\\[PK\\]")) {
                                parsedInput.remove(0);
                                parsedInput.remove(0);
                            } else if (parsedInput.get(0).replaceAll("\033\\[\\d+;\\d+m", "").replaceAll("\033\\[\\d+m", "").matches("\\d+|NA")) {
                                parsedInput.remove(0);
                            }
                        }

                        switch (parsedInput.get(0).replaceAll("\033\\[\\d+;\\d+m", "").replaceAll("\033\\[\\d+m", "")) {
                            case "LEAVE":
                                quit(user);
                                break;
                            case "ALIVE":
                                user.setTimeout(0);
                                break;
                            case "ADDUSER":
                                if (user.isAdministrator() && user.isAddUser() && parsedInput.size() == 3) {
                                    hibernate.addUser(parsedInput.get(1), parsedInput.get(1), Boolean.parseBoolean(parsedInput.get(2)));
                                    message.adminMessage(user.getLoginName() + " has added " + parsedInput.get(1) + "!!");
                                } else if (parsedInput.size() != 3) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat ADD <Name> <Admin>.", user);
                                } else {
                                    message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                }
                                break;
                            case "DELETEUSER":
                                if (user.isAdministrator() && user.isBan() && parsedInput.size() == 2) {
                                    User deleteTarget = hibernate.searchForUser(parsedInput.get(1));
                                    if (deleteTarget.getLoginName() != null) {
                                        hibernate.deleteUser(deleteTarget);
                                        message.adminMessage(user.getLoginName() + " has removed " + deleteTarget.getLoginName() + "!!");
                                    } else {
                                        message.personalMessage("SERVER", "Error deleting user.", user);
                                    }
                                } else if (parsedInput.size() != 2) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat ADD <Name> <Admin>.", user);
                                } else {
                                    message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                }
                                break;
                            case "PASSWORD":
                                if (parsedInput.size() == 2) {
                                    User searchTarget = hibernate.searchForUser(user.getLoginName());
                                    hibernate.updatePassword(searchTarget, parsedInput.get(1));
                                    user.setPassword(parsedInput.get(1));
                                    message.adminMessage(user.getLoginName() + " has updated their password!!");
                                    message.personalMessage("SERVER", "Your password has been updated to " + user.getPassword(), user);
                                } else if (parsedInput.size() != 2) {
                                    message.personalMessage("SERVER", "Invalid password, please follow the syntax #ochat PASSWORD <password>.", user);
                                } else {
                                    message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                }
                                break;
                            case "WHERE":
                                if (parsedInput.size() == 1) {
                                    for (User u : onlineUsers) {
                                        message.commandMessage("ROOM", u);
                                    }
                                    message.globalMessage("GLOBAL", AnsiTable.getCode("light red") + "WARNING:" + AnsiTable.getCode("white") + " intrusion detection! Location comprimised by " + user.getLoginName() + "!");
                                } else if (parsedInput.size() == 2) {
                                    boolean foundUser = false;
                                    for (User u : onlineUsers) {
                                        if (u.getLoginName() == null ? parsedInput.get(1) == null : u.getLoginName().equalsIgnoreCase(parsedInput.get(1).toLowerCase())) {
                                            foundUser = true;
                                            message.personalMessage("SERVER", AnsiTable.getCode("light red") + "WARNING:" + AnsiTable.getCode("white") + " intrusion detection! Location comprimised by " + user.getLoginName() + "!", u);
                                            message.commandMessage("ROOM", u);
                                            break;
                                        }
                                    }
                                    if (!foundUser) {
                                        message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                    }
                                } else {
                                    message.personalMessage("SERVER", "Invalid where, please follow the syntax #ochat WHERE or #ochat WHERE <user>.", user);
                                }
                                break;

                            case "LOCATION":
                                if (!parsedInput.get(1).isEmpty() || parsedInput.get(1) != null) {
                                    message.globalMessage("LOCATION", user.getLoginName() + ": " + parsedInput.get(1));
                                }
                                break;
                            case "TOGGLE":
                                boolean roomExists = false;
                                if (parsedInput.size() == 2) {
                                    for (String s : channel.getChannels()) {
                                        if (parsedInput.get(1).equalsIgnoreCase(s)) {
                                            if (user.isSubscribedToChannel(s)) {
                                                user.deleteChannelFromSubscription(s);
                                                message.personalMessage("SERVER", "You are now unsubscribed to the channel: " + s + ".", user);
                                                message.globalMessage("GLOBAL", user.getDisplayName() + " " + AnsiTable.getCode("white") + "has left the channel: " + s + ".");
                                            } else {
                                                user.addChannelToSubscription(s);
                                                message.globalMessage("GLOBAL", user.getDisplayName() + " " + AnsiTable.getCode("white") + "has joined the channel: " + s + ".");
                                                message.personalMessage("SERVER", "You are now subscribed to the channel: " + s + ".", user);
                                                message.personalMessage("SERVER", String.valueOf(channel.usersInChannelCount(s)) + " users currently in " + s + ".", user);
                                            }
                                        }
                                    }
                                } else if (parsedInput.size() != 2 || !roomExists) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat TOGGLE <channel>.", user);
                                }
                                break;
                            case "WHO":
                                message.personalMessage("SERVER", onlineUsers.size() + " users currently logged in.", user);
                                for (User u : onlineUsers) {
                                    if (u.isAdministrator() && u.isDeleteUser()) {
                                        message.personalMessage("SERVER", AnsiTable.getCode("yellow") + "[ " + AnsiTable.getCode("light red") + "SUPER ADMIN" + AnsiTable.getCode("yellow") + " ] " + AnsiTable.getCode("white") + u.getLoginName() + " (" + u.getDisplayName() + AnsiTable.getCode("white") + ") in channel: " + u.getCurrentChannel(), user);
                                    } else if (u.isAdministrator()) {
                                        message.personalMessage("SERVER", AnsiTable.getCode("red") + "[ " + AnsiTable.getCode("light green") + "ADMIN" + AnsiTable.getCode("red") + " ] " + AnsiTable.getCode("white") + u.getLoginName() + " (" + u.getDisplayName() + AnsiTable.getCode("white") + ") in channel: " + u.getCurrentChannel(), user);
                                    } else {
                                        message.personalMessage("SERVER", u.getLoginName() + " (" + u.getDisplayName() + AnsiTable.getCode("white") + ") in channel: " + u.getCurrentChannel(), user);
                                    }
                                }
                                break;
                            case "KICK":
                                if (user.isAdministrator() && user.isKick() && parsedInput.size() == 2) {
                                    boolean failedSearch = true;
                                    for (User u : onlineUsers) {
                                        if (u.getLoginName().equals(parsedInput.get(1))) {
                                            failedSearch = false;
                                            message.adminMessage(user.getLoginName() + " has kicked " + u.getLoginName() + "!");
                                            message.globalMessage("SERVER", u.getLoginName() + " - Don't let the door hit your ass on the way out!");
                                            quit(u);

                                        }
                                    }
                                    if (failedSearch) {
                                        message.personalMessage("SERVER", "User not online, check user list.", user);
                                    }
                                } else if (parsedInput.size() != 2) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat KICK <user>.", user);
                                } else {
                                    message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                }
                                break;
                            case "HELP":
                                Collections.sort(user.getCommands());
                                if (user.isAdministrator()) {
                                    Collections.sort(user.getAdminCommands());
                                    for (String s : user.getAdminCommands()) {
                                        message.personalMessage("SERVER", s, user);
                                    }
                                }
                                for (String s : user.getCommands()) {
                                    message.personalMessage("SERVER", s, user);
                                }
                                break;
                            case "ADMIN":
                                if (user.isAdminChannel() && parsedInput.size() == 2) {
                                    message.adminMessage(user.getLoginName() + ": " + parsedInput.get(1));
                                } else if (parsedInput.size() != 2) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat ADMIN \"<message>\".", user);
                                } else {
                                    message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                }
                                break;
                            case "ADDCHANNEL":
                                roomExists = false;
                                if (parsedInput.size() == 2) {
                                    for (String s : channel.getChannels()) {
                                        if (parsedInput.get(1).toLowerCase().equals(s.toLowerCase())) {
                                            roomExists = true;
                                            message.personalMessage("SERVER", "This channel name already exists.", user);
                                        }
                                    }
                                    if (!roomExists) {
                                        channel.addChannel(parsedInput.get(1).substring(0, 1).toUpperCase().concat(parsedInput.get(1).substring(1, parsedInput.get(1).length())).trim());
                                        message.globalMessage("GLOBAL", "A new channel has been created: " + parsedInput.get(1).substring(0, 1).toUpperCase().concat(parsedInput.get(1).substring(1, parsedInput.get(1).length())).trim() + ".");
                                    }
                                } else if (parsedInput.size() != 2) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat ADDCHANNEL <channel>.", user);
                                } else {
                                    message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                }
                                break;
                            case "DELETECHANNEL":
                                if (parsedInput.size() == 2) {
                                    if (channel.deleteChannel(parsedInput.get(1).trim())) {
                                        message.adminMessage(user.getLoginName() + " has deleted the channel " + parsedInput.get(1) + "!!");
                                    } else {
                                        message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                    }
                                } else if (parsedInput.size() != 2) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat DELETECHANNEL <channel>.", user);
                                }
                                break;
                            case "CHANNELS":
                                message.personalMessage("SERVER", "Available Channels:", user);
                                for (String s : channel.getChannels()) {
                                    //for (String sub : user)
                                    message.personalMessage("SERVER", s, user);
                                }
                                break;
                            case "SUB":
                                message.personalMessage("SERVER", "Subscribed Channels:", user);
                                for (String s : user.subscribedChannels()) {
                                    message.personalMessage("SERVER", s, user);
                                }
                                break;
                            case "VOID":
                                if (user.isAdministrator() && user.isAdminChannel() && parsedInput.size() == 2) {
                                    message.globalMessage("VOID", parsedInput.get(1));
                                } else {
                                    message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                }
                                break;
                            case "SHUTDOWN":
                                if (user.isAdministrator() && user.isBan()) {
                                    message.adminMessage(user.getLoginName() + " has shut down the server.");
                                    System.exit(0);
                                }
                                break;
                            case "MAIL":
                                boolean send = false;
                                if (parsedInput.size() == 3) {
                                    send = (parsedInput.get(1) == null ? user.getLoginName() != null : !parsedInput.get(1).equals(user.getLoginName()));
                                    if (send) {
                                        addElementToQueue(parsedInput.get(1), user.getLoginName() + ": " + parsedInput.get(2), mailBox);
                                        message.personalMessage("MAIL", "You have sent mail to " + parsedInput.get(1) + ".", user);
                                    }
                                } else if (!send && parsedInput.size() == 1) {
                                    for (String s : mailBox.keySet()) {
                                        if (s == null ? user.getLoginName() == null : s.equals(user.getLoginName())) {
                                            if (mailBox.get(s).peek() == null) {
                                                message.personalMessage("MAIL", "You currently have no mail.", user);
                                            }
                                            while (mailBox.get(s).peek() != null) {
                                                message.personalMessage("MAIL", mailBox.get(s).poll(), user);
                                            }
                                        }
                                    }
                                } else if (parsedInput.size() != 3) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat MAIL <user> \"<message>\" or to check your mail #ochat MAIL.", user);
                                }
                                break;
                            case "PM":
                                if (parsedInput.size() == 3) {
                                    boolean online = false;
                                    for (User u : onlineUsers) {
                                        if ((user.getLoginName().toLowerCase() == null ? u.getLoginName().toLowerCase() != null : !user.getLoginName().toLowerCase().equals(u.getLoginName().toLowerCase())) && u.getLoginName().toLowerCase().equals(parsedInput.get(1).toLowerCase())) {
                                            online = true;
                                            message.personalMessage("PRIVATE", "Whispers: " + AnsiTable.getCode("grey") + parsedInput.get(2), u, user);
                                            message.personalMessage("PRIVATE", "You Whisper: " + AnsiTable.getCode("grey") + parsedInput.get(2), user, u);
                                        }
                                    }
                                    if (!online) {
                                        message.personalMessage("SERVER", parsedInput.get(1) + " is not online. If you wish to send them a message, you can send them a MAIL.", user);
                                    }
                                } else {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat PM <user> \"<message>\"", user);
                                }
                                break;
                            case "MOTD":
                                message.personalMessage("SERVER", motd, user);
                                break;
                            case "MOTDUPDATE":
                                if (parsedInput.size() == 2) {
                                    announcements.removeAnnouncement("MOTD - " + motd);
                                    motd = parsedInput.get(1);
                                    announcements.addAnnouncement("MOTD - " + motd);
                                    message.globalMessage("GLOBAL", "MOTD - " + motd);
                                } else if (parsedInput.size() != 2) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat MOTDUPDATE <motd>.", user);
                                } else {
                                    message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                }

                                break;
                            case "DICE":
                                Dice dice = new Dice();
                                int rollOne = dice.rollDiceWithSides(6);
                                int rollTwo = dice.rollDiceWithSides(6);

                                if (rollOne == 1 && rollTwo == 1) {
                                    message.globalMessage("GAMBLE", user.getDisplayName() + AnsiTable.getCode("white") + " has rolled Snake Eyes!!");
                                } else if (rollOne + rollTwo == 3) {
                                    message.globalMessage("GAMBLE", user.getDisplayName() + AnsiTable.getCode("white") + " has rolled an Ace Deuce!!");
                                } else if (rollOne == 6 && rollTwo == 6) {
                                    message.globalMessage("GAMBLE", "This is " + AnsiTable.getCode("red") + "MADNESS"
                                            + AnsiTable.getCode("white") + "! " + user.getDisplayName()
                                            + AnsiTable.getCode("white") + " has rolled Two Taters?!");
                                } else if (rollOne + rollTwo == 7) {
                                    message.globalMessage("GAMBLE", user.getDisplayName() + AnsiTable.getCode("white") + " has rolled a Lucky 7!!");
                                } else if (rollOne == rollTwo && (rollOne + rollTwo == 6)) {
                                    message.globalMessage("GAMBLE", user.getDisplayName() + AnsiTable.getCode("white") + " has rolled a Hard Tater!!");
                                } else if (rollOne + rollTwo == 6) {
                                    message.globalMessage("GAMBLE", user.getDisplayName() + AnsiTable.getCode("white") + " has rolled a Soft Tater!!");
                                } else if (rollOne == rollTwo) {
                                    message.globalMessage("GAMBLE", user.getDisplayName() + AnsiTable.getCode("white") + " has rolled a Hard " + (rollOne + rollTwo) + "!!");
                                } else {
                                    message.globalMessage("GAMBLE", user.getDisplayName() + AnsiTable.getCode("white") + " has rolled a " + rollOne + " and " + rollTwo + " for: " + (rollOne + rollTwo) + "!");
                                }
                                break;
                            case "VERSION":
                                message.personalMessage("SERVER", "Current OcelChat server version is: " + version + ".", user);
                                break;
                            case "IP":
                                message.personalMessage("SERVER", "Current OcelChat external server IP is: " + NodekaChat.IPv4_Address.getIP(), user);
                                break;
                            case "BLACKJACK":
                                if (parsedInput.size() == 1) {
                                    BLACKJACK.joinRoom(user);
                                } else if (parsedInput.size() == 2) {
                                    if (parsedInput.get(1).equalsIgnoreCase("quit")) {
                                        BLACKJACK.quit(user);
                                    }
                                }
                                break;
                            case "TAG":
                                if (parsedInput.size() == 2) {
                                    boolean findUser = false;
                                    for (User u : onlineUsers) {
                                        if (u.getDisplayName().equals(parsedInput.get(1)) || (u.getLoginName().equals(parsedInput.get(1)) && !u.getLoginName().equals(user.getLoginName()))) {
                                            findUser = true;
                                            message.personalMessage("SERVER", "Nickname already exists.", user);
                                            break;
                                        }
                                    }
                                    if (!findUser) {
                                        user.setOldDisplayName(user.getDisplayName());
                                        user.setDisplayName(parsedInput.get(1));
                                        message.globalMessage("GLOBAL", user.getOldDisplayName() + AnsiTable.getCode("white") + "'s tag is now " + user.getDisplayName() + AnsiTable.getCode("white") + "!");
                                        break;
                                    }
                                } else {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat TAG \"<tag>\".", user);
                                }
                                break;
                            case "CHANGETAG":
                                if (user.isAdministrator() && user.isChangeTag() && parsedInput.size() == 3) {
                                    boolean findUser = false;
                                    for (User u : onlineUsers) {
                                        if (u.getLoginName().equals(parsedInput.get(1))) {
                                            findUser = true;
                                            if (findUser && (u.getDisplayName().toLowerCase() == null ? parsedInput.get(2).toLowerCase() != null : !u.getDisplayName().toLowerCase().equals(parsedInput.get(2).toLowerCase()))) {
                                                u.setOldDisplayName(u.getDisplayName());
                                                u.setDisplayName(parsedInput.get(2));
                                                message.globalMessage("GLOBAL", u.getLoginName() + AnsiTable.getCode("white") + "'s tag is now " + u.getDisplayName() + AnsiTable.getCode("white") + "!");
                                                message.adminMessage(user.getLoginName() + " has changed " + u.getLoginName() + "'s tag to: " + u.getDisplayName() + ".");
                                                break;
                                            } else {
                                                message.personalMessage("SERVER", "Nickname already exists.", user);
                                                break;
                                            }
                                        }
                                    }
                                    if (!findUser) {
                                        message.personalMessage("SERVER", "User not online, check user list.", user);
                                    }
                                } else if (parsedInput.size() != 3) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat CHANGETAG <user> \"<tag>\".", user);
                                } else {
                                    message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                }
                                break;
                            case "LISTFTP":
                                Files.walk(Paths.get("C:\\Users\\Isaac\\Desktop\\OcelChatFTP"), 2).forEach(filePath -> {
                                    if (Files.isDirectory(filePath)) {
                                        System.out.println(filePath.getFileName());
                                    } else if (Files.isRegularFile(filePath)) {
                                        System.out.println(filePath.getFileName());
                                    }
                                });
                                break;
                            case "FTP":
                                user.getOutput().print("FTP");
                                break;
                            case "MUTE":
                                if (user.isAdministrator() && user.isKick() && parsedInput.size() == 2) {
                                    boolean findUser = false;
                                    for (User u : onlineUsers) {
                                        if (u.getLoginName().equals(parsedInput.get(1))) {
                                            if (u.isAdministrator()) {
                                                break;
                                            }
                                            if (!u.isMuted()) {
                                                u.setMuted(true);
                                                message.adminMessage(user.getLoginName() + " has muted " + u.getLoginName() + ".");
                                            } else {
                                                u.setMuted(false);
                                                message.adminMessage(user.getLoginName() + " has unmuted " + u.getLoginName() + ".");
                                            }
                                            findUser = true;
                                        }
                                    }
                                    if (!findUser) {
                                        message.personalMessage("SERVER", "User not online, check user list.", user);
                                    }
                                } else if (parsedInput.size() != 2) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat MUTE <user>.", user);
                                } else {
                                    message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                }
                                break;
                            case "BLIND":
                                if (user.isAdministrator() && user.isCanBlind() && parsedInput.size() == 2) {
                                    boolean findUser = false;
                                    for (User u : onlineUsers) {
                                        if (u.getLoginName().equals(parsedInput.get(1))) {
                                            if (u.isAdministrator()) {
                                                break;
                                            }
                                            if (!u.isBlind()) {
                                                u.setBlind(true);
                                                message.adminMessage(user.getLoginName() + " has blinded " + u.getLoginName() + ".");
                                            } else {
                                                u.setBlind(false);
                                                message.adminMessage(user.getLoginName() + " has unblinded " + u.getLoginName() + ".");
                                            }
                                            findUser = true;
                                        }
                                    }
                                    if (!findUser) {
                                        message.personalMessage("SERVER", "User not online, check user list.", user);
                                    }
                                } else if (parsedInput.size() != 2) {
                                    message.personalMessage("SERVER", "Invalid command, please follow the syntax #ochat KICK <user>.", user);
                                } else {
                                    message.personalMessage("SERVER", "I'm sorry, " + user.getLoginName() + ". I'm afraid I can't do that.", user);
                                }
                                break;
                            default:
                                if (user.isMuted()) {
                                    message.personalMessage("SERVER", "You have been muted.", user);
                                } else {
                                    message.broadcastMessage(input, user);

                                }
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Ungraceful exit...");
                System.out.println(Arrays.toString(e.getStackTrace()));
                System.out.println(e.getMessage());
                System.out.println(e.getClass());
            } finally {
                // Only do this quit if we've ungracefully exited, and they are a connectedClient client.
                if ((user.isStop() && connectedClient) || user.getTimeout() > 2) {
                    try {
                        BLACKJACK.quit(user);
                        quit(user);
                    } catch (Exception e) {
                        System.out.println("Issue at thread termination.");
                        System.out.println(Arrays.toString(e.getStackTrace()));
                    }
                }
            }
        }
    }
}
