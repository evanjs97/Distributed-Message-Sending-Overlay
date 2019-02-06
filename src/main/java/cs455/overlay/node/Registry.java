package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.util.OverlayEdge;
import cs455.overlay.util.OverlayNode;
import cs455.overlay.wireformats.*;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class Registry extends Node{

	private ConcurrentLinkedQueue<TrafficSummary> summary;


	private Map<String, Integer> registeredNodes;
	private final AtomicInteger completedNodes = new AtomicInteger();
	private OverlayNode[] overlay;
	/**
	 * Registry constructor creates new Registry on current machine listening over specified port
	 * @param port
	 * @throws IOException
	 */
	public Registry(int port) throws IOException {
		super(port);
		summary = new ConcurrentLinkedQueue<>();
		registeredNodes = Collections.synchronizedMap(new HashMap<String, Integer>());
	}


	/**
	 * registerNode method registers (0r doesn't if register is invalid) node in Map and sends message
	 * to node that registered indicating success or failure in registration
	 * @param reg the register message
	 * @param socket the socket the register message was received over
	 * @throws IOException
	 */
	private void registerNode(Register reg, Socket socket) throws IOException{
		System.out.println("Register request received from: " + reg.getIp() +" on port: " + reg.getPort());
		byte status = 0;
		String info = "Successfully Registered!";

		Integer port = registeredNodes.get(reg.getIp());
		if(port != null || !reg.getIp().equals(socket.getInetAddress().getHostAddress())) {
			status = 1;
			info = "Registration failed, already registered";
		}else {
			registeredNodes.put(reg.getIp(),reg.getPort());
		}

		Socket sender = new Socket(reg.getIp(),reg.getPort());
		new TCPSender(sender).sendData(new RegisterResponse(status, info).getBytes());
	}

	/**
	 * deregister's node (removes from map), if node is registered and ip is valid
	 * @param dreg the node to deregister
	 * @param socket the socket the dreg request was received over
	 * @throws IOException
	 */
	private void deregisterNode(Deregister dreg, Socket socket) throws IOException {
		System.out.println("Deregister request received from: " + dreg.getIp() +" on port: " + dreg.getPort());
		Integer port = registeredNodes.get(dreg.getIp());
		if(port == null || !dreg.getIp().equals(socket.getInetAddress().getHostAddress())) {
			Socket sender = new Socket(dreg.getIp(),dreg.getPort());
			byte status = 1;
			new TCPSender(sender).sendData(new RegisterResponse(status, "De-registration failed, not registered").getBytes());
		}else {
			registeredNodes.remove(dreg.getIp());
		}
	}

	/**
	 * Lists all registered nodes in format 'ip, port'
	 */
	private void listNodes() {
		Iterator nodeIter = registeredNodes.entrySet().iterator();
		while(nodeIter.hasNext()) {
			Map.Entry tuple = (Map.Entry) nodeIter.next();
			System.out.println(tuple.getKey() + ", " + tuple.getValue());
		}
	}

	/**
	 * createOverlay method sets up the overlay of connections to be made by registered nodes
	 * through use of the OverlayCreator class
	 * @param connectionCount is the max number of connections allowed
	 * @throws IOException
	 */
	private void createOverlay(int connectionCount) throws IOException{
		Iterator nodeIter = registeredNodes.entrySet().iterator();
		OverlayNode[] nodes = new OverlayNode[registeredNodes.size()];
		int index = 0;
		while(nodeIter.hasNext()) {
			Map.Entry tuple = (Map.Entry) nodeIter.next();
			nodes[index] = new OverlayNode(tuple.getKey().toString(),(Integer)tuple.getValue(), connectionCount);
			index++;
		}
		OverlayCreator.createOverlay(nodes, connectionCount);
		this.overlay = nodes;
		OverlayCreator.printOverlay(this.overlay);
		sendOverlay();
	}

	/**
	 * sendOverlay will send a list of nodes each node should connect to, each node will have same # of connections
	 * @throws IOException
	 */
	private void sendOverlay() throws IOException{
		for(OverlayNode oNode : overlay) {
			new TCPSender(new Socket(oNode.getIp(),oNode.getPort())).sendData(new MessagingNodesList(oNode.getEdges()).getBytes());
		}
	}

	/**
	 * sendLinkWeights will send each registered node the linkWeights of each connection in the overlay
	 * @param links the link edges to send (don't send same edge twice)
	 * @throws IOException
	 */
	private void sendLinkWeights(LinkedList<OverlayEdge> links) throws IOException{
		Iterator nodeIter = registeredNodes.entrySet().iterator();
		while(nodeIter.hasNext()) {
			Map.Entry tuple = (Map.Entry) nodeIter.next();
			new TCPSender(new Socket(tuple.getKey().toString(), (Integer) tuple.getValue())).sendData(new LinkWeights(links).getBytes());
		}
		System.out.println("Sent all link weights.");
	}

	/**
	 * startRounds method will send message to each node indicating they should start sending messages to other nodes
	 * @param rounds the number of rounds of messages to send
	 * @throws IOException
	 */
	private void startRounds(int rounds) throws IOException{
		for(OverlayNode oNode : overlay) {
			new TCPSender(new Socket(oNode.getIp(), oNode.getPort())).sendData(new TaskInitiate(rounds).getBytes());
		}
		System.out.println("Starting Rounds.");
	}


	/**
	 * handles commands for the registry
	 * Valid Commands:
	 * quit: stops process and exits JVM
	 * list-messaging-nodes: lists all registered nodes in format 'ip, port'
	 * @throws IOException
	 */
	public void commandHandler() throws IOException{
		Scanner scan = new Scanner(System.in);
		while(true) {
			while(scan.hasNext()) {
				String command = scan.next();
				switch(command) {
					case "quit":
						System.exit(0);
					case "list-messaging-nodes":
						listNodes();
						break;
					case "setup-overlay":
						//System.out.println("TEST: " + scan.next());
						createOverlay(scan.nextInt());
						scan.nextLine();
						break;
					case "send-overlay-link-weights":
						sendLinkWeights(OverlayCreator.getEdges(overlay));
						break;
					case "start":
						startRounds(scan.nextInt());
						break;
				}
			}
		}
	}

	/**
	 * taskComplete increments task complete counter if sender node is registered
	 * then sends PullTrafficSummary request if counter is equal to registered.size()
	 * @param task is the task complete message with info about which node sent it
	 * @throws IOException
	 */
	private void taskComplete(TaskComplete task) throws IOException{
		if(registeredNodes.containsKey(task.getIp()+":"+task.getPort())) {
			int current = completedNodes.addAndGet(1);
			if(current >= registeredNodes.size()) {
				Iterator nodeIter = registeredNodes.entrySet().iterator();
				while(nodeIter.hasNext()) {
					Map.Entry tuple = (Map.Entry) nodeIter.next();
					new TCPSender(new Socket(tuple.getKey().toString(), (Integer) tuple.getValue())).sendData(new PullTrafficSummary().getBytes());
				}
			}
		}
	}

	private void printSummary() {
		System.out.println("+----------+-------------+-----------------------+-----------------------+-----------+");
		System.out.println("|  Number  |  Number of  |   Summation of sent   | Summation of received | Number of |");
		System.out.println("|    of    |  messages   |       messages        |       messages        | messages  |");
		System.out.println("| messages |  received   |                       |                       | relayed   |");
		System.out.println("|   sent   |                                                                         |");
		System.out.println("+----------+-------------+-----------------------+-----------------------+-----------+");
		int current = 1;
		int sendTrack = 0;
		int receiveTrack = 0;
		int relaytrack = 0;
		long sendSum = 0;
		long receiveSum = 0;
		for(TrafficSummary sum : summary) {
			sendTrack += sum.getSendTracker(); receiveTrack += sum.getReceiveTracker(); relaytrack += sum.getRelayTracker();
			sendSum += sum.getSendSummation(); receiveSum += sum.getReceiveSummation();
			System.out.printf("| Node %d  | %d | %d | %d | %d | %d |",current, sum.getSendTracker(),
					sum.getReceiveTracker(),sum.getSendSummation(), sum.getReceiveSummation(), sum.getRelayTracker());
			System.out.println("+----------+-------------+-----------------------+-----------------------+-----------+");
		}
		System.out.printf("|   Sum   | %d | %d | %d | %d | %d |", sendTrack,receiveTrack, sendSum, receiveSum, relaytrack);
		System.out.println("+----------+-------------+-----------------------+-----------------------+-----------+");
	}


	/**
	 * onEvent method accepts an event and handles it based on its type
	 * @param event a message received event
	 * @param socket the socket that the message was received over
	 * @throws IOException
	 */
	public void onEvent(Event event, Socket socket) throws IOException{
		switch (event.getType()) {
			case 0:
				Register reg = (Register) event;
				registerNode(reg, socket);
				break;
			case 1:
				Deregister dreg = (Deregister) event;
				deregisterNode(dreg, socket);
				break;
			case 8:
				taskComplete((TaskComplete) event);
				break;
			case 10:
				TrafficSummary traffic = (TrafficSummary) event;
				summary.add(traffic);
				if(summary.size() == registeredNodes.size()) printSummary();
				break;


		}
	}

	/**
	 * main method for Registry class error checks and creates Registry
	 * Registry will be created on current machine listening over specified port
	 * @param args [0] = port to open registry on must be between 1024-65535
	 *             if port not open Error will be thrown.
	 */
	public static void main(String[] args) throws IOException{
		try {
			int port = Integer.parseInt(args[0]);
			if(port < 1024 || port > 65535) {
				System.out.println("Error: Invalid port number, port must be in the range 1024-65535");
				System.exit(1);
			}
			System.out.println("Creating Registry!");
			Registry registry = new Registry(port);
			//new TCPServerThread(port,registry);
			registry.commandHandler();

		}catch (NumberFormatException e) {
			System.out.println("Error: Port must be a valid integer.");
			System.exit(1);
		}

	}
}
