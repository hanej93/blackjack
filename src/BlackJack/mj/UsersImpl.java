package BlackJack.mj;
/*
 * 작성자 : 김명주
 * 작성일 : 2021-04-19
 * 관련 서류 : 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import BlackJack.db.Users;
import dbconnect.MyConnect;

public class UsersImpl {
//	private static UsersImpl instance = new UsersImpl();
//	
//	private UsersImpl() {
//		
//	}
//	
//	public static UsersImpl getInstance() {
//		return instance;
//	}

	private BufferedReader br;
	private BufferedWriter bw;
	private String checkFunction;

	public UsersImpl(BufferedReader br, BufferedWriter bw) {
		this.br = br;
		this.bw = bw;
	}

	public String selectWithId(Users user) throws IOException {// id로 정보조회
		String sql = "select * from customer_info where user_id = ?";
		Users u = new Users();

		try (Connection conn = MyConnect.getConnect(); PreparedStatement pstm = conn.prepareStatement(sql)) {

			pstm.setString(1, user.getUserId());
			ResultSet rs = pstm.executeQuery();

			if (rs.next()) {
				u.setCustomerId(rs.getInt("customer_id"));
				u.setUserId(rs.getString("user_id"));
				u.setPassword(rs.getString("password"));
				u.setPhoneNumber(rs.getString("phone_number"));
				u.setAddress(rs.getString("address"));
			} else {
				bw.write("해당되는 아이디를 조회할 수 없습니다.\n");
				bw.flush();
				return null;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return " ";
	}

	public String login(Users user) throws IOException {
	      String sql = "select * from customer_info where user_id = ?";

	      try (Connection conn = MyConnect.getConnect(); PreparedStatement pstm = conn.prepareStatement(sql)) {

	         pstm.setString(1, user.getUserId());
	         ResultSet rs = pstm.executeQuery();
	         while (rs.next()) {
	            if (user.getPassword().equals(rs.getString("password"))) {
	               bw.write("로그인에 성공했습니다.\n");
	               bw.flush();
	            } else {
	               bw.write("로그인에 실패했습니다.");
	               bw.newLine();
	               bw.write("아이디와 패스워드가 일치하지 않습니다. 회원가입 = y, 재로그인 = n\n");
	               bw.flush();
	               
	               String isYesLogin = br.readLine();
	               if (isYesLogin.equals("y")) {
	                  bw.write("회원가입을 실행합니다.\n");
	                  bw.flush();
	                  Users signupUser = new Users(br, bw);
	                  signupUser.userSignup();
	                  return "false";
	               } else {
	                  return "false";
	               }
	            }
	         }
	      } catch (ClassNotFoundException e) {
	         e.printStackTrace();
	      } catch (SQLException e) {
	         e.printStackTrace();
	      }
	      return "true";
	   }

	public List<Users> selectList() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Users> selectWithTitle(Users movie) {
		// TODO Auto-generated method stub
		return null;
	}

	public int insert(Users user) {
		int result = 0;

		String sql = "insert into customer_info(customer_id, user_id, password, phone_number, address) "
				+ "values(?, ?, ?, ?, ?)";

		try (Connection conn = MyConnect.getConnect(); PreparedStatement pstm = conn.prepareStatement(sql)) {

			pstm.setInt(1, user.getCustomerId());
			pstm.setString(2, user.getUserId());
			pstm.setString(3, user.getPassword());
			pstm.setString(4, user.getPhoneNumber());
			pstm.setString(5, user.getAddress());

			result = pstm.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return result;
	}

	public int update(Users movie) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int delete(Users movie) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void selectTotalCount() {
		// TODO Auto-generated method stub

	}

}
