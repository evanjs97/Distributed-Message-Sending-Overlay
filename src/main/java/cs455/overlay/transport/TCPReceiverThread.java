package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPReceiverThread implements Runnable {

	private Socket socket;
	private DataInputStream din;
	private Node node;

	/**
	 * TCPReceiverThread creates new receiver thread instance
	 * @param socket the socket to receive messages over
	 * @throws IOException
	 */
	public TCPReceiverThread(Socket socket, Node node) throws IOException {
		this.node = node;
		this.socket = socket;
		this.din = new DataInputStream(socket.getInputStream());
	}

	/**
	 * run method
	 * reads from socket while not null
	 * send event to EventFactory after reading
	 */
	@Override
	public void run() {
		int dataLength;
		while(true) {
			//while (socket != null) {
				try {
					dataLength = din.readInt();

					byte[] data = new byte[dataLength];
					din.readFully(data, 0, dataLength);

					Event event = EventFactory.getInstance().getEvent(data);
					node.onEvent(event, socket);
					//break;
				} catch (IOException ioe) {
					//				System.out.println(ioe.getMessage() + " " + ioe.getLocalizedMessage());
					//				break;
				}
			//}
		}
	}
}
