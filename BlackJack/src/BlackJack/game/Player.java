package BlackJack.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class Player {

	private List<Card> playerHand;
	private BufferedReader br;
	private BufferedWriter bw;
	private CardDeck cardDeck;

	public Player(BufferedReader br, BufferedWriter bw, CardDeck cardDeck, List<Card> playerHand) {
		this.br = br;
		this.bw = bw;
		this.cardDeck = cardDeck;
		this.playerHand = playerHand;
	}

	public List<Card> drawCard() {
		playerHand.add(cardDeck.drawCard());
		return playerHand;
	}

	public int sumHand() {
		int total = 0;
		String strCardNum;
		int intCardNum;
		for(int i = 0;i<playerHand.size();i++) {
			strCardNum = playerHand.get(i).getCardNum();
			if(strCardNum.equals("A")) {
				intCardNum = 11;
			}
			else if(strCardNum.equals("J")) {
				intCardNum = 10;
			}
			else if(strCardNum.equals("Q")) {
				intCardNum = 10;
			}
			else if(strCardNum.equals("K")) {
				intCardNum = 10;
			}else {
				intCardNum = Integer.parseInt(strCardNum);
			}
			total += intCardNum;
			
		}
		for (Card hand : playerHand) {
			if( total > 21 && hand.getCardNum().equals("A")) {
				total -= 10;
			}
		}
		
		return total;
	}

	public void showHand() throws IOException {
		for (Card hand : playerHand) {
			bw.write("┍━━━━━━┑");
		}
		bw.newLine();
		for (Card hand : playerHand) {
			bw.write("│" + hand.getShape() + "     │");
		}
		bw.newLine();
		for (Card hand : playerHand) {
			bw.write("│      │");
		}
		bw.newLine();
		for (Card hand : playerHand) {
			if(hand.getCardNum().equals("10")) {
				bw.write("│  " + hand.getCardNum() + "  │");
				continue;
			}
			bw.write("│   " + hand.getCardNum() + "  │");
		}
		bw.newLine();
		for (Card hand : playerHand) {
			bw.write("│      │");
		}
		bw.newLine();
		for (Card hand : playerHand) {
			bw.write("│     " + hand.getShape() + "│");
		}
		bw.newLine();
		for (Card hand : playerHand) {
			bw.write("┕━━━━━━┙");
		}
		bw.newLine();
		bw.flush();
	}
	
	public boolean isBust() {
		if(this.sumHand() > 21) {
			return true;
		} else {
			return false;
		}
	}

}
