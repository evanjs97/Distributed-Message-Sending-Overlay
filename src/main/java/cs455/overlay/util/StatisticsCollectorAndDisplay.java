package cs455.overlay.util;

import cs455.overlay.wireformats.TrafficSummary;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StatisticsCollectorAndDisplay {

	private String ip;
	private int port;

	private final AtomicLong sendSummation = new AtomicLong();
	private final AtomicLong receiveSummation = new AtomicLong();
	private final AtomicInteger receiveTracker = new AtomicInteger();
	private final AtomicInteger sendTracker = new AtomicInteger();
	private final AtomicInteger relayTracker = new AtomicInteger();

	public StatisticsCollectorAndDisplay(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void receivedMessage(int received) {
		receiveTracker.addAndGet(1);
		receiveSummation.addAndGet(received);
	}

	public void sendMessage(int sent) {
		sendTracker.addAndGet(1);
		sendSummation.addAndGet(sent);
	}

	public void relayMessage() {
		relayTracker.addAndGet(1);
	}

	public TrafficSummary getTrafficSummary() {
		return new TrafficSummary(ip,port,sendTracker.get(),receiveTracker.get(),relayTracker.get(),sendSummation.get(),receiveSummation.get());
	}
}
