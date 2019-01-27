package cs455.overlay.util;

public class OverlayCreator {
	//private OverlayNode[] nodes;

//	/**
//	 * sets this class with the specified overlay
//	 * @param nodes the overlay to set
//	 */
//	private void set(OverlayNode[] nodes) {
//		this.nodes = nodes;
//	}

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
		}
	}

	/**
	 * prints out the overlay of this class
	 */
	public static void printOverlay(OverlayNode[] nodes) {
		for(OverlayNode node : nodes) {
			System.out.println(node);
		}
	}

	/**
	 * clear the overlay specified
	 * @param nodes the overlay to clear
	 */
	public static void clearOverlay(OverlayNode[] nodes) {
		for(OverlayNode node : nodes) {
			node.clear();
		}
	}

	/**
	 * setup overlay so that there are no partitions
	 * start by setting each node to have connection to successor and predecessor
	 * then continue from there
	 * @param nodes the array of nodes to create connections between
	 * @param connectionCount the exact number of connections each node must have
	 */
	private static void setupOverlay(OverlayNode[] nodes, int connectionCount) {
		System.out.println("Setting up overlay");
		for(int i = 0; i < nodes.length; i++) {
			if(i == nodes.length-1) {
				nodes[i].addConnection(nodes[0]);
				nodes[0].addConnection(nodes[i]);
			}else {
				nodes[i].addConnection(nodes[i + 1]);
				nodes[i + 1].addConnection(nodes[i]);
			}
		}
		int currCount;
		int index = 2;
		for(int i = 0; i < nodes.length; i++) {
			currCount = nodes[i].getCount();
			while(currCount < connectionCount) {
				int temp = (i + index) % nodes.length;
				if(!nodes[temp].isFull()) {
					nodes[i].addConnection(nodes[temp]);
					nodes[temp].addConnection(nodes[i]);
					index++;
					currCount++;
				}
			}
			index = 2;
		}

	}
}
