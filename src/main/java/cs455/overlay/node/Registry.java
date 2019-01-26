package cs455.overlay.node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Registry {
	private ServerSocket serverSocket;
	private int port;

	public Registry(int port) {
		this.port = port;
		try {
			this.serverSocket = new ServerSocket(port);
		}catch(IOException ioE) {
			System.out.println("Failed to open server socket on port " + port + " . Please specify an open port.");
		}
		runServerSocket();
		try {
			serverSocket.close();
		}catch(IOException ioE) {
			System.out.println("Failed to close socket.");
		}
	}

	private void runServerSocket() {
		System.out.println("Listening on port " + port);
		while(true) {
			try {
				System.out.println("Listening...");
				Socket s = serverSocket.accept();
				handleRequest(s);
				s.close();
			}catch(Exception e){}
		}
	}

	private void handleRequest(Socket s) {
		int socketPort  = s.getLocalPort();
		String socketAddress = s.getInetAddress().getHostAddress();
		try {
			DataInputStream in = new DataInputStream(s.getInputStream());
//			ObjectInputStream objectStream = new ObjectInputStream(s.getInputStream());
//			NodeRequest message = objectStream.readObject();
//			while(true) {
//				String value = in.readUTF();
//				System.out.println(value);
//			}
//			System.out.println("Created data Stream");
//			String input = in.readUTF();
			System.out.println(in.readUTF());
//			System.out.println(input);
		}catch(Exception e) {
			System.out.println(e);
			System.out.println("Registry failed to handle output stream from " + socketAddress + " on port " + socketPort + ".");
		}
	}

//	private void registerNode(NodeRequest message) {
//
//	}

//	private void openServerSocket() {
//		for(int i = 1024; i < 65536; i++) {
//			try{
//				this.serverSocket = new ServerSocket(i);
//				return;
//			}catch(Exception e) {
//				continue;
//			}
//		}
//	}

	public static void main(String[] args) {
		try {
			int port = Integer.parseInt(args[0]);
			if(port < 1024 || port > 65535) {
				System.out.println("Error: Invalid port number, port must be in the range 1024-65535");
				System.exit(1);
			}
			Registry registry = new Registry(port);
		}catch (NumberFormatException e) {
			System.out.println("Error: Port must be a valid integer.");
			System.exit(1);
		}

	}
}
