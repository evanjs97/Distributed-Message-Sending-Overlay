package cs455.overlay.node;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Node {
	private TCPServerThread tcpServerThread;
	protected String address;
	protected InetAddress iAddress;
	protected int port;
	private EventFactory eventFactory;

	/**
	 * Node constructor to be inherited by MessagingNode and Registry
	 * @param port is the port to open a TCPServerThread over, pass '0' for automatic allocation
	 * @throws IOException if TCPServerThread fails to create ServerSocket
	 */
	public Node(int port) throws IOException{
		tcpServerThread = new TCPServerThread(port,this);
		address = tcpServerThread.getAddress();
		iAddress = tcpServerThread.getInetAddress();
		System.out.println("Server Thread opened on: " + address + " INET: " + iAddress);
		this.port  = tcpServerThread.getLocalPort();
		System.out.println("Node Created!");
		new Thread(tcpServerThread).start();

	}

	public abstract void onEvent(Event event) throws IOException;

}
