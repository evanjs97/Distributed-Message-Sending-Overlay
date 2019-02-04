package cs455.overlay.dijkstra;

import cs455.overlay.util.OverlayEdge;
import cs455.overlay.util.OverlayNode;

import java.util.*;

public class RoutingCache {

	private HashMap<String, LinkedList<OverlayNode>> cache;

	/**
	 * RoutingCache constructor caches shortest paths found by dijkstra
	 * @param nodes the list of nodes to cache paths to
	 * @param start the node in the list not to find path to
	 */
	public RoutingCache(Set<OverlayNode> nodes, OverlayNode start) {
		cache = new HashMap<>();
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
	}

	/**
	 * print out all paths in the cache
	 */
	public void print() {
		Iterator cacheIter = cache.entrySet().iterator();
		System.out.println("\n\n");
		while(cacheIter.hasNext()) {
			Map.Entry tuple = (Map.Entry) cacheIter.next();
			System.out.println(tuple.getKey() + ":  " + tuple.getValue());
		}System.out.println("\n\n");

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
		//return cache.get(key);
	}

}
