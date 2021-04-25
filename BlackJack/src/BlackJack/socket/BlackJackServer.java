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
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import BlackJack.db.Users;
import BlackJack.game.Card;
import BlackJack.game.CardDeck;
import BlackJack.game.Dealer;
import BlackJack.game.Player;

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

			// 전적관리에 필요한 변수
			String gameResult;
			long oneGameBet;
			int totalHit;
			int totalStay;
			LocalDateTime endGameTime;

			// 히스토리에 기록하기 위한 변수
			LocalDateTime accessTime;
			LocalDateTime exitTime;

			try {
				System.out.println("== 블랙잭 서버 실행 ==");
				br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

				// 서버에서 클라이언트로 보내는 메시지
				// 블랙잭 인트로(시각적으로 꾸미는 것 필요!)
				bw.write("블랙잭 서버에 오신 것을 환영합니다.\n");
				bw.flush();

				// [로그인기능 - 명주, 회원가입 - 명주,승연,의진]
				// 로그인, 회원가입 기능
				while (true) {
					bw.newLine();
					bw.newLine();
					bw.write("수행할 메뉴를 선택하고 숫자로 입력하세요.\n");
					bw.write("1. 로그인\n");
					bw.write("2. 회원가입\n");
					bw.flush();

					String loginChoice = br.readLine();

					// [로그인 기능] - [명주]
					// 아이디가 있는지 확인, 아이디와 비밀번호 일치하는지 확인
					// 로그인 과정 중 실패 시 로그인,회원가입을 선택할 수 있는 단계로 넘어갈 수 있도록 수행
					if (loginChoice.equals("1")) {

						Users user = new Users(br, bw);

						while (true) {
							bw.write("아이디를 입력하세요 : \n");
							bw.flush();
							playerId = br.readLine();

							bw.write("비밀번호를 입력하세요 : \n");
							bw.flush();
							String password = br.readLine();

							if ((playerId.equals("")) || (password.equals(""))) {// 빈값체크
								bw.write("아이디 혹은 비밀번호를 입력하지 않았습니다. 다시 입력해주세요.\n");
								bw.flush();
								continue;
							}

							boolean tf = user.userCheck(playerId, password);

							if (tf) {// 로그인 성공 시
								bw.write("로그인에 성공하셨습니다.\n");
								bw.flush();
								break;
							} else {// 로그인 실패
								bw.write("해당되는 아이디를 조회할 수 없습니다. 회원가입 = y, 재로그인 = n\n");
								bw.flush();
								String isYesLogin = br.readLine();
								if (isYesLogin.equals("y")) {// 회원가입 실행하겠습니다.
									bw.write("회원가입을 실행합니다.\n");
									bw.flush();
									user.userSignup(user);
								} else if (isYesLogin.equals("n")) {
									continue;
								} else {
									bw.write("다시 입력해주세요.\n");
									bw.flush();
								}
							}
						}
						break;

						// [회원가입] - [승연, 명주, 의진]
						// (아이디,비밀번호,전화번호,주소 기록) - 아이디 중복확인
					} else if (loginChoice.equals("2")) {
						Users user = new Users(br, bw);
						user.userSignup(user);
						continue;

					} else {
						bw.write("데이터를 잘못 입력했습니다 다시 입력해주세요\n");
						continue;
					}

				}

				// 로그인 성공 이후 접속한 날짜 저장(접속 기록을 저장하기 위함)
				accessTime = LocalDateTime.now();

				GAME_MENU_LOOP: while (true) {
					bw.newLine();
					bw.newLine();
					bw.newLine();
					bw.write("=========================\n");
					bw.write("1. 블랙잭 게임 시작\n");
					bw.write("2. 플레이어 정보 검색\n");
					bw.write("3. 플레이어 전적 검색\n");
					bw.write("4. 사용자 정보 수정\n");
					bw.write("5. 랭킹 조회\n");
					bw.write("6. 전적 집계\n");
					bw.write("7. 게임 종료\n");
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
						gameResult = "win";// 게임 승패저장(base = 'win')
						String win = "¯\\_( ͡° ͜ʖ ͡°)_/¯";// 게임 이겼을 때 이모티콘
						String draw = "( ͡° ͜ʖ ͡°)";// 게임 비겼을 때 이모티콘
						String lose = "ಠ_ಠ";// 게임 졌을 때 이모티콘

						CardDeck cardDeck = new CardDeck();
						List<Card> dealerHand = new ArrayList<Card>();
						List<Card> playerHand = new ArrayList<Card>();

						Dealer dealer = new Dealer(br, bw, cardDeck, dealerHand);
						Player player = new Player(br, bw, cardDeck, playerHand);
						Users playerUser = new Users(br, bw);

						// 배팅에 관한 기능 수행(보유 자산확인, 배팅에 관한 수행)
						long userOldAsset = playerUser.selectUserAssets(playerId);
						bw.write("보유한 자산: " + userOldAsset);
						bw.newLine();
						bw.flush();

						while (true) {
							bw.write("배팅할 금액을 입력해주세요!\n");
							bw.flush();
							oneGameBet = Long.parseLong(br.readLine());
							if (oneGameBet > userOldAsset) {
								bw.write("보유한 자산보다 배팅금액이 많습니다.\n");
								bw.write("다시 입력바랍니다.\n");
								bw.flush();
								continue;
							}
							if (oneGameBet < 0) {
								bw.write("유효한 숫자를 입력 바랍니다.\n");
								bw.newLine();
								bw.newLine();
								bw.flush();
								continue;
							}
							playerUser.updateUserAssetsM(oneGameBet, playerId);
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

						bw.write("딜러의 카드를 확인하셨습니까? Enter를 입력해주세요.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();
						br.readLine();

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

						bw.write("본인의 카드를 확인하셨습니까? Enter를 입력해주세요.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();
						br.readLine();

						double twoCardBlackJack = 0;

						long insuranceMoney = 0;
						boolean checkInsurance = false;

						int doubleDown = 1;
						boolean checkDoubleDown = false;

						// [이븐머니]
						if (player.isTwoCardBlackJack() && dealerHand.get(1).getCardNum().equals("A")) {

							while (true) {
								bw.write("이븐머니를 하시겠습니까? y/n\n");
								bw.flush();
								String yesOrNo = br.readLine();
								if (yesOrNo.equals("y")) {
									// 무승부 시 본전치기(숫자의 합이 같을 시)
									// 즉시 승리 배팅금만큼 그대로 받음
									bw.write(win + "\n");
									bw.write("플레이어 승리(특수 승리: 이븐머니)\n");
									bw.write("게임 전 금액 : " + userOldAsset + "\n");
									bw.write("변동 금액 : +" + oneGameBet + "\n");
									playerUser.updateUserAssetsP(oneGameBet * 2, playerId);
									bw.write("게임 후 금액 : " + playerUser.selectUserAssets(playerId) + "\n");
									bw.flush();
									bw.newLine();
									bw.newLine();
									bw.newLine();
									bw.write("메뉴로 이동하시겠습니까? Enter를 입력해주세요.\n");
									bw.newLine();
									bw.newLine();
									bw.newLine();
									bw.flush();
									br.readLine();
									continue GAME_MENU_LOOP;
								} else if (yesOrNo.equals("n")) {
									twoCardBlackJack = 0.5;
									break;
								} else {
									bw.write("정확한 문자를 입력바랍니다.\n");
									bw.flush();
									continue;
								}
							}
						}

						// [인셔런스]
						else if (dealerHand.get(1).getCardNum().equals("A")
								&& playerUser.selectUserAssets(playerId) >= (long) (oneGameBet / 2)) {

							while (true) {
								bw.write("인셔런스를 하시겠습니까? y/n\n");
								bw.flush();
								String yesOrNo = br.readLine();
								if (yesOrNo.equals("y")) {
									checkInsurance = true;
									insuranceMoney = (long) (oneGameBet / 2);
									playerUser.updateUserAssetsM(insuranceMoney, playerId);
									break;
								} else if (yesOrNo.equals("n")) {
									break;
								} else {
									bw.write("정확한 문자를 입력바랍니다.\n");
									bw.flush();
									continue;
								}
							}
						}

						// [일반 블랙잭](처음받은 두 카드의 합이 21)
						else if (player.isTwoCardBlackJack()) {
							twoCardBlackJack = 0.5;
						}

						
						String yesOrNo = "n";
						// [일반 게임](더블 다운 체크)
						while (true) {
							if (playerUser.selectUserAssets(playerId) >= oneGameBet && player.sumHand() < 20) {
								bw.write("더블 다운을 하시겠습니까? y/n\n");
								bw.flush();
								yesOrNo = br.readLine();
							}
							if (yesOrNo.equals("y")) {
								playerUser.updateUserAssetsM(oneGameBet, playerId);
								oneGameBet *= 2;
								checkDoubleDown = true;
								playerHand = player.drawCard();
								player.showHand();
								bw.write("=====================\n");
								bw.write(" 플레이어 손 패의 총합 : " + player.sumHand());
								bw.newLine();
								bw.write("=====================\n");
								bw.flush();
								bw.write("본인의 카드를 확인하셨습니까? Enter를 입력해주세요.\n");
								bw.newLine();
								bw.newLine();
								bw.newLine();
								bw.flush();
								br.readLine();
								break;

							} else if (yesOrNo.equals("n")) {
								bw.newLine();
								bw.newLine();
								bw.newLine();
								bw.flush();
								while (true) {
									bw.write("패를 보충하시겠습니까? 1.hit 2.stay\n");
									bw.flush();
									String handHit = br.readLine();
									if (handHit.equals("1")) {
										totalHit++;
										playerHand = player.drawCard();
										player.showHand();
										bw.write("=====================\n");
										bw.write(" 플레이어 손 패의 총합 : " + player.sumHand());
										bw.newLine();
										bw.write("=====================\n");
										bw.flush();
										bw.write("본인의 카드를 확인하셨습니까? Enter를 입력해주세요.\n");
										bw.newLine();
										bw.newLine();
										bw.newLine();
										bw.flush();
										br.readLine();
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
										continue;
									}
								}
								break;
							} else {
								bw.write("정확한 문자를 입력바랍니다.\n");
								bw.flush();
								continue;
							}
						}

						// 플레이어 카드 완성단계(힛, 스테이 선택)

						bw.newLine();
						bw.newLine();
						bw.newLine();

						// 플레이어 결과 출력
						if (player.sumHand() < 21) {
							bw.write("=====================\n");
							bw.write(" 플레이어 손 패의 총합 : " + player.sumHand());
							bw.newLine();
							bw.write("=====================\n");
						} else if (player.sumHand() == 21) {
							bw.write("♤♥♧◆♤♥♧◆♤♥♧◆♤♥♧◆♤♥♧◆♤♥♧◆\n");
							bw.write("　　　　　　　블랙잭                 \n ");
							bw.write("♤♥♧◆♤♥♧◆♤♥♧◆♤♥♧◆♤♥♧◆♤♥♧◆\n");
						} else {
							bw.write("✧/ᐠ-ꞈ-ᐟ\\✧/ᐠ-ꞈ-ᐟ\\✧/ᐠ-ꞈ-ᐟ\\✧/ᐠ-ꞈ-ᐟ\\✧/ᐠ-ꞈ-ᐟ\\✧\n ");
							bw.write("		BUST		\n");
							bw.write("✧/ᐠ-ꞈ-ᐟ\\✧/ᐠ-ꞈ-ᐟ\\✧/ᐠ-ꞈ-ᐟ\\✧/ᐠ-ꞈ-ᐟ\\✧/ᐠ-ꞈ-ᐟ\\✧\n ");
						}
						
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();

						bw.write("딜러의 최종 핸드를 확인하시겠습니까? Enter를 입력해주세요.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();
						br.readLine();

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

						bw.write("게임 결과!! Enter를 입력해주세요.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();
						br.readLine();

						// 게임 종료시점 저장
						endGameTime = LocalDateTime.now();

						// [인셔런스 성공 정산]
						if (checkInsurance && dealer.sumHand() == 21) {
							playerUser.updateUserAssetsP(insuranceMoney * 2, playerId);
							bw.write("딜러 블랙잭 !!  [인셔런스 성공]\n");
							bw.write("인셔런스 수당 지급\n");
							bw.write("인셔런스 수당: " + insuranceMoney);
							bw.newLine();
							bw.newLine();
							bw.write("최종 결과 조회!! Enter를 입력해주세요.\n");
							bw.newLine();
							bw.newLine();
							bw.newLine();
							bw.flush();
							br.readLine();
						}

						// 게임 후 변동된 자산 체크
						long changedMoney = 0;
						// 승리 패배 결과 정리(배팅금 자산 업데이트)
						if (!player.isBust() && player.sumHand() > dealer.sumHand()) {
							bw.write(win + "\n");
							bw.write("플레이어 승리\n");
							// 배팅금액 만큼 플페이어 자산 업데이트(증가)
							bw.write("게임 전 금액 : " + userOldAsset + "\n");
							playerUser.updateUserAssetsP((long) (oneGameBet * (2 + twoCardBlackJack)), playerId);
							changedMoney = playerUser.selectUserAssets(playerId) - userOldAsset;
							bw.write("변동 금액 : " + changedMoney + "\n");
							bw.write("게임 후 금액 : " + playerUser.selectUserAssets(playerId) + "\n");

						} else if (!player.isBust() && dealer.isBust()) {
							bw.write(win + "\n");
							bw.write("플레이어 승리\n");
							bw.write("게임 전 금액 : " + userOldAsset + "\n");
							playerUser.updateUserAssetsP((long) (oneGameBet * (2 + twoCardBlackJack)), playerId);

							bw.write("게임 후 금액 : " + playerUser.selectUserAssets(playerId) + "\n");

						} else if (!player.isBust() && player.sumHand() == dealer.sumHand()) {
							bw.write(draw + "\n");
							bw.write("무승부\n");
							gameResult = "draw";
							bw.write("게임 전 금액 : " + userOldAsset + "\n");
							playerUser.updateUserAssetsP(oneGameBet, playerId);
							changedMoney = playerUser.selectUserAssets(playerId) - userOldAsset;
							bw.write("변동 금액 : " + changedMoney + "\n");
							bw.write("게임 후 금액 : " + playerUser.selectUserAssets(playerId) + "\n");

						} else if (player.isBust() && dealer.isBust()) { // 둘 다 버스트시 일반적으로 패배지만 무승부로 쳐주기로...
							bw.write(draw + "\n");
							bw.write("무승부\n");
							gameResult = "draw";
							bw.write("게임 전 금액 : " + userOldAsset + "\n");
							playerUser.updateUserAssetsP(oneGameBet, playerId);
							changedMoney = playerUser.selectUserAssets(playerId) - userOldAsset;
							bw.write("변동 금액 : " + changedMoney + "\n");
							bw.write("게임 후 금액 : " + playerUser.selectUserAssets(playerId) + "\n");

						} else {
							bw.write(lose + "\n");
							bw.write("딜러 승리\n");
							gameResult = "lose";
							// 배팅금액 만큼 플레이어 자산 업데이트(차감)
							bw.write("게임 전 금액 : " + userOldAsset + "\n");
							changedMoney = playerUser.selectUserAssets(playerId) - userOldAsset;
							bw.write("변동 금액 : " + changedMoney + "\n");
							bw.write("게임 후 금액 : " + playerUser.selectUserAssets(playerId) + "\n");
						}

						bw.flush();
						// 전적테이블 업데이트
						playerUser.recordSet(playerId, gameResult, oneGameBet, totalHit, totalStay, endGameTime);

						// 랭크 업데이트
						playerUser.UserAssetsForRank(playerId);

						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.write("메뉴로 이동하시겠습니까? Enter를 입력해주세요.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();
						br.readLine();

						// 2. 사용자 정보 조회(아이디를 통한 검색)
					} else if (choiceNum.equals("2")) {
						Users user = new Users(br, bw);
						bw.write("아이디를 입력하세요\n");
						bw.flush();

						// 사용자정보 조회
						user.userInformationSelect(br.readLine());

						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.write("메뉴로 이동하시겠습니까? Enter를 입력해주세요.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();
						br.readLine();
						continue;

						// 3. 전적 테이블 조회(아이디를 통한 검색)
						// 일부 기능 추가 필요(+ 승률, 승리/패배 횟수, 한 게임 평균 히트)
					} else if (choiceNum.equals("3")) {
						// 전적 테이블 조회
						Users user = new Users(br, bw);

						bw.write("전적을 조회할 아이디를 입력해주세요.\n");
						bw.flush();
						String str = br.readLine();

						boolean tf = user.RecordTableLookUp(str);
						if (tf == false) {
							bw.write("조회할 전적이 없습니다.\n");
							bw.flush();
						}

						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.write("메뉴로 이동하시겠습니까? Enter를 입력해주세요.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();
						br.readLine();
						continue;

						// 4. 사용자 정보 수정 (로그인한 본인 정보 수정)
						// 비밀번호, 전화번호, 주소 변경 가능(수정하기 원하는 것 선택 가능)
					} else if (choiceNum.equals("4")) {
						Users user = new Users(br, bw);

						// 사용자정보 수정
						user.userInformationUpdate(playerId);

						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.write("메뉴로 이동하시겠습니까? Enter를 입력해주세요.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();
						br.readLine();
						continue;

						// 5. 랭킹 조회(1~10위 까지 조회)
						// 일부 기능 추가필요(+ 본인 등수 및 정보)
					} else if (choiceNum.equals("5")) {

						// 랭킹조회 메소드 구현
						Users user = new Users(br, bw);
						user.joinRank(playerId);

						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.write("메뉴로 이동하시겠습니까? Enter를 입력해주세요.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();
						br.readLine();
						continue;

						// 6. 전적 집계
					}else if (choiceNum.equals("6")) {
							bw.newLine();
							bw.newLine();
							bw.write("전적 집계를 선택하셨습니다.\n");
							bw.newLine();
							bw.write("1. ID로 집계하기\n");
							bw.write("2. 전체로 집계하기\n");
							bw.flush();
							String totChoiceNum = br.readLine();
							Users user = new Users(br, bw);
							String searchId = null;
							DecimalFormat form = new DecimalFormat("#.##");
							
							if(totChoiceNum.equals("1")) {
								searchId = user.recordIdSet();
								int winCount = user.winCount(searchId);// 승리 횟수
								int loseCount = user.loseCount(searchId);// 패배 횟수
								String winOdds =  form.format(user.winOdds(searchId));// 승률
								String betTotAvg = form.format(user.betTotAvg(searchId));// 배팅금액 평균
								String hitTotAvg = form.format(user.hitTotAvg(searchId));// 한판 당 평균 hit 횟수
								String stayTotAvg = form.format(user.stayTotAvg(searchId));// 한판 당 평균 stay 횟수
								String result = "" + "┌───────────────────────────────────┐" + "\n   [전적 집계]              \n"
										+ " 총 승리 횟수 : " + winCount + "       \n" + " 총 패배 횟수 : " + loseCount + "       \n"
										+ " 승률 : " + winOdds + "       \n" + " 배팅금액 평균 : " + betTotAvg + "       \n"
										+ " 게임 당 평균 hit 횟수 : " + hitTotAvg + "       \n" + " 게임 당 평균 stay 횟수 : " + stayTotAvg
										+ "       \n" + "└───────────────────────────────────┚";

								bw.write(result + "\n");
								bw.flush();
							}else if(totChoiceNum.equals("2")) {
								int winCount = user.winCount(searchId);// 승리 횟수
								int loseCount = user.loseCount(searchId);// 패배 횟수
								String winOdds = form.format(user.winCount(searchId) / (user.winCount(searchId) + user.loseCount(searchId)));// 승률
								String betTotAvg = form.format(user.betTotAvg(searchId));// 배팅금액 평균
								String hitTotAvg = form.format(user.hitTotAvg(searchId));// 한판 당 평균 hit 횟수
								String stayTotAvg = form.format(user.stayTotAvg(searchId));// 한판 당 평균 stay 횟수
								String result = "" + "┌───────────────────────────────────┐" + "\n   [전적 집계]              \n"
										+ " 총 승리 횟수 : " + winCount + "       \n" + " 총 패배 횟수 : " + loseCount + "       \n"
										+ " 승률 : " + winOdds + "       \n" + " 배팅금액 평균 : " + betTotAvg + "       \n"
										+ " 게임 당 평균 hit 횟수 : " + hitTotAvg + "       \n" + " 게임 당 평균 stay 횟수 : " + stayTotAvg
										+ "       \n" + "└───────────────────────────────────┚";

								bw.write(result + "\n");
								bw.flush();
							}else {
								bw.write("잘못 입력하셨습니다. 다시 입력해주세요.\n");
								bw.flush();
								continue;
							}
							bw.newLine();
							bw.newLine();
							bw.newLine();
							bw.write("메뉴로 이동하시겠습니까? Enter를 입력해주세요.\n");
							bw.newLine();
							bw.newLine();
							bw.newLine();
							bw.flush();
							br.readLine();
							continue;

							// 7. 게임 종료
							// 종료시 접속기록 인설트
						} else if (choiceNum.equals("7")) {

						bw.write("블랙잭을 종료합니다!\n");
						bw.flush();

						// 게임 종료시간 업데이트
						exitTime = LocalDateTime.now();
						Users user = new Users();
						user.historySet(playerId, accessTime, exitTime);// history테이블 저장 완료
						
						bw.write("승연 명주님 수고하셨습니다!!\n");
						bw.flush();
						break;

					} else {
						bw.write("정확한 숫자를 입력바랍니다.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();

						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.write("메뉴로 이동하시겠습니까? Enter를 입력해주세요.\n");
						bw.newLine();
						bw.newLine();
						bw.newLine();
						bw.flush();
						br.readLine();
						continue;
					}

				}

			} catch (IOException e) {
				System.out.println("서버: 클라이언트가 강제 종료되었습니다.");
//				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

}
