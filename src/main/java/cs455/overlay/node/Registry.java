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

	private Queue<TrafficSummary> summary;

	private final AtomicInteger taskComplete = new AtomicInteger();
	private Set<String> registeredNodes;
	private AtomicInteger completedNodes = new AtomicInteger();
	private OverlayNode[] overlay;
	/**
	 * Registry constructor creates new Registry on current machine listening over specified port
	 * @param port
	 * @throws IOException
	 */
	public Registry(int port) throws IOException {
		super(port);
		summary = new LinkedList<>();
		registeredNodes = Collections.synchronizedSet(new HashSet<String>());
	}


	/**
	 * registerNode method registers (0r doesn't if register is invalid) node in Map and sends message
	 * to node that registered indicating success or failure in registration
	 * @param reg the register message
	 * @param socket the socket the register message was received over
	 * @throws IOException
	 */
	private void registerNode(Register reg, Socket socket) throws IOException{


		byte status = 0;
		int numRegistered = registeredNodes.size()+1;
		String info = "Successfully Registered! There are " + numRegistered + " currently registered.";

		if(registeredNodes.contains(reg.getIp()+":"+reg.getPort())) {
			status = 1;
			info = "Registration failed, already registered";
		}else {
			registeredNodes.add(reg.getIp() + ":" + reg.getPort());
		}
		System.out.println("Registration request successful from node on: " + reg.getIp() + ":" + reg.getPort() +". The number of messaging nodes currently constituting the overlay is " + numRegistered);
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

		if(registeredNodes.contains(dreg.getIp() + ":" + dreg.getPort()) || !dreg.getIp().equals(socket.getInetAddress().getHostAddress())) {
			Socket sender = new Socket(dreg.getIp(),dreg.getPort());
			byte status = 1;
			new TCPSender(sender).sendData(new RegisterResponse(status, "De-registration failed, not registered").getBytes());
		}else {
			registeredNodes.remove(dreg.getIp());
			System.out.println("Deregister request received from: " + dreg.getIp() +" on port: " + dreg.getPort() + ". Request Successful.");
		}
	}

	/**
	 * Lists all registered nodes in format 'ip, port'
	 */
	private void listNodes() {
		for(String address : registeredNodes) {
			System.out.println(address);
		}
	}

	/**
	 * createOverlay method sets up the overlay of connections to be made by registered nodes
	 * through use of the OverlayCreator class
	 * @param connectionCount is the max number of connections allowed
	 * @throws IOException
	 */
	private void createOverlay(int connectionCount) throws IOException{
		OverlayNode[] nodes = new OverlayNode[registeredNodes.size()];
		int index = 0;
		for(String address : registeredNodes) {
			String[] arr = address.split(":");
			nodes[index] = new OverlayNode(arr[0],Integer.parseInt(arr[1]), connectionCount);
			index++;
		}

		OverlayCreator.createOverlay(nodes, connectionCount);

		this.overlay = nodes;
		//OverlayCreator.printOverlay(this.overlay);
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

		for(String address : registeredNodes) {
			//Map.Entry tuple = (Map.Entry) nodeIter.next();
			String[] arr = address.split(":");
			new TCPSender(new Socket(arr[0], Integer.parseInt(arr[1]))).sendData(new LinkWeights(links).getBytes());
		}
		System.out.println("Sent all link weights.");
	}

	/**
	 * startRounds method will send message to each node indicating they should start sending messages to other nodes
	 * @param rounds the number of rounds of messages to send
	 * @throws IOException
	 */
	private void startRounds(int rounds) throws IOException{
		completedNodes = new AtomicInteger();
		summary.clear();
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
			while(scan.hasNextLine()) {
				String command = scan.nextLine();
				String[] split = command.split(" ");
				try {
					switch (split[0]) {

						case "quit":
							System.exit(0);
						case "list-messaging-nodes":
							listNodes();
							break;
						case "setup-overlay":
							int def = 4;
							if (def >= registeredNodes.size()) def = registeredNodes.size() - 1;
							try {
								if (split.length > 1) {
									def = Integer.parseInt(split[1]);
								} else {
									System.out.println("Defaulting to " + def + " connections");
								}
								createOverlay(def);
							} catch (Exception e) {
								System.err.println("Error: argument after setup-overlay must be an int");
							}
							break;
						case "send-overlay-link-weights":
							if (overlay == null) {
								System.err.println("Error: please setup overlay before sending links");
							} else sendLinkWeights(OverlayCreator.getEdges(overlay));
							break;
						case "start":
							if (split.length > 1) {
								try {
									startRounds(Integer.parseInt(split[1]));
								} catch (Exception e) {
									System.err.println("Error: Invalid argument to start. Should be integer");
								}
							} else {
								System.err.println("Error: Please specify an int number of rounds as argument");
							}
							break;

					}
				}catch(InputMismatchException e) {

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
	private void taskComplete(TaskComplete task) throws IOException, InterruptedException{
			if (registeredNodes.contains(task.getIp() + ":" + task.getPort())) {
				int current = completedNodes.addAndGet(1);
				System.out.println("TASKCOMPLETE: " + current);
				if (current >= registeredNodes.size()) {
					Thread.sleep(15000);
					for (String address : registeredNodes) {
						String[] arr = address.split(":");
						new TCPSender(new Socket(arr[0], Integer.parseInt(arr[1]))).sendData(new PullTrafficSummary().getBytes());
					}
				}
			}
	}

	/**
	 * pads a string to size, for use by printSummary
	 * @param s the string to pad to size
	 * @param size the size desired
	 * @return s padded to size desired
	 */
	private static String padToSize(String s, int size) {
		while(s.length() < size) {
			s+= " ";
		}
		return s;
	}

	/**
	 * prints out the summary for the completed rounds
	 */
	private void printSummary() {
		System.out.println("Node    # of messages sent  # of messages received  sum of sent messages  sum of received messages  # of messages relayed");
		int current = 1;
		int sendTrack = 0;
		int receiveTrack = 0;
		int relaytrack = 0;
		long sendSum = 0;
		long receiveSum = 0;
		for(TrafficSummary sum : summary) {
			sendTrack += sum.getSendTracker(); receiveTrack += sum.getReceiveTracker(); relaytrack += sum.getRelayTracker();
			sendSum += sum.getSendSummation(); receiveSum += sum.getReceiveSummation();
			System.out.printf("Node %s%s%s%s%s%s\n",padToSize(""+current,3), padToSize(""+sum.getSendTracker(),20),
					padToSize(""+sum.getReceiveTracker(),23),padToSize(""+sum.getSendSummation(),22),
					padToSize(""+sum.getReceiveSummation(),26), padToSize(""+sum.getRelayTracker(),21));
			//System.out.println("+----------+-------------+-----------------------+-----------------------+-----------+");
			current++;
		}
		System.out.printf("Sum     %s%s%s%s%s\n", padToSize(""+sendTrack,20),padToSize(""+receiveTrack,23),
				padToSize(""+sendSum,22), padToSize(""+receiveSum,26), padToSize(""+relaytrack,21));
		//System.out.println("+----------+-------------+-----------------------+-----------------------+-----------+");
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
				try {
					taskComplete((TaskComplete) event);
				}catch (InterruptedException e) {
					System.out.println(e);
				}
				//System.out.println("Received Task Complete from node at: " +  taskComplete.getIp() + ":" + taskComplete.getPort());
				break;
			case 10:
				TrafficSummary traffic = (TrafficSummary) event;


				synchronized (summary) {
					summary.add(traffic);
					if (summary.size() == registeredNodes.size()) printSummary();
				}
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
