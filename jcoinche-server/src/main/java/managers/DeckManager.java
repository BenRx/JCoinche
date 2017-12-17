package managers;

import models.Card;
import models.Player;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Manage decks in game
 */
public class DeckManager {
    public ArrayList<Card> mDeck;

    /**
     * Init the DeckManager by creating a deck and shuffle it
     */
    public DeckManager() {
        mDeck = new ArrayList<>();
        createDeck();
        shuffleDeck();
    }

    /**
     * Remove the card passed in parameter of the specified player's hand
     * @param player
     * @param cardToRm
     */
    public void removeCardFromPlayerHand(Player player, Card cardToRm) {
        for (Card card : player.mHand) {
            if (card.mCardValue == cardToRm.mCardValue && card.mColorType == cardToRm.mColorType) {
                player.mHand.remove(card);
                break;
            }
        }
    }

    /**
     * Create a hand for a player by removing cards of the actual deck
     * @return
     */
    public ArrayList<Card> createHand() {
        shuffleDeck();
        ArrayList<Card> newHand = new ArrayList<>();
        for (int i = 0 ; i != 8 ; ++i) {
            newHand.add(mDeck.get(0));
            mDeck.remove(0);
        }
        return newHand;
    }

    public ArrayList<Card> getDeck() {
        return mDeck;
    }

    private void createDeck() {
        for (int i = 0 ; i != 4 ; ++i) {
            Card.ColorType colorType = Card.ColorType.forInt(i);
            for (int j = 0 ; j != 8 ; ++j) {
                Card.ValueType valueType = Card.ValueType.forInt(j);
                mDeck.add(new Card(colorType, valueType));
            }
        }
    }

    private void shuffleDeck() {
        Collections.shuffle(mDeck);
    }
}
