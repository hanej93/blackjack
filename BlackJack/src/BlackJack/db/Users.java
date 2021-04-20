package BlackJack.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbconnect.MyConnect;

public class Users {
	private int customerId;
	private String userId;
	private String password;
	private String phoneNumber;
	private String address;
	private BufferedReader br;
	private BufferedWriter bw;

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

	
	
	// 아이디 중복확인 필요!(일부 기능 미구현)
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

	// 사용자정보 조회 (미구현 수정 필요!, 테스트 필요 + 랭크, 자산도 조회해야 함!)
	public void userInformationSelect() throws SQLException, IOException, ClassNotFoundException {
		String selectSql = "select user_id, phone_number, address from customer_info where user_id = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(selectSql);

		// 객체에 아이디 저장
		userId = br.readLine();
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
		}

	}

	// 사용자 정보 수정 (미구현)
	public void userInformationUpdate() throws SQLException, IOException, ClassNotFoundException {
		String updateSql = "update customer_info set(?) where user_id = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(updateSql);

		// 객체에 아이디 저장
		userId = br.readLine();
		pstm.setString(1, userId);

		pstm.executeQuery();

		bw.write(userId + "님의 사용자 정보\n");
		bw.write("아이디 : " + userId);
		bw.write("\n휴대폰번호 : " + phoneNumber);
		bw.write("\n주소 : " + address);
		bw.flush();
	}

	
	// 버그 수정 필요, 테스트 검증 필요(미구현)
	public void userRecord() throws SQLException, IOException, ClassNotFoundException {
		UsersImpl u1 = new UsersImpl();
		Record r = new Record();
		List<Record> recordList = new ArrayList<Record>();
		u.setGameId(userId);
		u.setPassword(password);// 현재 가져올 수 있는 데이터까지
		Users user = u1.selectWithId(u);// select로 데이터 가져옴

		String sql = "select * from record_table where customer_id = ? order by end_game_time limit 20";
		try (Connection conn = MyConnect.getConnect()) {
			PreparedStatement pstm = conn.prepareStatement(sql);
			pstm.setInt(1, user.getCustomerId());
			ResultSet rs = pstm.executeQuery();

			while (rs.next()) {
				r.setRecordId(rs.getInt("record_id"));
				r.setCustomerId(rs.getInt("customer_id"));
				r.setGameresult(rs.getString("gameresult"));
				r.setBet(rs.getInt("bet"));
				r.setTotalHit(rs.getInt("total_hit"));
				r.setTotalStay(rs.getInt("total_stay"));
				r.setEnd_game_time(rs.getTimestamp("end_game_time").toLocalDateTime());
				recordList.add(r);
			}
			for (Record ru : recordList) {
				bw.write(recordList + "정보1");
				bw.flush();
			}
		}
	}
	
	// 자산 테이블을 조회하여 자산을 반환하는 메소드 구현해야 함
	
	// 자산을 업데이트해주는 메소드 구현해야 함
	
	

}
