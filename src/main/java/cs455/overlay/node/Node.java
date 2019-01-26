package cs455.overlay.node;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.EventFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Node {
	private TCPServerThread tcpServerThread;
	protected String address;
	protected InetAddress iAddress;
	protected int port;
	private EventFactory eventFactory;

	public Node(int port) throws IOException{
		tcpServerThread = new TCPServerThread(port);
		address = tcpServerThread.getAddress();
		iAddress = tcpServerThread.getInetAddress();
		this.port  = tcpServerThread.getLocalPort();
	}

}
