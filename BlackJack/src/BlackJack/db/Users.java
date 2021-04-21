package BlackJack.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dbconnect.MyConnect;

public class Users {
	private int customerId;
	private String userId;
	private String password;
	private String phoneNumber;
	private String address;
	private long money;
	private String rankf;

	private BufferedReader br;
	private BufferedWriter bw;

	public Users() {

	}

	public Users(BufferedReader br, BufferedWriter bw) {
		this.br = br;
		this.bw = bw;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Users [customerId=" + customerId + ", userId=" + userId + ", password=" + password + ", phoneNumber="
				+ phoneNumber + ", address=" + address + "]";
	}

	// Customer_info 테이블의 모든정보를 조회하는 메소드
	public void selectAllCustomerInfo(String inputId) throws ClassNotFoundException, SQLException {
		String selectSql = "select customer_id, user_id, password, phone_number, address, money, rankf from customer_info where user_id = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(selectSql);

		pstm.setString(1, inputId);

		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			customerId = rs.getInt("customer_id");
			userId = rs.getString("user_id");
			password = rs.getString("password");
			phoneNumber = rs.getString("phone_number");
			address = rs.getString("address");
			money = rs.getLong("money");
			rankf = rs.getString("rankf");
		}

	}

	// 아이디 중복확인 필요!(일부 기능 미구현) 배팅이랑 기본랭크도 설정!
	public void userSignup() throws SQLException, IOException, ClassNotFoundException {
		String insertSql = "insert into customer_info(user_id, password, phone_number, address) values(?,?,?,?)";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(insertSql);

		bw.write("회원가입을 위해 정보를 입력해 주세요\n");
		bw.newLine();
		bw.newLine();

		bw.write("아이디를 입력하세요 : \n");
		bw.flush();
		// 객체에 아이디 저장
		userId = br.readLine();
		pstm.setString(1, userId);

		bw.write("비밀번호를 입력하세요 : \n");
		bw.flush();
		// 객체에 비밀번호 저장
		password = br.readLine();
		pstm.setString(2, password);

		bw.write("휴대폰번호를 입력하세요 : \n");
		bw.flush();
		// 객체에 휴대폰번호 저장
		phoneNumber = br.readLine();
		pstm.setString(3, phoneNumber);

		bw.write("주소를 입력하세요 : \n");
		bw.flush();
		// 객체에 비밀번호 저장
		address = br.readLine();
		pstm.setString(4, address);

		bw.write("회원가입 성공!!!\n");
		bw.newLine();
		bw.newLine();
		bw.flush();
		pstm.executeUpdate();
	}

	// 사용자정보 조회(구현 끝?)
	public void userInformationSelect() throws SQLException, IOException, ClassNotFoundException {
		String selectSql = "select user_id, phone_number, address, money, rankf from customer_info where user_id = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(selectSql);

		pstm.setString(1, userId);

		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			userId = rs.getString("user_id");
			bw.write(userId + "님의 사용자 정보\n");
			bw.write("아이디 : " + userId);
			bw.newLine();

			phoneNumber = rs.getString("phone_number");
			bw.write("휴대폰번호 : " + phoneNumber);
			bw.newLine();

			address = rs.getString("address");
			bw.write("주소 : " + address);
			bw.newLine();
			bw.flush();

			money = rs.getLong("money");
			bw.write("보유 자산: " + money);
			bw.newLine();
			bw.flush();

			rankf = rs.getString("rankf");
			bw.write("티어 : " + rankf);
			bw.newLine();
			bw.flush();

		}

	}

	// 사용자 정보 수정 (미구현) - 로그인한 본인만 수정가능!
	public void userInformationUpdate(String inputId) throws SQLException, IOException, ClassNotFoundException {
		String updateSql = "update customer_info set password = ?, phone_number = ?, address = ? where user_id = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(updateSql);

		this.selectAllCustomerInfo(inputId);

		// 만들어야 할 것! 출력하는 것을 클라이언트에게 보내주고 클라이언트가 입력받은 것을 서버가 읽어서 필드에 저장!
		String enterCheck;

		bw.write("비밀번호를 수정하시겠습니까? 엔터시 수정 안함\n");
		bw.flush();
		enterCheck = br.readLine();
		if (!enterCheck.equals("")) {
			bw.write("수정할 비밀번호를 입력하세요 : \n");
			bw.flush();
			password = br.readLine();
		}

		bw.write("전화번호를 수정하시겠습니까? 엔터시 수정 안함\n");
		bw.flush();
		enterCheck = br.readLine();
		if (!enterCheck.equals("")) {
			bw.write("수정할 전화번호를 입력하세요 : \n");
			bw.flush();
			phoneNumber = br.readLine();
		}

		bw.write("주소를 수정하시겠습니까? 엔터시 수정 안함\n");
		bw.flush();
		enterCheck = br.readLine();
		if (!enterCheck.equals("")) {
			bw.write("수정할 주소를 입력하세요 : \n");
			bw.flush();
			address = br.readLine();
		}

		pstm.setString(1, password);
		pstm.setString(2, phoneNumber);
		pstm.setString(3, address);
		pstm.setString(4, inputId);

		pstm.executeUpdate();

	}

	// 플레이어 보유한 자산 반환하는 메소드
	public long playerMoney(String playerId) throws ClassNotFoundException, SQLException, IOException {
		String selectSql = "select money from customer_info where user_id = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(selectSql);

		// 객체에 아이디 저장
		pstm.setString(1, playerId);

		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			return rs.getLong("money");
		}

		return -1;
	}

	// 베팅
	public void updateMoney(long money) throws SQLException, IOException, ClassNotFoundException {
		String updateSql = "update customer_info set money = money + ? where user_id = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(updateSql);

		// 객체에 아이디 저장
		pstm.setLong(1, money);
		pstm.setString(2, userId);

		pstm.executeUpdate();

	}

	// 전적 테이블 조회해서 Record 반환 메소드
	public void RecordTableLookUp(String inputUserId) throws ClassNotFoundException, SQLException, IOException {
		String selectSql = "select * from record_table where user_id = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(selectSql);

		// 객체에 아이디 저장
		pstm.setString(1, inputUserId);

		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			int recordId = rs.getInt("record_id");
			int customerId = rs.getInt("customer_id");
			String gameResult = rs.getString("game_result");
			long bet = rs.getLong("bet");
			int totalHit = rs.getInt("total_hit");
			int totalStay = rs.getInt("total_stay");
			LocalDateTime endGameTime = rs.getTimestamp("end_game_time").toLocalDateTime();
			LocalDateTime accessTime = rs.getTimestamp("access_game_time").toLocalDateTime();
			LocalDateTime exitTime = rs.getTimestamp("exit_game_time").toLocalDateTime();

			String result = "[전적 테이블 조회]\n" + "[" + recordId + "]" + "고객 아이디 : " + customerId + ", " + "게임 결과 : "
					+ gameResult + ", " + "게임 내 배팅 금액 : " + bet + ", " + "게임 내 hit한 수 : " + totalHit + ", "
					+ "게임 내 stay한 수 : " + totalStay + ", " + "게임 끝난 시간 : " + endGameTime + ", " + "접속 시간 : "
					+ accessTime + ", " + "접속 종료 시간 : " + exitTime;

			bw.write(result + "\n");
			bw.flush();
		}
	}

}
