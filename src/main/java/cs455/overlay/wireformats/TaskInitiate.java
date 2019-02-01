package cs455.overlay.wireformats;

import javax.xml.crypto.Data;
import java.io.*;

public class TaskInitiate  implements Event{
	private int type = 7;
	private int rounds;

	public TaskInitiate(int rounds){
		this.rounds = rounds;
	}

	public TaskInitiate(DataInputStream din) throws IOException{
		rounds = din.readInt();
	}

	public int getRounds() {
		return rounds;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledData;
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutStream));

		dout.writeInt(type);
		dout.writeInt(rounds);

		dout.flush();
		marshalledData = baOutStream.toByteArray();

		baOutStream.close();
		dout.close();
		return marshalledData;
	}
}
