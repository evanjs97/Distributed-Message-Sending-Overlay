## CS455-Distributed Systems: Using Dijkstra’s Shortest Paths to Route Packets in a Network Overlay
### Author: Evan Steiner
### Purpose: Building network overlay with multi node communication using dijkstra's shortest path to route packets

#### Instructions, Compilation/Running:
* Compile on command line: "gradle build"
* run startup.sh script
* startup.sh will default to starting registry on santa-fe on port 3007, but you can modify this if you like.
* Registry will open in a large window and all the messaging nodes will open as tabs in smaller window.
* Start running registry and/or messaging node commands.
	* Presumably you should first setup overlay on registry

#### Commands:
* Registry Commands:
	* list-messaging-nodes: lists all registered messaging nodes.
	* setup-overlay {numLinks}: setup overlay of nodes with {numLinks} connections each
		where {numLinks} %2 == 0 AND 1 < {numLinks} < # of registered nodes
	* send-overlay-link-weights: send randomly generated link weights to overlay nodes for use in shortest path.
	* start {numRounds}: starts {numRounds} rounds where each messaging node sends 5 messages per round to random other messaging
			node using shortest path calculations.
	* quit: stops execution of messaging node program.
* Messaging Node Commands:
	* exit-overlay: de-registers node with registry.
	* print-shortest-path: prints shortest path to each other node.
	* quit: stops execution of messaging node program.

#### Notes:
* Registry will wait 15 seconds after receiving last task complete message before requesting statistics.
* Should run fairly fast at around 15,000 rounds or less, more rounds will take increasingly longer in time.

#### Classes/Project Structure:
* dijkstra
	* RoutingCache.java: Storing shortest path to each other node in overlay, also can print them.
	* ShortestPath.java: Calculating shortest path to each other node in overlay.
* node
	* Node.java: Abstract class inherited by Registry and MessagingNode holds general node information.
	* Registry.java: Class responsible for overseeing node overlay communications, creation of overlay, statistics etc.
	* MessagingNode.java: Class responsible for handling bulk of messages within overlay creating connections between nodes etc.
* transport
	* TCPReceiverThread.java: Class responsible for receiving incoming connections.
	* TCPServerThread.java: Class responsible for listening for incoming connections.
	* TCPSender.java: Class responsible for sending messages.
* util
	* OverlayCreator.java: Class responsible for creation of the node overlay and ensuring its validity.
	* OverlayNode.java: Class responsible for storing info about nodes in overlay.
	* OverlayEdge.java: Class responsible for storing info about edges in overlay.
	* StatisticsCollectorAndDisplay: Class responsible for collecting statistics.
* wireformats
	* Event.java: Interface containing info about events to be implemented by all messages/events in project.
	* EventFactory.java: Singleton instance for identifying and creating received events.
	* MessageNodeEvent.java: Class that handles all messaging nodes, message types, format ip,port,type
	* Register.java: Class responsible for register message to registry.
	* RegisterResponse.java: Class responsible for registry response to nodes on register message, success or fail.
	* Deregister.java: Class responsible for deregister message to registry.
	* TaskInitiate.java: Class responsible for initiating rounds from registry.
	* TaskComplete.java: Class responsible for task complete message to registry.
	* CreateLink.java: Class responsible for linking message between messaging nodes.
	* Message.java: Class responsible for messages sent between messaging nodes during rounds.
	* MessagingNodesList.java: Class responsible for registry sending of neighbors to each messaging node.
	* PullTrafficSummary.java: Class responsible for registry requesting summary of traffic from all messaging nodes.
	* TrafficSummary.java: Class responsible for messaging nodes sending traffic summaries to registry.
