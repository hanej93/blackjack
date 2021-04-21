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

	public BlackJackServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		clientList = new ArrayList<BlackJackServer.ServerToClient>();

	}

	public void runServer() throws IOException {
		while (true) {
			Socket socket = serverSocket.accept();
			ServerToClient stc = new ServerToClient(socket);
			clientList.add(stc);
			stc.start();
		}
	}

	private class ServerToClient extends Thread {

		private Socket socket;
		private BufferedWriter bw;
		private String playerId;

		public ServerToClient(Socket socket) throws UnsupportedEncodingException, IOException {
			this.socket = socket;
			this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
		}

		@Override
		public void run() {
			BufferedReader br = null;

			try {
				System.out.println("== 블랙잭 서버 실행 ==");
				br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

				// 서버에서 클라이언트로 보내는 메시지
				// 블랙잭 인트로
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

					if (loginChoice.equals("1")) {

		                  Users user = new Users(br, bw);
		                  UsersImpl usersImpl = new UsersImpl(br,bw);

		                  while (true) {
		                     bw.write("아이디를 입력하세요 : \n");
		                     bw.flush();
		                     this.playerId = br.readLine();

		                     // DB에서 아이디, 비밀번호 일치필요!!
		                     bw.write("비밀번호를 입력하세요 : \n");
		                     bw.flush();
		                     String password = br.readLine();

		                     user.setUserId(playerId);
		                     user.setPassword(password);
		                     String functionResult = usersImpl.selectWithId(user);
		                     
		                     String returnTorF = null;
		                     
		                     if(functionResult == null) {
		                        continue;
		                     }
		                     if(!(password.equals(""))) { //UsersImpl login null체크 사용 안할 시 이 주석 풀기
		                        returnTorF = usersImpl.login(user);//아이디, 패스워드 일치 검사
		                        
		                        if(returnTorF.equals("false")) {//return값이 false면 재 로그인
		                           continue;
		                        }
		                        break;
		                     }else {
		                        bw.write("비밀번호를 입력하지 않았습니다. 다시 입력해주세요.\n");
		                        bw.flush();
		                        continue;
		                     }
		                     
		                     // 실패 시 continue
		                     // 로그인 성공처리 후 반복문 break
		                  }
		                  break;
		               } else if (loginChoice.equals("2")) {
		                  Users user = new Users(br, bw);
		                  user.userSignup();
		                  continue;

		               } else {
		                  bw.write("데이터를 잘못 입력했습니다 다시 입력해주세요\n\n");
		               }

				}

				// 요기에 접속 시작 날짜 업데이트 해줘야함!! ㅎ
				
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

						CardDeck cardDeck = new CardDeck();
						List<Card> dealerHand = new ArrayList<Card>();
						List<Card> playerHand = new ArrayList<Card>();

						Dealer dealer = new Dealer(br, bw, cardDeck, dealerHand);
						Player player = new Player(br, bw, cardDeck, playerHand);

						// 자산 조회기능 미구현!!!!!
						// int userAsset = user.userAsset();
						// bw.write("보유한 자산: " + userAsset);
						// bw.newLine();
						// bw.flush();

						/*
						 * while(true) { bw.write("배팅할 금액을 입력해주세요!\n"); bw.flush(); int bettingMoney =
						 * Integer.parseInt(br.readLine()); if(bettingMoney > userAsset) {
						 * bw.write("보유한 자산보다 배팅금액이 많습니다.\n"); bw.write("다시 입력바랍니다.\n"); bw.flush(); }
						 * Users user = new Users(br, bw); user.updateBet(); }
						 */

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
								if (player.isBust()) {
									break;
								}
							} else if (handHit.equals("2")) {
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

						if (!player.isBust() && player.sumHand() > dealer.sumHand()) {
							bw.write("플레이어 승리");
							// 배팅금액 만큼 플페이어 자산 업데이트(증가)
							// 전적테이블 업데이트(플레이종료시간, 승패)

						} else if (!player.isBust() && dealer.isBust()) {
							bw.write("플레이어 승리");
						} else if (!player.isBust() && player.sumHand() == dealer.sumHand()) {
							bw.write("무승부");
						} else {
							bw.write("딜러 승리");
							// 배팅금액 만큼 플레이어 자산 업데이트(차감)
						}
						bw.flush();

						// 미구현!!!!
						// *************************************
						// 보유한 자산을 기반으로 등급 업데이트하는 메소드
						// 보유한 자산을 조회하여 반환한 것을 파라미터로 넘겨줌
						// user.updateRating(보유한 자산을 넘겨줌);
						// *************************************

					} else if (choiceNum.equals("2")) {
						Users user = new Users(br, bw);
						bw.write("아이디를 입력하세요\n");
						bw.flush();
						user.setUserId(br.readLine());

						// 사용자정보 조회
						user.userInformationSelect();
						continue;

					} else if (choiceNum.equals("3")) {
						// 전적 테이블 조회

					} else if (choiceNum.equals("4")) {
						// 커스터머 테이블 업데이트(본인 것만 가능!)
						Users user = new Users(br, bw);
						bw.write("아이디를 입력하세요\n");
						bw.flush();
						user.setUserId(br.readLine());

						// 사용자정보 수정
						user.userInformationUpdate();
						continue;

					} else if (choiceNum.equals("5")) {

						// 랭킹조회 메소드 구현

						continue;
					} else if (choiceNum.equals("6")) {

						bw.write("블랙잭을 종료합니다!\n");
						bw.flush();
						// 게임 종료시간 업데이트해줘야함!

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
