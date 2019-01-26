package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread implements Runnable{
	ServerSocket serverSocket;
	int port;

	/**
	 * TCPServerThread constructor creates new Server thread
	 * @param port to open server socket for, use '0' for automatic allocation
	 */
	public TCPServerThread(int port) {
		openServerSocket(port);
		this.port = this.serverSocket.getLocalPort();
	}

	/**
	 * openServerSocket opens ServerSocket over port
	 * @param port to open ServerSocket over, pass '0' to automatically allocate
	 */
	private void openServerSocket(int port) {
		try{
			this.serverSocket = new ServerSocket(port);
			return;
		}catch(IOException ioe) {
			System.out.println(ioe.getMessage());
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

	/**
	 * run method for thread
	 * blocks till connection made, then open TCPReceiverThread over that socket
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("Accepted Message");
				new Thread(new TCPReceiverThread(socket)).start();
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}
		}
	}
}
