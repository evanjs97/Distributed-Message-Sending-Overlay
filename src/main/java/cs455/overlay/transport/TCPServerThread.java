package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread implements Runnable{
	ServerSocket serverSocket;
	int port;


	public TCPServerThread(int port) {
		openServerSocket(port);
		this.serverSocket.getLocalPort();
	}

	private void openServerSocket(int port) {
		try{
			this.serverSocket = new ServerSocket(port);
			return;
		}catch(Exception e) {
		}
	}

	public InetAddress getInetAddress() {
		return serverSocket.getInetAddress();
	}

	public String getAddress() {
		return serverSocket.getInetAddress().getHostAddress();
	}

	public int getLocalPort() {
		return serverSocket.getLocalPort();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				new TCPReceiverThread(socket).run();
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}
		}
	}
}
