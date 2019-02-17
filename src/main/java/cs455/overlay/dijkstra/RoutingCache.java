package cs455.overlay.dijkstra;

import cs455.overlay.util.OverlayNode;
import cs455.overlay.wireformats.LinkWeights;

import java.util.*;

public class RoutingCache {

	private final HashMap<String, LinkedList<OverlayNode>> cache;
	private final HashMap<String, Integer> weights;

	/**
	 * RoutingCache constructor caches shortest paths found by dijkstra
	 * @param nodes the list of nodes to cache paths to
	 * @param start the node in the list not to find path to
	 */
	public RoutingCache(Set<OverlayNode> nodes, OverlayNode start, HashMap<String, Integer> weights) {
		HashMap<String, LinkedList<OverlayNode>> cache = new HashMap<>();
		for(OverlayNode node : nodes) {
			if(!node.equals(start)) {
				LinkedList<OverlayNode> path = new LinkedList<>();
				OverlayNode prev = node;
				while(!prev.equals(start)) {
					path.addFirst(prev);
					prev = prev.getPrev();
				}
				String key = node.getIp() + ":" + node.getPort();
				cache.put(key,path);
			}
		}
		this.cache = cache;
		this.weights = weights;
	}

	/**
	 * print out all paths in the cache
	 */
	public String toString() {
		Iterator cacheIter = cache.entrySet().iterator();
		String toReturn = "";
		while(cacheIter.hasNext()) {
			Map.Entry tuple = (Map.Entry) cacheIter.next();
			LinkedList<OverlayNode> temp = (LinkedList<OverlayNode>) tuple.getValue();
			for(int i = 0; i < temp.size(); i++) {
				OverlayNode nodeFrom = temp.get(i);

				if(i == temp.size()-1) {
					toReturn += nodeFrom.getIp()+":"+nodeFrom.getPort() +"\n";
				}else {
					OverlayNode nodeTo = temp.get(i+1);
					int weight = weights.get(nodeFrom.getIp()+":"+nodeFrom.getPort()+"::"+nodeTo.getIp()+":"+nodeTo.getPort());
					toReturn += nodeFrom.getIp() + ":" + nodeFrom.getPort()+"--"+weight+"--";
				}
			}

		}
		return toReturn;
	}

	/**
	 * find random path from the cache
	 * @return the random path gotten from the cache
	 */
	public LinkedList<OverlayNode> getRandomPath() {
		Random rand = new Random();
		Object[] entries = cache.entrySet().toArray();
		Map.Entry tuple = (Map.Entry) entries[rand.nextInt(entries.length)];
		LinkedList<OverlayNode> copy = new LinkedList<>((LinkedList<OverlayNode>) tuple.getValue());
		return copy;
	}

}
