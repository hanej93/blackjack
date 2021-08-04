package BlackJack.db;

import java.time.LocalDateTime;

public class Record {
	private int recordId;
	private int customerId;
	private String gameresult;
	private int bet;
	private int totalHit;
	private int totalStay;
	private LocalDateTime end_game_time;
	public Record() {
		
	}
	public int getRecordId() {
		return recordId;
	}
	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getGameresult() {
		return gameresult;
	}
	public void setGameresult(String gameresult) {
		this.gameresult = gameresult;
	}
	public int getBet() {
		return bet;
	}
	public void setBet(int bet) {
		this.bet = bet;
	}
	public int getTotalHit() {
		return totalHit;
	}
	public void setTotalHit(int totalHit) {
		this.totalHit = totalHit;
	}
	public int getTotalStay() {
		return totalStay;
	}
	public void setTotalStay(int totalStay) {
		this.totalStay = totalStay;
	}
	public LocalDateTime getEnd_game_time() {
		return end_game_time;
	}
	public void setEnd_game_time(LocalDateTime end_game_time) {
		this.end_game_time = end_game_time;
	}
	@Override
	public String toString() {
		return "record [recordId=" + recordId + ", customerId=" + customerId + ", gameresult=" + gameresult + ", bet="
				+ bet + ", totalHit=" + totalHit + ", totalStay=" + totalStay + ", end_game_time=" + end_game_time
				+ "]";
	}
}	

