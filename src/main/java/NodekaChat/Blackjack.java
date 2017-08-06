package NodekaChat;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.lsd.umc.util.AnsiTable;
import java.io.IOException;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Leviticus
 */
public class Blackjack extends NodekaChat implements Runnable {

    private final Object instance = new Object();
    protected final Queue<User> inRoom = new ConcurrentLinkedQueue<>();
    protected final Queue<User> inGame = new ConcurrentLinkedQueue<>();
    private TimerTask initialWaitToJoinGameTask;
    private int counter = 0;
    private Timer timer;
    private final Message message = new Message();
    protected AtomicBoolean startGame = new AtomicBoolean();
    protected AtomicBoolean waitToStartRound = new AtomicBoolean();
    protected AtomicBoolean waitForInput = new AtomicBoolean();
    protected int duration = 10;

    private final Queue<Player> inGamePlayers = new ConcurrentLinkedQueue<>();

    protected void joinRoom(User user) {
        boolean alreadyInRoom = false;
        for (User u : inRoom) {
            if (u.getLoginName() == null ? user.getLoginName() == null : u.getLoginName().equals(user.getLoginName())) {
                alreadyInRoom = true;
                break;
            } else {
                for (User v : inGame) {
                    if (u.getLoginName() == null ? v.getLoginName() == null : u.getLoginName().equals(v.getLoginName())) {
                        alreadyInRoom = true;
                        break;
                    }
                }
            }
        }
        if (!alreadyInRoom) {
            message.globalMessage("GAMBLE", user.getLoginName() + " has joined " + AnsiTable.getCode("yellow") + "Blackjack!");
            inRoom.add(user);
        }

        for (User u : inRoom) {
            if (inGame.contains(u)) {
                message.personalMessage("GAMBLE", u.getLoginName() + " (" + u.getDisplayName() + ") is in a " + AnsiTable.getCode("yellow") + "Blackjack " + AnsiTable.getCode("white") + "game!", user);
            } else {
                message.personalMessage("GAMBLE", u.getLoginName() + " (" + u.getDisplayName() + ") is in the " + AnsiTable.getCode("yellow") + "Blackjack " + AnsiTable.getCode("white") + "lobby.", user);
            }
        }

        /*
         if (nextRound) {
         inGame.add(p);
         }
         */
    }

    void quit(User p) {
        inGame.remove(p);
        inRoom.remove(p);
    }

    protected Blackjack() {
    }

    public void doRoundTimer(int dur) {
        counter = 0;
        initialWaitToJoinGameTask = new TimerTask() {
            @Override
            public synchronized void run() {
                counter++;
                if (counter >= dur) {
                    counter = 0;
                    timer.cancel();
                    waitToStartRound.set(false);
                    startGame.set(true);
                    notifyAll();
                }
            }
        };
        timer = new Timer("WaitTimer");
    }

    // Runtime initialization
    // By defualt ThreadSafe
    public Object getInstance() {
        return instance;
    }

    public void doPlayerTurn(int dur, Player player) {
        counter = 0;
        initialWaitToJoinGameTask = new TimerTask() {
            @Override
            public synchronized void run() {
                counter++;
                try {
                    message.gameMessage(inGame, player.user.getInput().readLine());
                } catch (IOException ex) {
                }
                if (counter >= dur) {
                    counter = 0;
                    timer.cancel();
                    waitForInput.set(false);
                    message.gameMessage(inGame, "timed out.");
                    notifyAll();
                }
            }
        };
        timer = new Timer("playerRound");
    }

    /**
     *
     */
    @Override
    public synchronized void run() {
        doRoundTimer(duration);
        waitToStartRound.set(true);
        Deck deck = new Deck();
        Player dealer = new Player();
        deck.shuffleDeck();

        message.globalMessage("GAMBLE", "A new game of " + AnsiTable.getCode("yellow") + "Blackjack " + AnsiTable.getCode("white") + "is starting!! #ochat BLACKJACK");
        timer.scheduleAtFixedRate(initialWaitToJoinGameTask, 30, 1000);

        while (!waitToStartRound.get()) {
            try {
                wait();
            } catch (InterruptedException e) {
            }

        }
        for (User u : inRoom) {
            inRoom.remove(u);
            inGame.add(u);
            inGamePlayers.add(new Player(u));
        }

        do {
            message.gameMessage(inRoom, "This round of " + AnsiTable.getCode("yellow") + "Blackjack " + AnsiTable.getCode("white") + "has begun.");

            for (Player p : inGamePlayers) {
                p.addCardToHand(deck.nextCard());
                message.gameMessage(inGame, p.user.getLoginName() + " recieved " + p.lastCardGiven().getCardValue() + " of " + p.lastCardGiven().getSuit());

            }
            dealer.addCardToHand(deck.nextCard());
            message.gameMessage(inGame, "Dealer recieved " + dealer.lastCardGiven().getCardValue() + " of " + dealer.lastCardGiven().getSuit());

            for (Player p : inGamePlayers) {
                p.addCardToHand(deck.nextCard());
                message.gameMessage(inGame, p.user.getLoginName() + " recieved " + p.lastCardGiven().getCardValue() + " of " + p.lastCardGiven().getSuit());
            }
            dealer.addCardToHand(deck.nextCard());
            message.gameMessage(inGame, "Dealer has recieved a card.");

            for (Player p : inGamePlayers) {
                doPlayerTurn(20, p);
                timer.scheduleAtFixedRate(initialWaitToJoinGameTask, 5, 1000);
                while (!waitForInput.get()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            //21 check
            if (dealer.getHandValue() == 21) {
                message.gameMessage(inGame, "Luck is not on your side. Dealer has 21!");
            }

            startGame.set(false);
        } while (startGame.get());

        for (User u : inGame) {
            inGame.remove();
            inRoom.add(u);
        }

        for (Player p : inGamePlayers) {
            inGamePlayers.remove(p);
        }

        message.gameMessage(inRoom, "Another round of " + AnsiTable.getCode("yellow") + "Blackjack " + AnsiTable.getCode("white") + "will begin shortly!");
    }
}
