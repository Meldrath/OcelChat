package NodekaChat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> deck = new ArrayList<>();

    public Deck() {
        for (int i = 0; i < 13; i++) {
            CardValue value = CardValue.values()[i];

            for (int j = 0; j < 4; j++) {
                Card card = new Card(value, CardSuit.values()[j]);
                this.deck.add(card);
            }
        }
        
        //shuffleDeck();
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public List<Card> getDeck() {
        return deck;
    }
    
    public Card nextCard() {
        return deck.remove(0);
    }
}
