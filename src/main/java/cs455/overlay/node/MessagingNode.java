package cs455.overlay.node;

import cs455.overlay.dijkstra.ShortestPath;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.OverlayNode;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessagingNode extends Node{

	private HashMap<String, TCPSender> neighbors = new HashMap<>();
	private String regName;
	private int regPort;
	private ShortestPath graph;
	private StatisticsCollectorAndDisplay statistics;


	/**
	 * MessagingNode constructor
	 * @param regName the hostname of the registry
	 * @param regPort the port of the registry to try to connect over
	 */
	public MessagingNode(String regName, int regPort) throws IOException{
		super(0);

		this.regName = regName;
		this.regPort = regPort;
		statistics = new StatisticsCollectorAndDisplay(address, port);

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
		System.out.println("Registering with registry host on: " + regName + ":" + regPort);
		Socket socket = new Socket(regName, regPort);
		new TCPSender(socket).sendData(new Register(this.address, this.port).getBytes());
		socket.close();
	}

	/**
	 * deregister sends deregister request to register
	 * @throws IOException
	 */
	private void deregister() throws IOException{
		System.out.println("De-registering...");
		Socket socket = new Socket(regName, regPort);
		new TCPSender(socket).sendData(new Deregister(this.address,this.port).getBytes());
		socket.close();
	}

	/**
	 * neighborsOverlay establishes connections to all nodes the register tells it to
	 * @param mnList the list of nodes to connect to
	 * @throws IOException
	 */
	private void neighborsOverlay(MessagingNodesList mnList) throws IOException{
		for(OverlayNode node : mnList.getNodes()) {
			Socket socket = new Socket(node.getIp(),node.getPort());
			TCPSender sender = new TCPSender(socket);
			synchronized (neighbors) {
				neighbors.put(node.getIp() + ":" + node.getPort(), sender);
			}
			new Thread(new TCPReceiverThread(socket,this)).start();
			System.out.println(this.address + ": Establishing connection with: " + node.getIp() + " on port: " + node.getPort());
			sender.sendData(new CreateLink(this.address,this.port).getBytes());
		}
		System.out.println("All connections are established. Number of connections: " + mnList.getNodes().size());
	}

	/**
	 * startRounds starts sending messages to other nodes in the overlay based off of dijkstra's shortest path
	 * @param rounds the number of messages to send
	 * @throws IOException
	 */
	private void startRounds(int rounds) throws IOException{

		for(int i = 0; i < rounds; i++) {
			LinkedList<OverlayNode> path = graph.getRandomShortestPath();
			OverlayNode dest = path.pollFirst();
			TCPSender sender = neighbors.get(dest.getIp() + ":" + dest.getPort());
//			if(sender != null) {
				for (int j = 0; j < 5; j++) {
					Message msg = new Message(path);
					synchronized (sender) {
						sender.sendData(msg.getBytes());
					}
					statistics.sendMessage(msg.getPayload());
				}
//			}else i--;


		}
		System.out.println("FINISHED ROUNDS");
		new TCPSender(new Socket(regName,regPort)).sendData(new TaskComplete(address,port).getBytes());
	}

	/**
	 * relays message from sender node to node specified in message
	 * @param msg the message that specifies the path for the message
	 * @throws IOException
	 */
	private void relayMessage(Message msg) throws IOException{
		OverlayNode dest = msg.nextDest();
			TCPSender sender = neighbors.get(dest.getIp() + ":" + dest.getPort());
			sender.sendData(msg.getBytes());
		statistics.relayMessage();
	}

	/**
	 * onEvent method accepts an event and handles it based on its type
	 * @param event a message received event
	 * @param socket the socket that the message was received over
	 * @throws IOException
	 */
	public void onEvent(Event event, Socket socket) throws IOException{
		switch(event.getType()) {
			case 2:
				RegisterResponse regRes = (RegisterResponse) event;
				System.out.println("Registration response received from register: status: " + regRes.getStatus() + ", " + regRes.getInfo());
				break;
			case 3:
				MessagingNodesList mnList = (MessagingNodesList) event;
				System.out.println("Neighbor overlay received from registry. Establishing connections...");
				neighborsOverlay(mnList);
				break;
			case 4:
				CreateLink link = (CreateLink) event;
				System.out.println("Link establish request received and approved from: " + link.getIp() + " on port: " + link.getPort());
				synchronized (neighbors) {
					neighbors.put(link.getIp() + ":" + link.getPort(), new TCPSender(socket));
				}
				break;
			case 5:
				LinkWeights lw = (LinkWeights) event;
				System.out.println("Received link overlay from registry.");
				graph = new ShortestPath(lw.getLinks(), this);
				break;
			case 6:
				Message msg = (Message) event;
				if(msg.relay())  {
					relayMessage(msg);
				} else {
					statistics.receivedMessage(msg.getPayload());
				}
				break;
			case 7:
				TaskInitiate task = (TaskInitiate) event;
				System.out.println("Starting " + task.getRounds() + " rounds. 5 messages will be sent each round.");
				startRounds(task.getRounds());
				break;
			case 9:
				new TCPSender(new Socket(regName,regPort)).sendData(statistics.getTrafficSummary().getBytes());
				statistics = new StatisticsCollectorAndDisplay(address,port);
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
			while(scan.hasNextLine()) {
				String command = scan.nextLine();
				switch(command) {
					case "quit":
						System.exit(0);
					case "exit-overlay":
						deregister();
						break;
					case "print-shortest-path":
						System.out.println(graph);
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
