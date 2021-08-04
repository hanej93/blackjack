package BlackJack.socket;

import java.io.IOException;

public class ServerTest {
	public static void main(String[] args) {
		BlackJackServer server;
		try {
			server = new BlackJackServer(8181);
			server.runServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
