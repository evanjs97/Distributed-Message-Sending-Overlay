package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Register;
import cs455.overlay.wireformats.RegisterResponse;

import java.io.*;
import java.net.Socket;
import java.util.*;


public class Registry extends Node{

	private Map<String, Integer> registeredNodes;
	/**
	 * Registry constructor creates new Registry on current machine listening over specified port
	 * @param port
	 * @throws IOException
	 */
	public Registry(int port) throws IOException {
		super(port);
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
	private void deregisterNode(Register dreg, Socket socket) throws IOException {
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
	public void listNodes() {
		Iterator nodeIter = registeredNodes.entrySet().iterator();
		while(nodeIter.hasNext()) {
			Map.Entry tuple = (Map.Entry) nodeIter.next();
			System.out.println(tuple.getKey() + ", " + tuple.getValue());
		}
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

						break;

				}
			}
		}
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
				Register dreg = (Register) event;
				deregisterNode(dreg, socket);
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
			registry.commandHandler();

		}catch (NumberFormatException e) {
			System.out.println("Error: Port must be a valid integer.");
			System.exit(1);
		}

	}
}
