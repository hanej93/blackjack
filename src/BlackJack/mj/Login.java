package BlackJack.mj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import BlackJack.db.Users;
import dbconnect.MyConnect;

public class Login {
	
	
	public static boolean dd(Users user) {//로그인 id, ps 매칭 시 true
		
		String sql = "select * from customer_info where user_id = ?";
		
		try(Connection conn = MyConnect.getConnect();
				PreparedStatement pstm = conn.prepareStatement(sql)){
			ResultSet rs = pstm.executeQuery();

			if(rs.next()) {
				user.setCustomerId(rs.getInt("customer_id"));
				user.setUserId(rs.getString("user_id"));
				user.setPassword(rs.getString("password"));
				user.setPhoneNumber(rs.getString("phone_number"));
				user.setAddress(rs.getString("address"));
				
				
			}
			System.out.println("select game_id 완료");
			
			
//			if(user.getUserId().equals()) {//만들던 중
//				
//			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("select game_Id를 실행하지 못했습니다.");
		}
		
//		if(coustomer_id == user_id) {
//			
//		}
		return true;
	}
}
