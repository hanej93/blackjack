package BlackJack.mj.test;

import java.util.Scanner;

import BlackJack.db.Users;
import BlackJack.mj.UsersImpl;

public class LoginTest {
	
	public static void main(String[] args) {
		UsersImpl u1 = UsersImpl.getInstance();
		Users u = new Users();
		while(true) {
			Scanner sc = new Scanner(System.in);
			System.out.println("아이디를 입력해주세요.");
			String gameId = sc.nextLine();
			System.out.println("비밀번호를 입력해주세요.");
			String password = sc.nextLine(); //UsersImpl login null체크 사용 안할 시 이 주석 풀기
			
			u.setUserId(gameId);
			u.setPassword(password);
			System.out.println(u);
			Users user = u1.selectWithId(u);
			
			if(user == null) {//select 시, user_id가 유효하지 않을 경우 재입력
				continue;
			}
			
			if(!(password.equals(""))) { //UsersImpl login null체크 사용 안할 시 이 주석 풀기
				System.out.println(user);
				u1.login(u);//아이디, 패스워드 일치 검사
				break;
			}else {
				System.out.println("비밀번호를 입력하지 않았습니다. 다시 입력해주세요.");
				continue;
			}
		}//while문
		System.out.println("아이디 패스워드 검사 종료");
	}
}
