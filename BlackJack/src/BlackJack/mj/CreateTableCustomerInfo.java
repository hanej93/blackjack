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
				+"address varchar(150) null COMMENT '유저 거주주소' "
				+")";
		
		//money_table 테이블 생성
		String moneyTable = "create table money_table( "
				+"money_id int primary key not null auto_increment COMMENT '자산 테이블 아이디', "//primary key
				+"customer_id int COMMENT '기본키', "
				+"money bigint default 100000000 COMMENT '기본 자산 1억', "
				+"foreign key (customer_id) references customer_info (customer_id)"
				+ ")";
		
		//customer_rank_table 테이블 생성
		String customerRankTable = "create table customer_rank_table( "
				+"customer_rank_id int primary key not null auto_increment COMMENT '유저 계급 아이디', "//primary key
				+"customer_id int COMMENT '기본키', "
				+"rank_id int not null COMMENT '랭크 아이디', "
				+"foreign key (customer_id) references customer_info (customer_id)"
				+ ")";
		
		//rank_table 테이블 생성
		String rankTable = "create table rank_table("
				+ "rank_id int not null auto_increment COMMENT '랭크 아이디', " //primary key
				+ "rankf varchar(45) not null default '아이언' COMMENT '실제 표기되는 rank', " 
				+" primary key (rank_id)"
				+ ")";
		
		//history_table 테이블 생성
		String historyTable = "create table history_table( "
				+"history_id int primary key not null auto_increment COMMENT '접속 기록 아이디', "//primary key
				+"customer_id int COMMENT '기본키', "
				+"login_date timestamp default current_timestamp not null COMMENT '접속일', "
				+"exit_date timestamp default current_timestamp not null COMMENT '종료일', "
				+" foreign key (customer_id) references customer_info (customer_id) "
				+ ")";
		
		//record_table 테이블 생성(전적 테이블)
		String recordTable = "create table record_table( "
				+"record_id int primary key not null auto_increment COMMENT '레코드 아이디', "//primary key
				+"customer_id int COMMENT '기본키', "
				+"gameresult enum('win', 'false') not null COMMENT '게임 결과', "
				+ "bet int not null COMMENT '배팅 금액', "
				+ "total_hit tinyint COMMENT '힛 총 횟수', "
				+ "total_stay tinyint COMMENT '스테이 총 횟수', "
				+"end_game_time timestamp default current_timestamp not null COMMENT '게임 종료 시간', "
				+" foreign key (customer_id) references customer_info (customer_id) "
				+ ")";

//		record_table 쿼리문
//		create table record_table(
//		record_id int primary key not null auto_increment COMMENT '레코드 아이디', 
//		customer_id int COMMENT '기본키', 
//		gameresult enum("win", "false") not null COMMENT '게임 결과', 
//		bet int not null COMMENT '배팅 금액', 
//		total_hit tinyint COMMENT '힛 총 횟수', 
//		total_stay tinyint COMMENT '스테이 총 횟수', 
//		end_game_time timestamp default current_timestamp not null COMMENT '게임 종료 시간', 
//		foreign key (customer_id) references customer_info (customer_id) );
		
		try(Connection conn = MyConnect.getConnect();
				Statement sm = conn.createStatement()){
			
			boolean runResult1 = sm.execute(customerInfoTable);//customer_info_table 테이블 생성
			boolean runResult2 = sm.execute(moneyTable);//money_table 테이블 생성
			boolean runResult3 = sm.execute(customerRankTable);//customer_rank_table 테이블 생성
			boolean runResult4 = sm.execute(rankTable);//rank_table 테이블 생성
			boolean runResult5 = sm.execute(historyTable);//history_table 테이블 생성
			boolean runResult6 = sm.execute(recordTable);//record_table 테이블 생성
			
			System.out.println(runResult1+","+runResult2+","+runResult3+","+runResult4+","+runResult5+","+runResult6+"테이블 생성 완료");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
