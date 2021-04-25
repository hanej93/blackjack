package BlackJack.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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

	// [명주, 의진]
	// customer테이블의 모든 정보 조회하여 필드에 담아줌
	public void userInformationUpdateSelect(String userId) throws ClassNotFoundException, SQLException {
		String sql = "select * from customer_info where user_id = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, userId);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			this.customerId = rs.getInt("customer_id");
			this.userId = rs.getString("user_id");
			this.password = rs.getString("password");
			this.phoneNumber = rs.getString("phone_number");
			this.address = rs.getString("address");
			this.money = rs.getLong("money");
			this.rankf = rs.getString("rankf");
		}
	}

	// [명주]
	// 아이디가 있는지 확인하는 메서드 1(select)
	// 반환: boolean , 매개변수: 아이디
	public boolean userIdOverlap(String userId) throws ClassNotFoundException, SQLException, IOException {
		String sql = "select user_id from customer_info where user_id= ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, userId);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {// 중복 아이디가 있을 시 반환값 false!
			return false;
		} else {// 중복 아이디가 없을 시 반환값 true!
			return true;
		}
	}

	// [명주]
	// 아이디, 비밀번호 둘 다 있는지 확인(select)
	// 반환: boolean , 매개변수 : 아이디
	public boolean userCheck(String userId, String password) throws ClassNotFoundException, SQLException, IOException {
		String sql = "select user_id, password from customer_info where user_id = ? and password = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, userId);
		pstm.setString(2, password);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {// 가져왔을 시!
			return true;
		} else {
			return false;
		}
	}

	// [승연, 명주, 의진]
	// 회원가입(insert)
	// : 아이디 중복확인(userIdOverlap 메서드 호출)
	// : 아아디, 비밀번호, 휴대폰번호, 주소 입력(티어, 자산은 디폴트)
	public void userSignup(Users user) throws SQLException, IOException, ClassNotFoundException {
		String insertSql = "insert into customer_info(user_id, password, phone_number, address) values(?,?,?,?)";
		Connection conn = MyConnect.getConnect();
		while (true) {
			bw.write("회원가입을 위해 정보를 입력해 주세요\n");
			bw.newLine();
			bw.newLine();

			bw.write("아이디를 입력하세요 : \n");
			bw.flush();
			// 객체에 아이디 저장
			userId = br.readLine();

			if (user.userIdOverlap(userId)) {// 중복 아이디가 없을 시!
				PreparedStatement pstm = conn.prepareStatement(insertSql);
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
				break;
			} else {// 중복 아이디가 있을 시!
				bw.write("이 아이디는 이미 존재하는 아이디입니다. 다시 입력해주세요. \n");
				bw.flush();
				continue;
			}
		}
	}

	// [승연, 의진, 명주]
	// 사용자정보 조회(select)
	// : 사용자 정보 조회
	// : 아이디, 휴대폰번호, 주소, 보유자산, 티어를 조회
	public void userInformationSelect(String inputId) throws SQLException, IOException, ClassNotFoundException {
		String selectSql = "select user_id, phone_number, address, money, rankf from customer_info where user_id = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(selectSql);

		pstm.setString(1, inputId);

		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			userId = rs.getString("user_id");
			bw.write("[" + userId + "님의 사용자 정보]\n");
			bw.newLine();
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

	// [승연, 명주, 의진]
	// 사용자 정보 수정(update)
	// : 셀렉트로 모든 정보를 가져오고
	// 수정하고 싶은 정보를 선택하여 수정 가능
	public void userInformationUpdate(String userId) throws SQLException, IOException, ClassNotFoundException {

		// customer 테이블에서 특정 유저를 조회해서 필드에 담아줌
		userInformationUpdateSelect(userId);
		String updateSql = "update customer_info set password = ?, phone_number = ?, address = ? where user_id = ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(updateSql);
		this.userId = userId;
		while (true) {
			bw.write(userId + "님, 어떤 정보를 바꾸시겠습니까? 1=비밀번호, 2=핸드폰번호, 3=주소");
			bw.newLine();
			bw.flush();
			String num = br.readLine();
			if (num.equals("1")) {
				bw.write("변경하실 비밀번호를 입력해주세요.");
				bw.newLine();
				bw.flush();
				this.password = br.readLine();
			} else if (num.equals("2")) {
				bw.write("변경하실 핸드폰 번호를 입력해주세요.");
				bw.newLine();
				bw.flush();
				this.phoneNumber = br.readLine();
			} else if (num.equals("3")) {
				bw.write("변경하실 주소를 입력해주세요.");
				bw.newLine();
				bw.flush();
				this.address = br.readLine();
			} else {
				bw.write("잘못 입력하셨습니다. 다시 입력해주세요.");
				bw.newLine();
				bw.flush();
				continue;
			}
			break;
		}
		pstm.setString(1, this.password);
		pstm.setString(2, this.phoneNumber);
		pstm.setString(3, this.address);
		pstm.setString(4, userId);
		pstm.executeUpdate();

		bw.newLine();
		bw.newLine();
		bw.write("[변경된 정보]\n");
		bw.write("아이디 : " + this.userId);
		bw.write("\n비밀번호 : " + password);
		bw.write("\n핸드폰번호 : " + phoneNumber);
		bw.write("\n주소 : " + address);
		bw.flush();
	}

	// [승연, 의진]
	// 배팅금
	/*
	 * public long playerMoney() throws ClassNotFoundException, SQLException,
	 * IOException { String selectSql =
	 * "select money from customer_info where user_id = ?"; Connection conn =
	 * MyConnect.getConnect(); PreparedStatement pstm =
	 * conn.prepareStatement(selectSql);
	 * 
	 * // 객체에 아이디 저장 pstm.setString(1, userId);
	 * 
	 * ResultSet rs = pstm.executeQuery(); if (rs.next()) { return
	 * rs.getLong("money"); }
	 * 
	 * return -1; }
	 */

	// 베팅
	/*
	 * public void updateMoney(long money) throws SQLException, IOException,
	 * ClassNotFoundException { String updateSql =
	 * "update customer_info set money += ? where user_id = ?"; Connection conn =
	 * MyConnect.getConnect(); PreparedStatement pstm =
	 * conn.prepareStatement(updateSql);
	 * 
	 * // 객체에 아이디 저장 pstm.setLong(1, money); pstm.setString(2, userId);
	 * 
	 * pstm.executeQuery();
	 * 
	 * }
	 */

	// [명주]
    // 전적 테이블 조회(select)
    // : boolean 반환 메소드
    public boolean RecordTableLookUp(String inputUserId) throws ClassNotFoundException, SQLException, IOException {
        String selectSql = "select * from record_table where user_id = ? order by end_game_time desc limit 10";
        Connection conn = MyConnect.getConnect();
        PreparedStatement pstm = conn.prepareStatement(selectSql);

        pstm.setString(1, inputUserId);

        ResultSet rs = pstm.executeQuery();
        int a = 1;
        if (!(rs == null)) {// 가져왔을 시!
            while (rs.next()) {
                String userId = rs.getString("user_id");
                String gameResult = rs.getString("game_result");
                long bet = rs.getLong("bet");
                int totalHit = rs.getInt("total_hit");
                int totalStay = rs.getInt("total_stay");
                LocalDateTime endGameTime = rs.getTimestamp("end_game_time").toLocalDateTime();

                String result = "" + "┌───────────────────────────────────┐" + "\n   [전적 테이블 조회" + a
                        + "]              \n" + " 고객 아이디 : " + userId + "       \n" + " 게임 결과 : " + gameResult
                        + "       \n" + " 게임 내 배팅 금액 : " + bet + "       \n" + " 게임 내 hit한 수 : " + totalHit
                        + "       \n" + " 게임 내 stay한 수 : " + totalStay + "       \n" + " 게임 끝난 시간 : " + endGameTime
                        + "       \n" + "└───────────────────────────────────┚";

                bw.write(result + "\n");
                bw.flush();
                a++;
            }
            return true;
        } else {
            return false;
        }
    }

	// [명주]
	// 히스토리(insert)
	// : 플레이어 접속기록 저장
	public void historySet(String userId, LocalDateTime accessTime, LocalDateTime exitTime)
			throws ClassNotFoundException, SQLException, IOException {
		String sql = "insert into history_table(user_id, access_date, exit_date)values(?, ?, ?)";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(sql);

		pstm.setString(1, userId);
		pstm.setTimestamp(2, Timestamp.valueOf(accessTime));
		pstm.setTimestamp(3, Timestamp.valueOf(exitTime));

		pstm.executeUpdate();
	}

	// [명주]
	// 전적기록(insert)
	// : 한 판 전적 저장하는 기능
	public void recordSet(String userId, String gameResult, long oneGameBet, int totalHit, int totalStay,
			LocalDateTime endGameTime) throws ClassNotFoundException, SQLException, IOException {
		String sql = "insert into record_table(user_id, game_result, bet, total_hit, total_stay, end_game_time)values(?, ?, ?, ?, ?, ?)";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(sql);

		pstm.setString(1, userId);
		pstm.setString(2, gameResult);
		pstm.setLong(3, oneGameBet);
		pstm.setInt(4, totalHit);
		pstm.setInt(5, totalStay);
		pstm.setTimestamp(6, Timestamp.valueOf(endGameTime));

		pstm.executeUpdate();
	}

	// [승연, 명주, 의진]
	// 자산 조회기능(select)
	// 반환 : "long"
	public long selectUserAssets(String userId) throws ClassNotFoundException, SQLException, IOException {
		String sql = "select money from customer_info where user_id= ?";
		long money = 0;
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, userId);
		ResultSet rs = pstm.executeQuery();
		if (rs.next()) {
			money = rs.getLong("money");
		}
		return money;
	}

	// [승연, 명주, 의진]
	// 자산 업데이트
	// 자산 +
	public void updateUserAssetsP(long bettingMoney, String userId)
			throws ClassNotFoundException, SQLException, IOException {
		String sql = "update customer_info set money = money + ? where user_id= ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setLong(1, bettingMoney);
		pstm.setString(2, userId);
		pstm.executeUpdate();
	}

	// [승연, 명주, 의진]
	// 자산 업데이트
	// 자산 -
	public void updateUserAssetsM(long bettingMoney, String userId)
			throws ClassNotFoundException, SQLException, IOException {
		String sql = "update customer_info set money = money - ? where user_id= ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setLong(1, bettingMoney);
		pstm.setString(2, userId);
		pstm.executeUpdate();
	}

	// [명주]
	// 자산을 기반으로 등급을 업데이트하는 메소드
	public void UserAssetsForRank(String userId) throws ClassNotFoundException, SQLException, IOException {
		long money = selectUserAssets(userId);
		String rank = "아이언";
		String sql = "update customer_info set rankf = ? where user_id= ?";
		Connection conn = MyConnect.getConnect();
		PreparedStatement pstm = conn.prepareStatement(sql);

		if (money < 50000000) {// 5천만 미만 아이언

		} else if (money < 75000000) {// 7천5백 미만 브론즈
			rank = "브론즈";
		} else if (money < 100000000) {// 1억 미만 실버
			rank = "실버";
		} else if (money < 150000000) {// 1억 5천 미만 골드
			rank = "골드";
		} else if (money < 300000000) {// 3억 미만 플래티넘
			rank = "플래티넘";
		} else if (money < 500000000) {// 5억 미만 다이아
			rank = "다이아";
		} else if (money < 1000000000) {// 10억 미만 마스터
			rank = "마스터";
		} else if (money < 3000000000L) {// 30억 미만 그랜드 마스터
			rank = "그랜드 마스터";
		} else {// 30억 이상 챌린저
			rank = "챌린저";
		}
		pstm.setString(1, rank);
		pstm.setString(2, userId);
		pstm.executeUpdate();
	}

	// [명주, 승연, 의진]
	// 랭킹 조회 메소드(모두가 볼 수 있음)
	public void joinRank(String userId) throws ClassNotFoundException, SQLException, IOException {
		String myRankSql = "select user_id, money, rankf from customer_info where user_id = ?";
		String topTenSelectSql = "select user_id, money, rankf from customer_info order by money desc limit 10";
		String sql = "select user_id, money, rankf from customer_info order by money desc";
		String ms = "[랭킹 ";

		int a = 1;

		Connection conn = MyConnect.getConnect();

		PreparedStatement myPstm = conn.prepareStatement(myRankSql);
		PreparedStatement topTenPstm = conn.prepareStatement(topTenSelectSql);
		PreparedStatement pstm = conn.prepareStatement(sql);

		myPstm.setString(1, userId);

		ResultSet myRs = myPstm.executeQuery();
		ResultSet topTenRs = topTenPstm.executeQuery();
		ResultSet rs = pstm.executeQuery();

		while (true) {
			bw.write("◈랭크 조회◈" + "\n┌──────────────────────────────────────┐" + "\n  1 = 나의 랭킹, 2 = 1~10위 랭킹, 3 = 전체 랭킹"
					+ "\n└──────────────────────────────────────┘" + "\n랭크 번호를 입력해주세요 : ");
			bw.newLine();
			bw.flush();
			String num = br.readLine();
			if (num.equals("1")) {
				// 나의 랭킹 조회
				while (rs.next()) {
					if (rs.getString("user_id").equals(userId)) {
						bw.write("◈나의 랭킹◈  ");
						bw.write(ms + a + "위 ]\n");
						this.userId = rs.getString("user_id");
						bw.write("아이디 : " + userId);
						bw.newLine();
						bw.flush();

						this.money = rs.getLong("money");
						bw.write("보유 자산: " + money);
						bw.newLine();
						bw.flush();

						this.rankf = rs.getString("rankf");
						bw.write("티어 : " + rankf);
						bw.newLine();
						bw.newLine();
						bw.flush();
						break;
					}
					a++;
				}
			} else if (num.equals("2")) {
				// 1~10위 랭킹 조회
				while (topTenRs.next()) {
					bw.write("◈1~10위 랭킹◈  ");
					bw.write(ms + a + "위 ]\n");
					this.userId = topTenRs.getString("user_id");
					bw.write("아이디 : " + this.userId);
					bw.newLine();
					bw.flush();

					this.money = topTenRs.getLong("money");
					bw.write("보유 자산: " + money);
					bw.newLine();
					bw.flush();

					this.rankf = topTenRs.getString("rankf");
					bw.write("티어 : " + rankf);
					bw.newLine();
					bw.newLine();
					bw.flush();
					a++;
				}
				// 전체 랭킹 조회를 위한 a 초기화
				a = 1;

			} else if (num.equals("3")) {
				// 전체 랭킹 조회
				while (rs.next()) {
					bw.write("◈전체 랭킹◈  ");
					bw.write(ms + a + "위 ]\n");
					this.userId = rs.getString("user_id");
					bw.write("아이디 : " + this.userId);
					bw.newLine();
					bw.flush();

					this.money = rs.getLong("money");
					bw.write("보유 자산: " + money);
					bw.newLine();
					bw.flush();

					this.rankf = rs.getString("rankf");
					bw.write("티어 : " + rankf);
					bw.newLine();
					bw.newLine();
					bw.flush();
					a++;
				}
			} else {
				bw.write("잘못 입력하셨습니다. 다시 입력해주세요.");
				bw.newLine();
				bw.flush();
				continue;
			}
			break;
		}

	}

	// [명주]//수정 2
		// 전적 집계(insert)
		// 전적 집계(ID)를 이용해 검색할 시 ID세팅용
		// 집계 내용 : 전적 ID로 찾기 세팅
		// 반환 : "int"
		
		public String recordIdSet() throws IOException {
			String searchId = null;
			bw.write("검색 할 ID를 입력해주세요.\n");
			bw.flush();
			searchId = br.readLine();
			
			return searchId;
		}
		
		
		// [명주]//수정 2-1
		// 전적 집계(insert)
		// 전체 판 전적 저장한 걸 집계하여 보여주는 기능
		// 집계 내용 : 승리 횟수
		// 반환 : "int"
		public int winCount(String searchId) throws SQLException, ClassNotFoundException, IOException {
			String sql = null;
			ResultSet rs = null;
			Connection conn = MyConnect.getConnect();
			if(!(searchId==null)) {
				sql = "select count(game_result) from record_table where user_id = ? and game_result = 'win'";
				PreparedStatement pstm = conn.prepareStatement(sql);
				pstm.setString(1, searchId);
				rs = pstm.executeQuery();
			}else {
				sql = "select count(game_result) from record_table where game_result = 'win'";
				PreparedStatement pstm = conn.prepareStatement(sql);
				rs = pstm.executeQuery();
			}
			int winCount = 0;
			if(rs.next()) {
				winCount = rs.getInt("count(game_result)");
			}
			return winCount;
		}
			
		// [명주]//수정 2-2
		// 전적 집계(insert)
		// 전체 판 전적 저장한 걸 집계하여 보여주는 기능
		// 집계 내용 : 패배 횟수
		// 반환 : "int"
		public int loseCount(String searchId) throws SQLException, ClassNotFoundException, IOException {
			String sql = null;
			ResultSet rs = null;
			Connection conn = MyConnect.getConnect();
			if(!(searchId==null)) {
				sql = "select count(game_result) from record_table where user_id = ? and game_result = 'lose'";
				PreparedStatement pstm = conn.prepareStatement(sql);
				pstm.setString(1, searchId);
				rs = pstm.executeQuery();
			}else {
				sql = "select count(game_result) from record_table where game_result = 'lose'";
				PreparedStatement pstm = conn.prepareStatement(sql);
				rs = pstm.executeQuery();
			}
			int loseCount = 0;
			if(rs.next()) {
				loseCount = rs.getInt("count(game_result)");
			}
			return loseCount;
		}
			
		// [명주]//수정 2-3
		// 전적 집계(insert)
		// 전체 판 전적 저장한 걸 집계하여 보여주는 기능
		// 집계 내용 : 승률
		// 반환 : "int"
		public double winOdds(String searchId) throws SQLException, ClassNotFoundException, IOException {
			double totalRecord = winCount(searchId)+loseCount(searchId);
			double winRecord = winCount(searchId);
			double result = winRecord/totalRecord;
//			bw.write(result+"\n");
//			bw.flush();
			String sql = null;
			ResultSet rs = null;
			Connection conn = MyConnect.getConnect();
			if(!(searchId==null)) {
				sql = "select count(game_result) from record_table where user_id = ?";
				PreparedStatement pstm = conn.prepareStatement(sql);
				pstm.setString(1, searchId);
				rs = pstm.executeQuery();
			}else {
				sql = "select count(game_result) from record_table";
				PreparedStatement pstm = conn.prepareStatement(sql);
				rs = pstm.executeQuery();
			}
			int loseCount = 0;
			if(rs.next()) {
				loseCount = rs.getInt("count(game_result)");
			}
			
			return result;
		}
			
		// [명주]//수정 2-4
		// 전적 집계(insert)
		// 전체 판 전적 저장한 걸 집계하여 보여주는 기능
		// 집계 내용 : 배팅금액 평균
		// 반환 : "int"
		public double betTotAvg(String searchId) throws SQLException, ClassNotFoundException, IOException {
			String sql = null;
			ResultSet rs = null;
			Connection conn = MyConnect.getConnect();
			if(!(searchId==null)) {
				sql = "select sum(bet), count(bet) from record_table where user_id = ?";
				PreparedStatement pstm = conn.prepareStatement(sql);
				pstm.setString(1, searchId);
				rs = pstm.executeQuery();
			}else {
				sql = "select sum(bet), count(bet) from record_table";
				PreparedStatement pstm = conn.prepareStatement(sql);
				rs = pstm.executeQuery();
			}
			double betSum = 0;
			double betCnt = 0;
			if(rs.next()) {
				betSum = rs.getInt("sum(bet)");
				betCnt = rs.getInt("count(bet)");
			}
			return betSum/betCnt;
		}
			
		// [명주]//수정 2-5
		// 전적 집계(insert)
		// 전체 판 전적 저장한 걸 집계하여 보여주는 기능
		// 집계 내용 : 한판 당 평균 hit 횟수
		// 반환 : "int"
		public double hitTotAvg(String searchId) throws SQLException, ClassNotFoundException, IOException {
			String sql = null;
			ResultSet rs = null;
			Connection conn = MyConnect.getConnect();
			if(!(searchId==null)) {
				sql = "select sum(total_hit), count(total_hit) from record_table where user_id = ?";
				PreparedStatement pstm = conn.prepareStatement(sql);
				pstm.setString(1, searchId);
				rs = pstm.executeQuery();
			}else {
				sql = "select sum(total_hit), count(total_hit) from record_table";
				PreparedStatement pstm = conn.prepareStatement(sql);
				rs = pstm.executeQuery();
			}
			double hitTotSum = 0;
			double hitTotCnt = 0;
			if(rs.next()) {
				hitTotSum = rs.getInt("sum(total_hit)");
				hitTotCnt = rs.getInt("count(total_hit)");
			}
			return hitTotSum/hitTotCnt;
		}
		
		// [명주]//수정 2-6
		// 전적 집계(insert)
		// 전체 판 전적 저장한 걸 집계하여 보여주는 기능
		// 집계 내용 : 한판 당 평균 stay 횟수
		// 반환 : "int"
		public double stayTotAvg(String searchId) throws SQLException, ClassNotFoundException, IOException {
			String sql = null;
			ResultSet rs = null;
			Connection conn = MyConnect.getConnect();
			if(!(searchId==null)) {
				sql = "select sum(total_stay), count(total_stay) from record_table where user_id = ?";
				PreparedStatement pstm = conn.prepareStatement(sql);
				pstm.setString(1, searchId);
				rs = pstm.executeQuery();
			}else {
				sql = "select sum(total_stay), count(total_stay) from record_table";
				conn = MyConnect.getConnect();
				PreparedStatement pstm = conn.prepareStatement(sql);
				rs = pstm.executeQuery();
			}
			double stayTotSum = 0;
			double stayTotCnt = 0;
			if(rs.next()) {
				stayTotSum = rs.getInt("sum(total_stay)");
				stayTotCnt = rs.getInt("count(total_stay)");
			}
			return stayTotSum/stayTotCnt;
		}
}
