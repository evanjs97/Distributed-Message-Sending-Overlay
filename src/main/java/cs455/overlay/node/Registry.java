package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Register;
import cs455.overlay.wireformats.RegisterResponse;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Registry extends Node{

	/**
	 * Registry constructor creates new Registry on current machine listening over specified port
	 * @param port
	 * @throws IOException
	 */
	public Registry(int port) throws IOException {
		super(port);
	}

	private void registerNode(Register reg) throws IOException{
		System.out.println("Register Message Recieved from: " + reg.getIp() +" on port: " + reg.getPort());
		//System.out.println("SOCKET INFO: " + socket.getInetAddress().getHostAddress());
		byte b = 0;
		Socket sender = new Socket(reg.getIp(),reg.getPort());
		new TCPSender(sender).sendData(new RegisterResponse(b, "Successfully Registered!").getBytes());
	}

	public void onEvent(Event event) throws IOException{
		switch (event.getType()) {
			case 0:
				Register reg = (Register) event;
				registerNode(reg);
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

//			boolean wait = true;
//			Scanner scanner = new Scanner(System.in);
//			while(wait) {
//				if(scanner.hasNext()) {
//					String next = scanner.next();
//					if(next.equals("quit")) {
//						break;
//					}
//				}
//			}
		}catch (NumberFormatException e) {
			System.out.println("Error: Port must be a valid integer.");
			System.exit(1);
		}

	}
}
