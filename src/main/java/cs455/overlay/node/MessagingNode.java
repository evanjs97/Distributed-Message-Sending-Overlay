package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Register;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MessagingNode extends Node{



	private String regName;
	private int regPort;


	/**
	 * MessagingNode constructor
	 * @param regName the hostname of the registry
	 * @param regPort the port of the registry to try to connect over
	 */
	public MessagingNode(String regName, int regPort) throws IOException{
		super(0);

		this.regName = regName;
		this.regPort = regPort;

		register();

	}

	/**
	 * MessagingNode test constructor, allows specification of port for MessagingNode
	 * @param regName the hostname of the registry
	 * @param regPort the port of the registry to try to connect over
	 */
	public MessagingNode(String regName, int regPort, int port) throws IOException {
		super(port);
		this.regName = regName;
		this.regPort = regPort;

		register();
//		try{
//			regSocket = new Socket(regName, regPort, iAddress, port);
//		}catch(IOException e) {
//			System.out.println("Failed to open socket to Registry " + regName + " on port " + regPort);
//			System.exit(1);
//		}


//		try{
//			regSocket.close();
//			serverSocket.close();
//		}catch(IOException ioE) {
//			System.out.println("Failed to close socket.");
//		}

	}

	/**
	 * register node in registry
	 * creates socket on regName and regPort
	 * creates TCPSender to send Register message to Registry
	 * @throws IOException
	 */
	private void register() throws IOException{
		System.out.println("Ready to register");
		Socket socket = new Socket(regName, regPort);
		new TCPSender(socket).sendData(new Register(this.address, this.port).getBytes());
		socket.close();
	}

	public void onEvent(Event event) throws IOException{

	}


	/**
	 * main method for MessagingNode mostly does error checking and starts MessagingNode
	 * @param args [0] = hostname of registry, [1] = port that registry is listening on
	 *             if [2] is specified node will open over that port specified by [2]
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
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
				if(args.length > 2) {
					MessagingNode messageNode = new MessagingNode(hostname, port, Integer.parseInt(args[2]));
				}else {
					MessagingNode messageNode = new MessagingNode(hostname, port);
				}
			}catch (NumberFormatException e) {
				System.out.println("Error: Port must be a valid integer.");
				System.exit(1);
			}

		}
	}
}
