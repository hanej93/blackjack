package BlackJack.mj.test;

import java.util.Scanner;

import BlackJack.db.Users;
import BlackJack.db.UsersImpl;

public class InsertTest {
	public static void main(String[] args) {
		UsersImpl u1 = UsersImpl.getInstance();
		Scanner sc = new Scanner(System.in);
		System.out.println("사용자 정보를 입력해주세요. (아이디, 비번, 폰번호, 주소)");
		String gameId = sc.nextLine();
		String password = sc.nextLine();
		String phoneNumber = sc.nextLine();
		String address = sc.nextLine();
		
		Users u = new Users();
		u.setUserId(gameId);
		u.setPassword(password);
		u.setPhoneNumber(phoneNumber);
		u.setAddress(address);
		int row = u1.insert(u);
		System.out.println(row);
	}
}
