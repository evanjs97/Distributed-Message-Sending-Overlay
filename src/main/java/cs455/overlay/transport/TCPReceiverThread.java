package cs455.overlay.transport;

import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPReceiverThread implements Runnable {

	private Socket socket;
	private DataInputStream din;

	public TCPReceiverThread(Socket socket) throws IOException {
		this.socket = socket;
		this.din = new DataInputStream(socket.getInputStream());
	}

	@Override
	public void run() {
		int dataLength;
		while(true) {
			while (socket != null) {
				try {
					dataLength = din.readInt();

					byte[] data = new byte[dataLength];
					din.readFully(data, 0, dataLength);
					EventFactory.getInstance().getEvent(data);
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
					break;
				}
			}
		}
//		try {
//			socket.close();
//		}catch(IOException ioe) {
//			System.out.println(ioe.getMessage());
//		}
	}
}
