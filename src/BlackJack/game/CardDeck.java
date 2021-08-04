package BlackJack.game;

import java.util.ArrayList;
import java.util.List;

public class CardDeck {
	private static final String[] SHAPES = { "♠", "♥", "♣", "♦️" };
	private static final int CARD_COUNT = 13;
	private List<Card> cards = new ArrayList<Card>();

	public CardDeck() {
		for (String shape : SHAPES) {
			for (int i = 1; i <= CARD_COUNT; i++) {
				Card card = new Card();
				String cardNum;
				if (i == 1) {
					cardNum = "A";
				} else if (i == 11) {
					cardNum = "J";
				} else if (i == 12) {
					cardNum = "Q";
				} else if (i == 13) {
					cardNum = "K";
				} else {
					cardNum = Integer.toString(i);
				}
				card.setCardNum(cardNum);
				card.setShape(shape);
				cards.add(card);
			}
		}
	}

	public List<Card> getCards() {
		return cards;
	}

	public Card drawCard() {
		int randomCardIndex;
		randomCardIndex = (int) (Math.random() * cards.size());
		Card resultCard = cards.get(randomCardIndex);
		cards.remove(resultCard);

		return resultCard;
	}

}
