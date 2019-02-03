package cs455.overlay.dijkstra;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.util.OverlayEdge;
import cs455.overlay.util.OverlayNode;

import java.util.*;

public class ShortestPath {
	private RoutingCache cache = null;
	Set<OverlayNode> nodes;
	private OverlayNode start;

	public ShortestPath(LinkedList<OverlayEdge> edges, MessagingNode start) {
		this.nodes = new HashSet<>();

		for(OverlayEdge edge : edges) {
			OverlayNode node1 = edge.getEndpointFrom();
			OverlayNode node2 = edge.getEndpointTo();
			nodes.add(node1);
			nodes.add(node2);
			for(OverlayNode node : nodes) {
				if(node.equals(node1)) node1 =  node;
				if(node.equals(node2)) node2 = node;
//				if(node.equals(node1))node.addEdge(new OverlayEdge(node,node2,true,edge.getWeight()));
//				if(node.equals(node2))node.addEdge(new OverlayEdge(node,node1,true, edge.getWeight()));
				if(node.getIp().equals(start.getAddress()) && node.getPort() == start.getPort()) this.start = node;
			}
			node1.addEdge(new OverlayEdge(node1,node2,true,edge.getWeight()));
			node2.addEdge(new OverlayEdge(node2,node1,true,edge.getWeight()));


		}
		dijkstra();
		System.out.println("Start: " + this.start);
		System.out.println("Printing nodes in set:");
		for(OverlayNode node : nodes) {
			System.out.println(node);
		}
		System.out.println("Link weights are received and processed. Ready to send messages.");
	}

	public LinkedList<OverlayNode> getRandomShortestPath() {
		return cache.getRandomPath();
	}

	private void dijkstra() {
		ArrayList<OverlayNode> set = new ArrayList<>();
		//System.out.println("START NODES");
		for(OverlayNode node : nodes) {
			node.makeDijkstra();
			if(node.equals(start)) {
				node.setDistance(0);
			}
			//System.out.println("NODE:  " + node);
			set.add(node);
		}
		//System.out.println("FINISHED START NODES");
//		for(OverlayNode node : nodes) {
//			System.out.println(node);
//			for(OverlayEdge edge : node.getEdges()) {
//				System.out.println("EDGE:");
//				System.out.println(edge.getEndpointFrom());
//				System.out.println(edge.getEndpointTo());
//			}
//		}

		while(!set.isEmpty()) {
			OverlayNode node = set.get(0);
			for(OverlayNode nodeTest : set) {
				if(nodeTest.compareTo(node) < 0) node = nodeTest;
			}
			set.remove(node);

//			System.out.println("Djikstra set");
//			for(OverlayNode test : set) {
//				System.out.println(test);
//			}
//			System.out.println("Djikstra nodes");
//			for(OverlayNode test2 : nodes) {
//				System.out.println(test2);
//			}

			for(OverlayEdge edge : node.getEdges()) {
				OverlayNode edgeNode = edge.getEndpointTo();
				if(edgeNode.equals(node)) edgeNode = edge.getEndpointFrom();
				System.out.println(edgeNode);
				boolean isInSet = false;
				for(OverlayNode nodeTest : set) {
					if(edgeNode.equals(nodeTest)) isInSet = true;
				}
				if(isInSet) {
					int dist = node.getDistance() + edge.getWeight();
					System.out.println("DISTANCE: " + dist + " " + edgeNode.getDistance());
					if (dist < edgeNode.getDistance()) {
						System.out.println("New Distance");
						edgeNode.setDistance(dist);
						edgeNode.setPrev(node);
					}
				}
			}
		}
		cache = new RoutingCache(nodes,start);
		cache.print();
	}

//	public OverlayNode randomSink() {
//		return cache.getRandom();
//	}

}
