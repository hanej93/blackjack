package BlackJack.mj;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import dbconnect.MyConnect;

//-------------------CreateTableCustomerInfo-----------------------------
public class CreateTableCustomerInfo {
	
	public static void main(String[] args) {
		//customer_info_table 테이블 생성
		String customerInfoTable = "create table customer_info( "
				+"customer_id int primary key not null auto_increment unique key COMMENT '기본키', "//primary key - *
				+"user_id varchar(30) not null COMMENT '유저 아이디', "
				+"password varchar(20) not null COMMENT '유저 비밀번호', "
				+"phone_number varchar(20) not null COMMENT '유저 폰번호', "
				+"address varchar(150) null COMMENT '유저 거주주소', "
				+ "money bigint default 100000000 COMMENT '기본 자산 1억', "
				+ "rankf varchar(45) not null default '아이언' COMMENT '실제 표기되는 rank'"
				+")";
		
		//history_table 테이블 생성
		String historyTable = "create table history_table( "
				+"history_id int primary key not null auto_increment COMMENT '접속 기록 아이디', "//primary key
				+"user_id varchar(30) not null COMMENT '유저 아이디', "
				+"access_date timestamp default current_timestamp not null COMMENT '접속일', "
				+"exit_date timestamp default current_timestamp not null COMMENT '종료일' "
				+ ")";
		
		//record_table 테이블 생성(전적 테이블)
        String recordTable = "create table record_table( "
                +"record_id int primary key not null auto_increment COMMENT '레코드 아이디', "//primary key
                +"user_id varchar(30) not null COMMENT '유저 아이디', "
                +"game_result enum('win','draw', 'lose') not null COMMENT '게임 결과', "
                + "bet bigint not null COMMENT '배팅 금액', "
                + "total_hit tinyint COMMENT '힛 총 횟수', "
                + "total_stay tinyint COMMENT '스테이 총 횟수', "
                + "start_game_time timestamp default current_timestamp not null COMMENT '게임 시작 시간', "
                +"end_game_time timestamp default current_timestamp not null COMMENT '게임 종료 시간'"
                + ")";

		
		try(Connection conn = MyConnect.getConnect();
				Statement sm = conn.createStatement()){
			
			boolean runResult1 = sm.execute(customerInfoTable);//customer_info_table 테이블 생성
			boolean runResult2 = sm.execute(historyTable);//history_table 테이블 생성
			boolean runResult3 = sm.execute(recordTable);//record_table 테이블 생성
			
			System.out.println(runResult1+","+runResult2+","+runResult3+"테이블 생성 완료");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
