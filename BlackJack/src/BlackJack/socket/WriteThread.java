package BlackJack.socket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;

public class WriteThread extends Thread{
	private Socket socket;

	public WriteThread(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"))){
			Scanner sc = new Scanner(System.in);
			while(true) {
				bw.write(sc.nextLine());
				bw.newLine();
				bw.flush();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}