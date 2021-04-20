package BlackJack.test;

import java.util.List;

import BlackJack.game.Card;
import BlackJack.game.CardDeck;

public class test {
	public static void main(String[] args) {
		CardDeck cardDeck = new CardDeck();
		
		List<Card> cards = cardDeck.getCards();
		
		
		for (int i = 0; i < 52; i++) {
			System.out.print(cards.get(i).getShape() + "\t");
			System.out.println(cards.get(i).getCardNum());
		}
		
	}
}
