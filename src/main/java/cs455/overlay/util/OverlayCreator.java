package cs455.overlay.util;

import java.util.LinkedList;

public class OverlayCreator {

	/**
	 * starts the chain of processes that spawn a new overlay
	 * @param nodes the node list to create an overlay from
	 * @param connectionCount the exact number of connections per node
	 */
	public static void createOverlay(OverlayNode[] nodes, int connectionCount) {
		if(nodes.length <= connectionCount || (nodes.length * connectionCount) % 2 != 0 || (connectionCount == 1 && nodes.length > 2)) {
			System.out.println(nodes.length + " " + connectionCount);
		}else {
			setupOverlay(nodes, connectionCount);
			printOverlay(nodes);
		}
	}

	/**
	 * prints out the overlay of this class
	 */
	public static void printOverlay(OverlayNode[] nodes) {
		System.out.println("Printing overlay");
		for(OverlayNode node : nodes) {
			System.out.println(node);
		}
	}

	public static LinkedList<OverlayEdge> getEdges(OverlayNode[] nodes) {
		LinkedList<OverlayEdge> edgeWeights = new LinkedList<>();
		for(OverlayNode node : nodes) {
			for(OverlayEdge edge : node.getEdges()) {
				if(edge.getSend()) {
					edgeWeights.add(edge);
				}
			}
		}
		return edgeWeights;
	}

	public static int randomWeight() {
		return (int) (10 * Math.random());
	}

	/**
	 * setup overlay so that there are no partitions
	 * start by setting each node to have connection to successor and predecessor
	 * then continue from there
	 * @param nodes the array of nodes to create connections between
	 * @param connectionCount the exact number of connections each node must have
	 */
	private static void setupOverlay(OverlayNode[] nodes, int connectionCount) {
		int weight;
		System.out.println("Setting up overlay");
		for(int i = 0; i < nodes.length; i++) {
			System.out.println(i);
			if(i == nodes.length-1) {

				weight = randomWeight();
				nodes[i].addEdge(nodes[0],true, weight);
				nodes[0].addEdge(nodes[i],false, weight);
			}else {
				weight = randomWeight();
				nodes[i].addEdge(nodes[i + 1],true, weight);
				nodes[i + 1].addEdge(nodes[i],false, weight);
			}
		}
		System.out.println("INITIAL SETUP OF OVERLAY DONE");
		int currCount;
		int index = 2;
		for(int i = 0; i < nodes.length; i++) {
			currCount = nodes[i].getCount();
			//System.out.println(i + " " + currCount);
			while(currCount < connectionCount) {
				int temp = (i + index) % nodes.length;
				if(!nodes[temp].isFull()) {
					weight = randomWeight();
					nodes[i].addEdge(nodes[temp],true, weight);
					nodes[temp].addEdge(nodes[i],false, weight);
					index++;
					currCount++;
				}
			}
			index = 2;
		}


	}
}
