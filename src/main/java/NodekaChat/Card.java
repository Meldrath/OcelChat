package NodekaChat;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Isaac
 */
public class Card {

    private CardSuit suit;
    private CardValue cardValue;

    public Card(CardValue cardValue, CardSuit suit) {
        this.cardValue = cardValue;
        this.suit = suit;
    }

    public String getSuit() {
        return suit.getCardSuit() + this.getSuitName().name();
    }

    public CardSuit getSuitName() {
        return suit;
    }

    public void setSuit(CardSuit suit) {
        this.suit = suit;
    }

    public CardValue getCardValue() {
        return cardValue;
    }

    public int getCardFaceValue() {
        return cardValue.getCardValue();
    }

    public void setCardValue(CardValue cardValue) {
        this.cardValue = cardValue;
    }
}
