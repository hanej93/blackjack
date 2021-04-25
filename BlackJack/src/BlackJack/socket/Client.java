package BlackJack.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	public static void main(String[] args) {
		try(Socket socket = new Socket("127.0.0.1",8888)){
			
			//read 스레드
			Thread rt = new Thread((new ReadThread(socket)));
			rt.setDaemon(true);
			rt.start();
			
			//write 스레드
			Thread wt = new Thread((new WriteThread(socket)));
			wt.setDaemon(true);
			wt.start();
			
			
			System.out.println("==== 서버 접속 ====");
			rt.join();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("종료");
		}
	}
}