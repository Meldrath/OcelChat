package NodekaChat;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Isaac
 */
public class Player extends Blackjack {

    protected User user;
    private final Queue<Card> hand = new LinkedList<>();
    private Card lastCardReceived;
    private int handValue;

    public Player(User user) {
        this.user = user;
    }

    public Player() {

    }

    public Queue<Card> getHand() {
        return hand;
    }

    public void addCardToHand(Card c) {
        hand.add(c);
        lastCardReceived = c;

        if (c.getCardFaceValue()>= 10 && c.getCardFaceValue() <= 13) {
            addToHandValue(10);
        } else if (c.getCardFaceValue() == 14) {
            addToHandValue(11);
        }
    }

    public void splitHand() {

    }

    public void removeCardFromHand(Card c) {
        if (hand.contains(c)) {
            hand.remove(c);
        }
    }

    public Card lastCardGiven() {
        if (lastCardReceived != null) {
            return lastCardReceived;
        }
        return null;
    }

    /**
     * @return the handValue
     */
    public int getHandValue() {
        return handValue;
    }

    /**
     * @param handValue the handValue to set
     */
    public void setHandValue(int handValue) {
        this.handValue = handValue;
    }

    public void addToHandValue(int valueOfCard) {
        this.handValue += valueOfCard;
    }
}
