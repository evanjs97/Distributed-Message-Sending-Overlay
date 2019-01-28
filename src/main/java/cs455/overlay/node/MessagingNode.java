package cs455.overlay.node;

import cs455.overlay.dijkstra.ShortestPath;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.OverlayNode;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessagingNode extends Node{


	private ConcurrentLinkedQueue<Socket> neighbors;
	private String regName;
	private int regPort;
	private ShortestPath graph;


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

	/**new
	 * MessagingNode test constructor, allows specification of port for MessagingNode
	 * @param regName the hostname of the registry
	 * @param regPort the port of the registry to try to connect over
	 */
	public MessagingNode(String regName, int regPort, int port) throws IOException {
		super(port);
		this.regName = regName;
		this.regPort = regPort;

		register();

	}

	/**
	 * register node in registry
	 * creates socket on regName and regPort
	 * creates TCPSender to send Register message to Registry
	 * @throws IOException
	 */
	private void register() throws IOException{
		System.out.println("Registering...");
		Socket socket = new Socket(regName, regPort);
		new TCPSender(socket).sendData(new Register(this.address, this.port, 0).getBytes());
		socket.close();
	}

	/**
	 * deregister sends deregister request to register
	 * @throws IOException
	 */
	private void deregister() throws IOException{
		System.out.println("De-registering...");
		Socket socket = new Socket(regName, regPort);
		new TCPSender(socket).sendData(new Register(this.address,this.port, 1).getBytes());
		socket.close();
	}

	private void neighborsOverlay(MessagingNodesList mnList) throws IOException{
		neighbors = new ConcurrentLinkedQueue<>();
		System.out.println("Num Neighbors: " + mnList.getNumConnections());
		for(OverlayNode node : mnList.getNodes()) {
			System.out.println("Neighbor Nodes: "+ node.getIp() + " " + node.getPort());
			Socket socket = new Socket(node.getIp(),node.getPort());
			neighbors.add(socket);
			System.out.println(this.address + " Establishing connection with: " + node.getIp() + " on port: " + node.getPort());
			new TCPSender(socket).sendData(new Register(this.address,this.port, 4).getBytes());
		}
		System.out.println("All connections are established. Number of connections: " + mnList.getNodes().size());
	}

	/**
	 * onEvent method accepts an event and handles it based on its type
	 * @param event a message received event
	 * @param socket the socket that the message was received over
	 * @throws IOException
	 */
	public void onEvent(Event event, Socket socket) throws IOException{
		System.out.println("Message Node Event: " + event.getType());
		switch(event.getType()) {
			case 2:
				RegisterResponse regRes = (RegisterResponse) event;
				System.out.println("Response received from register: status: " + regRes.getStatus() + ", " + regRes.getInfo());
				break;
			case 3:
				MessagingNodesList mnList = (MessagingNodesList) event;
				System.out.println("Overlay setup command received");
				neighborsOverlay(mnList);
				break;
			case 4:
				Register reg = (Register) event;
				System.out.println("Link connection established with: " + reg.getIp() + " on port: " + reg.getPort());
				neighbors.add(socket);
				break;
			case 5:
				LinkWeights lw = (LinkWeights) event;
				System.out.println("Received link overlay");
				graph = new ShortestPath(lw.getLinks());
				break;
		}
	}

	/**
	 * command handler for messaging node
	 * Valid Commands:
	 * quit: stops process and exits JVM
	 * exit-overlay: sends deregister request to register
	 * @throws IOException
	 */
	public void commandHandler() throws IOException{
		Scanner scan = new Scanner(System.in);
		while(true) {
			while(scan.hasNext()) {
				String command = scan.nextLine();
				switch(command) {
					case "quit":
						System.exit(0);
					case "exit-overlay":
						deregister();
						break;

				}
			}
		}
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
					messageNode.commandHandler();
				}else {
					MessagingNode messageNode = new MessagingNode(hostname, port);
					messageNode.commandHandler();
				}

			}catch (NumberFormatException e) {
				System.out.println("Error: Port must be a valid integer.");
				System.exit(1);
			}

		}
	}
}
