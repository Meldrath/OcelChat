package NodekaChat;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.lsd.umc.util.AnsiTable;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {

    private PrintWriter output;
    private BufferedReader input;
    private String currentChannel;
    private String defaultChannel = Channels.getDefaultChannel();
    private List<String> commands = new ArrayList<>();
    private List<String> adminCommands = new ArrayList<>();
    private String displayName;
    private String loginName;
    private String oldDisplayName;
    private String oldLoginName;
    private String password;
    private Socket socket;
    private ConcurrentLinkedQueue<String> userMailBox;
    private boolean stop = true;
    private int timeout = 0;

    private int id;
    private boolean administrator;
    private boolean adminChannel;
    private boolean kick;
    private boolean ban;
    private boolean changeTag;
    private boolean motdUpdate;
    private boolean changePassword;

    private boolean addUser;
    private boolean deleteUser;

    private boolean canBlind;
    private boolean blind = false;

    private boolean canMute;
    private boolean muteRoom;
    private boolean muteAll;
    private boolean muted = false;

    private boolean move;
    private boolean moveRoom;
    private boolean moveAll;

    private boolean addChannel;
    private boolean deleteChannel;

    User(String noAnsiName, String password, boolean admin) {
        this.userMailBox = new ConcurrentLinkedQueue<>();
        this.loginName = noAnsiName;
        this.password = password;
        genericCommands();
        this.administrator = admin;
    }

    User() {
        this.userMailBox = new ConcurrentLinkedQueue<>();
        genericCommands();
    }

    private void genericCommands() {
        addCommandToUser(AnsiTable.getCode("light black") + "("
                + AnsiTable.getCode("yellow") + "( "
                + AnsiTable.getCode("white") + " ~User Commands Available~ "
                + AnsiTable.getCode("yellow") + " )"
                + AnsiTable.getCode("light black") + ")");
        addCommandToUser("BLACKJACK");
        addCommandToUser("DICE");
        addCommandToUser("JOIN");
        addCommandToUser("LIST");
        addCommandToUser("MAIL");
        addCommandToUser("PASSWORD");
        addCommandToUser("PM");
        addCommandToUser("TAG");
        addCommandToUser("VERSION");
        addCommandToUser("WHO");
        addCommandToUser("WHERE");
    }

    public void addCommandToUser(String cmd) {
        getCommands().add(cmd);
    }

    public void incrementTimeout() {
        setTimeout(getTimeout() + 1);
    }

    /**
     * @return the output
     */
    public PrintWriter getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(PrintWriter output) {
        this.output = output;
    }

    /**
     * @return the input
     */
    public BufferedReader getInput() {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(BufferedReader input) {
        this.input = input;
    }

    /**
     * @return the defaultChannel
     */
    public String getDefaultChannel() {
        return defaultChannel;
    }

    /**
     * @param defaultChannel the defaultChannel to set
     */
    public void setDefaultChannel(String defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    /**
     * @return the currentChannel
     */
    public String getCurrentChannel() {
        return currentChannel;
    }

    /**
     * @param currentChannel the currentChannel to set
     */
    public void setCurrentChannel(String currentChannel) {
        this.currentChannel = currentChannel;
    }

    /**
     * @return the commands
     */
    public List<String> getCommands() {
        return commands;
    }

    /**
     * @param commands the commands to set
     */
    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the loginName
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * @param loginName the loginName to set
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return the oldDisplayName
     */
    public String getOldDisplayName() {
        return oldDisplayName;
    }

    /**
     * @param oldDisplayName the oldDisplayName to set
     */
    public void setOldDisplayName(String oldDisplayName) {
        this.oldDisplayName = oldDisplayName;
    }

    /**
     * @return the oldLoginName
     */
    public String getOldLoginName() {
        return oldLoginName;
    }

    /**
     * @param oldLoginName the oldLoginName to set
     */
    public void setOldLoginName(String oldLoginName) {
        this.oldLoginName = oldLoginName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @param socket the socket to set
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * @return the mailBox
     */
    public ConcurrentLinkedQueue<String> getMailBox() {
        return userMailBox;
    }

    /**
     * @param mailBox the mailBox to set
     */
    public void setMailBox(ConcurrentLinkedQueue<String> mailBox) {
        this.userMailBox = mailBox;
    }

    /**
     * @return the stop
     */
    public boolean isStop() {
        return stop;
    }

    /**
     * @param stop the stop to set
     */
    public void setStop(boolean stop) {
        this.stop = stop;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the administrator
     */
    public boolean isAdministrator() {
        return administrator;
    }

    /**
     * @param administrator the administrator to set
     */
    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
        this.adminChannel = administrator;
        if (administrator) {
            addAdminCommandToUser(AnsiTable.getCode("light magenta") + "("
                    + AnsiTable.getCode("white") + "( "
                    + AnsiTable.getCode("white") + " ~Admin Commands Available~ "
                    + AnsiTable.getCode("white") + " )"
                    + AnsiTable.getCode("light magenta") + ")");
            addAdminCommandToUser("ADMIN");
        }
    }

    /**
     * @return the kick
     */
    public boolean isKick() {
        return kick;
    }

    /**
     * @param kick the kick to set
     */
    public void setKick(boolean kick) {
        this.kick = kick;
        if (kick) {
            addAdminCommandToUser("KICK");
        }
    }

    /**
     * @return the mute
     */
    public boolean isCanMute() {
        return canMute;
    }

    /**
     * @param mute the mute to set
     */
    public void setCanMute(boolean mute) {
        this.canMute = mute;
        if (mute) {
            addAdminCommandToUser("MUTE");
        }
    }

    /**
     * @return the ban
     */
    public boolean isBan() {
        return ban;
    }

    /**
     * @param ban the ban to set
     */
    public void setBan(boolean ban) {
        this.ban = ban;
        if (ban) {
            addAdminCommandToUser("BAN");
        }
    }

    /**
     * @return the addUser
     */
    public boolean isAddUser() {
        return addUser;
    }

    /**
     * @param addUser the addUser to set
     */
    public void setAddUser(boolean addUser) {
        this.addUser = addUser;
        if (addUser) {
            addAdminCommandToUser("ADDUSER");
        }
    }

    public boolean isDeleteUser() {
        return deleteUser;
    }

    public void setDeleteUser(boolean deleteUser) {
        this.deleteUser = deleteUser;
        if (deleteUser) {
            addAdminCommandToUser("DELETEUSER");
        }
    }

    /**
     * @return the changeTag
     */
    public boolean isChangeTag() {
        return changeTag;
    }

    /**
     * @param changeTag the changeTag to set
     */
    public void setChangeTag(boolean changeTag) {
        this.changeTag = changeTag;
        if (changeTag) {
            addAdminCommandToUser("CHANGETAG");
        }
    }

    /**
     * @return the changeRoom
     */
    public boolean isMove() {
        return move;
    }

    /**
     * @param move
     */
    public void setMove(boolean move) {
        this.move = move;
        if (move) {
            addAdminCommandToUser("MOVE");
        }
    }

    /**
     * @return the muteRoom
     */
    public boolean isMuteRoom() {
        return muteRoom;
    }

    /**
     * @param muteRoom the muteRoom to set
     */
    public void setMuteRoom(boolean muteRoom) {
        this.muteRoom = muteRoom;
        if (muteRoom) {
            addAdminCommandToUser("MUTEROOM");
        }
    }

    /**
     * @return the moveAll
     */
    public boolean isMoveAll() {
        return moveAll;
    }

    /**
     * @param moveAll the moveAll to set
     */
    public void setMoveAll(boolean moveAll) {
        this.moveAll = moveAll;
        if (moveAll) {
            addAdminCommandToUser("MOVEALL");
        }

    }

    /**
     * @return the moveAll
     */
    public boolean isMoveRoom() {
        return moveRoom;
    }

    /**
     * @param moveRoom
     */
    public void setMoveRoom(boolean moveRoom) {
        this.moveRoom = moveRoom;
        if (moveRoom) {
            addAdminCommandToUser("MOVEROOM");
        }
    }

    /**
     * @return the changePassword
     */
    public boolean isChangePassword() {
        return changePassword;
    }

    /**
     * @param changePassword the changePassword to set
     */
    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
        if (changePassword) {
            addAdminCommandToUser("CHANGEPASSWORD");
        }
    }

    /**
     * @return the motdUpdate
     */
    public boolean isMotdUpdate() {
        return motdUpdate;
    }

    /**
     * @param motdUpdate the motdUpdate to set
     */
    public void setMotdUpdate(boolean motdUpdate) {
        this.motdUpdate = motdUpdate;
        if (motdUpdate) {
            addAdminCommandToUser("MOTDUPDATE");
        }
    }

    /**
     * @return the adminChannel
     */
    public boolean isAdminChannel() {
        return adminChannel;
    }

    /**
     * @param adminChannel the adminChannel to set
     */
    public void setAdminChannel(boolean adminChannel) {
        this.adminChannel = adminChannel;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the adminCommands
     */
    public List<String> getAdminCommands() {
        return adminCommands;
    }

    /**
     * @param adminCommands the adminCommands to set
     */
    public void setAdminCommands(List<String> adminCommands) {
        this.adminCommands = adminCommands;
    }

    public void addAdminCommandToUser(String cmd) {
        getAdminCommands().add(cmd);
    }

    /**
     * @return the createRoom
     */
    public boolean isAddChannel() {
        return addChannel;
    }

    /**
     * @param addChannel the createRoom to set
     */
    public void setAddChannel(boolean addChannel) {
        this.addChannel = addChannel;
        if (addChannel) {
            addCommandToUser("ADDCHANNEL");
        }
    }

    public boolean isDeleteChannel() {
        return deleteChannel;
    }

    /**
     * @param deleteChannel
     */
    public void setDeleteChannel(boolean deleteChannel) {
        this.deleteChannel = deleteChannel;
        if (deleteChannel) {
            addCommandToUser("DELETECHANNEL");
        }
    }

    /**
     * @return the muted
     */
    public boolean isMuted() {
        return muted;
    }

    /**
     * @param muted the muted to set
     */
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    /**
     * @return the blind
     */
    public boolean isBlind() {
        return blind;
    }

    /**
     * @param blind the blind to set
     */
    public void setBlind(boolean blind) {
        this.blind = blind;
    }

    /**
     * @return the canBlind
     */
    public boolean isCanBlind() {
        return canBlind;
    }

    /**
     * @param canBlind the canBlind to set
     */
    public void setCanBlind(boolean canBlind) {
        this.canBlind = canBlind;
        if (canBlind) {
            addAdminCommandToUser("BLIND");
        }
    }

    /**
     * @return the muteAll
     */
    public boolean isMuteAll() {
        return muteAll;
    }

    /**
     * @param muteAll the muteAll to set
     */
    public void setMuteAll(boolean muteAll) {
        this.muteAll = muteAll;
    }
}
