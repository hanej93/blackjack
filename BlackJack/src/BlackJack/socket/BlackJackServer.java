package BlackJack.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import BlackJack.db.Users;
import BlackJack.game.Card;
import BlackJack.game.CardDeck;
import BlackJack.game.Dealer;
import BlackJack.game.Player;
import BlackJack.mj.UsersImpl;

public class BlackJackServer {
	private ServerSocket serverSocket;
	private List<BlackJackServer.ServerToClient> clientList;

	// 서버소켓 생성
	public BlackJackServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		clientList = new ArrayList<BlackJackServer.ServerToClient>();

	}

	// 클라이언트로 부터 소켓을 받아오며, 받을 떄마다 새로운 쓰레드 생성
	public void runServer() throws IOException {
		while (true) {
			Socket socket = serverSocket.accept();
			ServerToClient stc = new ServerToClient(socket);
			clientList.add(stc);
			stc.start();
		}
	}

	// 실제 동작하는 쓰레드(모든 메인 기능을 수행하는 부분)
	private class ServerToClient extends Thread {

		// 통신에 관한 필드
		private Socket socket;
		private BufferedWriter bw;

		// 클라이언트 socket과 bw를 필드로 빼줌(다른 객체에서도 이것의 정보를 확인하기 위함!)
		public ServerToClient(Socket socket) throws UnsupportedEncodingException, IOException {
			this.socket = socket;
			this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
		}

		// 실제 동작하는 부분
		@Override
		public void run() {
			BufferedReader br = null;

			// 플레이어아이디, 베팅머니
			String playerId;
			long bettingMoney;

			// 전적관리에 관한 변수
			String gameResult;
			long oneGameBet;
			int totalHit;
			int totalStay;
			LocalDateTime endGameTime;

			// 히스토리에 관한 변수
			LocalDateTime accessTime;
			LocalDateTime exitTime;

			try {
				System.out.println("== 블랙잭 서버 실행 ==");

				br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

				// 서버에서 클라이언트로 보내는 메시지
				// 블랙잭 인트로(시각적으로 꾸미는 것 필요!)
				bw.write("블랙잭 서버에 오신 것을 환영합니다.\n");
				bw.flush();

				// 로그인, 회원가입 ..
				while (true) {
					bw.newLine();
					bw.newLine();
					bw.write("수행할 메뉴를 선택하고 숫자로 입력하세요.\n");
					bw.write("1. 로그인\n");
					bw.write("2. 회원가입\n");
					bw.flush();

					String loginChoice = br.readLine();

					// [로그인 기능]
					// 아이디가 있는지 확인, 아이디와 비밀번호 일치하는지 확인
					// 로그인 과정 중 실패 시 로그인,회원가입을 선택할 수 있는 단계로 넘어갈 수 있도록 수행
					if (loginChoice.equals("1")) {

						Users user = new Users(br, bw);
						UsersImpl usersImpl = new UsersImpl(br, bw);

						while (true) {
							bw.write("아이디를 입력하세요 : \n");
							bw.flush();
							playerId = br.readLine();

							// DB에서 아이디, 비밀번호 일치필요!!
							bw.write("비밀번호를 입력하세요 : \n");
							bw.flush();
							String password = br.readLine();

							user.setUserId(playerId);
							user.setPassword(password);
							String functionResult = usersImpl.selectWithId(user);

							String returnTorF = null;

							if (functionResult == null) {
								continue;
							}
							if (!(password.equals(""))) { // UsersImpl login null체크 사용 안할 시 이 주석 풀기
								returnTorF = usersImpl.login(user);// 아이디, 패스워드 일치 검사

								if (returnTorF.equals("false")) {// return값이 false면 재 로그인
									continue;
								}
								break;
							} else {
								bw.write("비밀번호를 입력하지 않았습니다. 다시 입력해주세요.\n");
								bw.flush();
								continue;
							}

						}
						break;

						// [회원가입]
						// (아이디,비밀번호,전화번호,주소 기록) - 아이디 중복확인
					} else if (loginChoice.equals("2")) {
						Users user = new Users(br, bw);
						user.userSignup();
						continue;

					} else {
						bw.write("데이터를 잘못 입력했습니다 다시 입력해주세요\n\n");
					}

				}

				// 로그인 성공 이후 접속한 날짜 저장(접속 기록을 저장하기 위함)
				accessTime = LocalDateTime.now();

				while (true) {
					bw.newLine();
					bw.newLine();
					bw.newLine();
					bw.write("=========================\n");
					bw.write("1. 블랙잭 게임 시작\n");
					bw.write("2. 플레이어 정보 검색\n");
					bw.write("3. 플레이어 전적 검색\n");
					bw.write("4. 사용자 정보 수정\n");
					bw.write("5. 랭킹 조회\n");
					bw.write("6. 게임 종료\n");
					bw.write("=========================\n");
					bw.newLine();
					bw.newLine();
					bw.newLine();
					bw.write("수행할 메뉴를 선택하고 숫자로 입력하세요.\n");
					bw.flush();
					String choiceNum = br.readLine();

					if (choiceNum.equals("1")) {
						// ♠ ♥ ♣ ♦️
						// ┍━━━━━━┑┍━━━━━━┑┍━━━━━━┑┍━━━━━━┑
						// │♠ ││♥ ││♣ ││♦️ │
						// │ ││ ││ ││ │
						// │ 10 ││ J ││ 3 ││ A │
						// │ ││ ││ ││ │
						// │ ♠││ ♥││ ♣││ ♦️│
						// ┕━━━━━━┙┕━━━━━━┙┕━━━━━━┙┕━━━━━━┙
						//
						// ┍━━━━━━┑
						// │ │
						// │ │
						// │ │
						// │ │
						// │ │
						// ┕━━━━━━┙

						// 게임 시작시 초기화(덱,딜러,유저, 한 게임 히트/스테이 횟수)
						totalHit = 0;
						totalStay = 0;
						CardDeck cardDeck = new CardDeck();
						List<Card> dealerHand = new ArrayList<Card>();
						List<Card> playerHand = new ArrayList<Card>();

						Dealer dealer = new Dealer(br, bw, cardDeck, dealerHand);
						Player player = new Player(br, bw, cardDeck, playerHand);

						// 배팅에 관한 기능 수행(보유 자산확인, 배팅에 관한 수행)
						Users moneyUser = new Users(br, bw);
						moneyUser.setUserId(playerId);
						long userAsset = moneyUser.playerMoney(playerId);
						bw.write("보유한 자산: " + userAsset);
						bw.newLine();
						bw.flush();

						while (true) {
							bw.write("배팅할 금액을 입력해주세요!\n");
							bw.flush();
							bettingMoney = Long.parseLong(br.readLine());
							if (bettingMoney > userAsset) {
								bw.write("보유한 자산보다 배팅금액이 많습니다.\n");
								bw.write("다시 입력바랍니다.\n");
								bw.flush();
								continue;
							}
							moneyUser.updateMoney(-bettingMoney);
							break;
						}

						// 딜러와 플레이어에게 카드 2장씩 분배(딜러는 한 장은 비공개, 플레이어는 모든 카드 오픈)
						bw.write("------ 딜러의 카드 ------\n");
						dealerHand = dealer.drawCard();
						dealerHand = dealer.drawCard();
						dealer.showHiddenHand();
						bw.newLine();
						bw.newLine();
						bw.newLine();

						bw.write("------ 플레이어의 카드 ------\n");
						playerHand = player.drawCard();
						playerHand = player.drawCard();
						player.showHand();

						bw.write("=====================\n");
						bw.write(" 플레이어 손 패의 총합 : " + player.sumHand());
						bw.newLine();
						bw.write("=====================\n");
						bw.flush();
						bw.newLine();
						bw.newLine();
						bw.newLine();

						// 플레이어 카드 완성단계(힛, 스테이 선택)
						while (true) {
							bw.write("패를 보충하시겠습니까? 1.hit 2.stay\n");
							bw.flush();
							String handHit = br.readLine();
							if (handHit.equals("1")) {
								playerHand = player.drawCard();
								player.showHand();
								bw.write("=====================\n");
								bw.write(" 플레이어 손 패의 총합 : " + player.sumHand());
								bw.newLine();
								bw.write("=====================\n");
								bw.flush();
								totalHit++;

								if (player.isBust()) {
									break;
								}
							} else if (handHit.equals("2")) {
								totalStay++;
								break;
							} else {
								bw.write("정확한 숫자를 입력바랍니다\n");
								bw.newLine();
								bw.newLine();
							}
						}
						bw.newLine();
						bw.newLine();
						bw.newLine();

						if (player.sumHand() < 21) {
							bw.write("=====================\n");
							bw.write(" 플레이어 손 패의 총합 : " + player.sumHand());
							bw.newLine();
							bw.write("=====================\n");
						} else if (player.sumHand() == 21) {
							bw.write("★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆\n");
							bw.write("　　　　　　　블랙잭                 \n ");
							bw.write("★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆\n");
						} else {
							bw.write("버스트... ㅜㅜ\n");
						}
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();

						// 딜러 카드 완성 단계(합이 17이 넘을 때까지 카드를 뽑음)
						dealer.makeDealerHand();
						bw.write("------ 딜러 최종 핸드 ------\n");
						dealer.showHand();
						bw.write("=====================\n");
						bw.write(" 딜러 손 패의 총합 : " + dealer.sumHand());
						bw.newLine();
						bw.write("=====================\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();

						// 승리 패배 결과 정리(배팅금 자산 업데이트)

						// 일부 미구현!!!!
						// : 얼마를 벌고 잃었는지 출력 필요
						if (!player.isBust() && player.sumHand() > dealer.sumHand()) {
							bw.write("플레이어 승리");
							moneyUser.updateMoney(bettingMoney * 2);
							bw.write(Long.toString(moneyUser.playerMoney(playerId)));
							bw.newLine();
							bw.flush();

							gameResult = "Win";

						} else if (!player.isBust() && dealer.isBust()) {
							bw.write("플레이어 승리");
							moneyUser.updateMoney(bettingMoney * 2);
							bw.write("플레이어 보유 자산: " + Long.toString(moneyUser.playerMoney(playerId)));
							bw.newLine();
							bw.flush();

							gameResult = "Win";

						} else if (!player.isBust() && player.sumHand() == dealer.sumHand()) {
							bw.write("무승부");
							moneyUser.updateMoney(bettingMoney);
							bw.write("플레이어 보유 자산: " + Long.toString(moneyUser.playerMoney(playerId)));
							bw.newLine();
							bw.flush();

							gameResult = "Draw";

						} else if (player.isBust() && dealer.isBust()) {
							bw.write("무승부");
							moneyUser.updateMoney(bettingMoney);
							bw.write("플레이어 보유 자산: " + Long.toString(moneyUser.playerMoney(playerId)));
							bw.newLine();
							bw.flush();

							gameResult = "Draw";

						} else {
							bw.write("딜러 승리");
							bw.write("플레이어 보유 자산 : " + Long.toString(moneyUser.playerMoney(playerId)));
							bw.newLine();
							bw.flush();

							gameResult = "Defeat";

						}
						bw.flush();

						// 게임 종료 시간 저장(전적 테이블에 사용)
						endGameTime = LocalDateTime.now();

						
						
						
						// 미구현!!!! 최우선 사항
						/*
						 * 보유한 자산을 기반으로 티어 업데이트 기능 구현 필요
						 * 
						 * 예시)
						 * 
						 * if(자산>100000) 
						 *  그랜드마스터 
						 * 
						 * else if (자산 >8000) 
						 *  마스터 
						 * ....
						 * 
						 * 파라미터: 로그인한 아이디(playerId), 반영할 티어
						 * 
						 */
						
						
						/*
						 * 전적테이블에 인설트 기능 추가해야 함
						 * 
						 * 파라미터: playerId gameResult oneGameBet totalHit totalStay endGameTime
						 * 
						 */

						
						
						// 2. 사용자 정보 조회(아이디를 통한 검색)
					} else if (choiceNum.equals("2")) {
						Users user = new Users(br, bw);
						bw.write("아이디를 입력하세요\n");
						bw.flush();
						user.setUserId(br.readLine());

						user.userInformationSelect();

						continue;

						// 3. 전적 테이블 조회(아이디를 통한 검색)
						// 일부 기능 추가 필요(+ 승률, 승리/패배 횟수, 한 게임 평균 히트)
					} else if (choiceNum.equals("3")) {

						Users user = new Users(br, bw);

						bw.write("전적을 조회할 아이디를 입력해주세요.\n");
						bw.flush();
						String str = br.readLine();
						user.RecordTableLookUp(str);

						continue;

						// 4. 사용자 정보 수정 (로그인한 본인 정보 수정)
						// 비밀번호, 전화번호, 주소 변경 가능(수정하기 원하는 것 선택 가능)
					} else if (choiceNum.equals("4")) {
						Users user = new Users(br, bw);
						user.userInformationUpdate(playerId);
						continue;

						// 5. 랭킹 조회(1~10위 까지 조회)
						// 일부 기능 추가필요(+ 본인 등수 및 정보)
					} else if (choiceNum.equals("5")) {
						Users user = new Users(br, bw);
						user.selectRank();
						continue;

						// 6. 게임 종료
						// 종료시 접속기록 인설트
					} else if (choiceNum.equals("6")) {

						bw.write("블랙잭을 종료합니다!\n");
						bw.flush();

						exitTime = LocalDateTime.now();
						Users user = new Users();
						user.accessTimeSet(playerId, accessTime, exitTime);

						break;

					} else {
						bw.write("정확한 숫자를 입력바랍니다.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						continue;
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("서버: 클라이언트 강제종료!");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
