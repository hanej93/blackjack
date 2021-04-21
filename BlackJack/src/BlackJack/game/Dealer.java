package BlackJack.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class Dealer {
	private List<Card> dealerHand;
	private BufferedReader br;
	private BufferedWriter bw;
	private CardDeck cardDeck;

	public Dealer(BufferedReader br, BufferedWriter bw, CardDeck cardDeck, List<Card> dealerHand) {
		this.br = br;
		this.bw = bw;
		this.cardDeck = cardDeck;
		this.dealerHand = dealerHand;
	}

	public List<Card> drawCard() {
		dealerHand.add(cardDeck.drawCard());
		return dealerHand;
	}

	public int sumHand() {
		int total = 0;
		String strCardNum;
		int intCardNum;
		for (int i = 0; i < dealerHand.size(); i++) {
			strCardNum = dealerHand.get(i).getCardNum();
			if (strCardNum.equals("A")) {
				intCardNum = 11;
			}
			else if (strCardNum.equals("J")) {
				intCardNum = 10;
			} else if (strCardNum.equals("Q")) {
				intCardNum = 10;
			} else if (strCardNum.equals("K")) {
				intCardNum = 10;
			} else {
				intCardNum = Integer.parseInt(strCardNum);
			}
			total += intCardNum;

		}
		for (Card hand : dealerHand) {
			if (total > 21 && hand.getCardNum().equals("A")) {
				total -= 10;
			}
		}

		return total;
	}

	public void showHand() throws IOException {
		for (Card hand : dealerHand) {
			bw.write("┍━━━━━━┑");
		}
		bw.newLine();
		for (Card hand : dealerHand) {
			bw.write("│" + hand.getShape() + "     │");
		}
		bw.newLine();
		for (Card hand : dealerHand) {
			bw.write("│      │");
		}
		bw.newLine();
		for (Card hand : dealerHand) {
			if(hand.getCardNum().equals("10")) {
				bw.write("│  " + hand.getCardNum() + "  │");
				continue;
			}
			bw.write("│   " + hand.getCardNum() + "  │");
		}
		bw.newLine();
		for (Card hand : dealerHand) {
			bw.write("│      │");
		}
		bw.newLine();
		for (Card hand : dealerHand) {
			bw.write("│     " + hand.getShape() + "│");
		}
		bw.newLine();
		for (Card hand : dealerHand) {
			bw.write("┕━━━━━━┙");
		}
		bw.newLine();
		bw.flush();
	}

	public void showHiddenHand() throws IOException {
		int firstCard = 0;
		for (Card hand : dealerHand) {
			bw.write("┍━━━━━━┑");
		}
		bw.newLine();
		for (Card hand : dealerHand) {
			if (firstCard == 0) {
				bw.write("│      │");
				firstCard++;
				continue;
			}
			bw.write("│" + hand.getShape() + "     │");
		}
		bw.newLine();
		for (Card hand : dealerHand) {
			bw.write("│      │");
		}
		bw.newLine();
		firstCard = 0;
		for (Card hand : dealerHand) {
			if (firstCard == 0) {
				bw.write("│      │");
				firstCard++;
				continue;
			}
			if(hand.getCardNum().equals("10")) {
				bw.write("│  " + hand.getCardNum() + "  │");
				continue;
			}
			bw.write("│   " + hand.getCardNum() + "  │");
		}
		bw.newLine();
		for (Card hand : dealerHand) {
			bw.write("│      │");
		}
		bw.newLine();
		firstCard = 0;
		for (Card hand : dealerHand) {
			if (firstCard == 0) {
				bw.write("│      │");
				firstCard++;
				continue;
			}
			bw.write("│     " + hand.getShape() + "│");
		}
		bw.newLine();
		for (Card hand : dealerHand) {
			bw.write("┕━━━━━━┙");
		}
		bw.newLine();
		bw.flush();
	}

	public boolean isBust() {
		if (this.sumHand() > 21) {
			return true;
		} else {
			return false;
		}
	}
	
	public void makeDealerHand() {
		while(true) {
			if(this.sumHand() < 17) {
				this.drawCard();
			} else {
				break;
			}
		}
	}
	
}
