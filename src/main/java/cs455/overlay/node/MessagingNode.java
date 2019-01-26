package cs455.overlay.node;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MessagingNode {
	private ServerSocket serverSocket;
	private String address;
	private InetAddress iAddress;
	private int port;
	private String regName;
	private int regPort;
	private Socket regSocket;


	/**
	 * MessagingNode constructor
	 * @param regName the hostname of the registry
	 * @param regPort the port of the registry to try to connect over
	 */
	public MessagingNode(String regName, int regPort) {
		openServerSocket();
		address = serverSocket.getInetAddress().getHostAddress();
		port  = serverSocket.getLocalPort();
		this.regName = regName;
		this.regPort = regPort;
		register();
		//runServerNode();
		try {
			regSocket = new Socket(regName, regPort, iAddress, port);
		}catch(IOException e) {
			System.out.println("Failed to open socket to Registry " + regName + " on port " + regPort);
		}
	}

	/**
	 * MessagingNode test constructor, allows specification of port for MessagingNode
	 * @param regName the hostname of the registry
	 * @param regPort the port of the registry to try to connect over
	 */
	public MessagingNode(String regName, int regPort, int port) {
		this.regName = regName;
		this.regPort = regPort;
		openServerSocket();
		iAddress = serverSocket.getInetAddress();
		address = iAddress.getHostAddress();
		this.port  = port;
		//runServerNode();try {
		try{
			regSocket = new Socket(regName, regPort, iAddress, port);
		}catch(IOException e) {
			System.out.println("Failed to open socket to Registry " + regName + " on port " + regPort);
			System.exit(1);
		}

		register();
		try{
			regSocket.close();
			serverSocket.close();
		}catch(IOException ioE) {
			System.out.println("Failed to close socket.");
		}

	}


	public void runServerNode() {
		while(true) {
			try{
				Socket s = serverSocket.accept();

//				Runnable connection = new NodeConnection(s);
//				new Thread(connection).start();
			}catch(Exception e) {

			}
		}
	}

	private void register() {
		System.out.println("Ready to register");
		boolean registered = false;
		while(!registered) {
			try {
				DataOutputStream out = new DataOutputStream(regSocket.getOutputStream());
				out.writeUTF("Registering node");
				out.flush();

				out.close();
				System.out.println("Sent registration message");
				registered = true;
			} catch (IOException ioE) {
				System.out.println("MessagingNode at " + address + " failed to connect to " + regName);
			}
		}
	}

	private void openServerSocket() {
			try{
				this.serverSocket = new ServerSocket(0);
				return;
			}catch(Exception e) {
			}
	}

	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("Error: Please specify a hostname and port number.");
			System.exit(1);
		}else {

			String hostname = args[0];
			try {
				int port = Integer.parseInt(args[1]);
				if(port < 1024 || port > 65535) {
					System.out.println("Error: Invalid port number, port must be in the range 1024-65535");
					System.exit(1);
				}
				MessagingNode messageNode = new MessagingNode(hostname, port,3005);
			}catch (NumberFormatException e) {
				System.out.println("Error: Port must be a valid integer.");
				System.exit(1);
			}

		}
	}
}
