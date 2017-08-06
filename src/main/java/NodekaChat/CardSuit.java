package NodekaChat;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.lsd.umc.util.AnsiTable;

/**
 *
 * @author Isaac
 */
public enum CardSuit {

    SPADES(AnsiTable.getCode("LIGHT BLACK")),
    CLUBS(AnsiTable.getCode("LIGHT BLACK")),
    HEARTS(AnsiTable.getCode("LIGHT RED")),
    DIAMONDS(AnsiTable.getCode("LIGHT RED"));

    private final String cardSuit;

    private CardSuit(String color) {
        this.cardSuit = color;
    }

    public String getCardSuit() {
        return cardSuit;
    }
}
